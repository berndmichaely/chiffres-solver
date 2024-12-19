/*
 * Copyright (C) 2024 Bernd Michaely (info@bernd-michaely.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.bernd_michaely.chiffres.common.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A lightweight Collection which provides an efficient concatenation for two
 * instances of this type. At the time of this writing {@link Collection}s
 * provide concatenation only through
 * {@link Collection#addAll(Collection) Collection.addAll(collection)} in
 * {@code  O(n)} time. Also note that
 * {@link java.util.stream.Stream#concat(java.util.stream.Stream, java.util.stream.Stream) Stream.concat(Stream, Stream)}
 * discourages repeated concatenation. The Collection is useful e.g. for
 * accumulating partial results in the combine phase of
 * {@link java.util.concurrent.RecursiveTask RecursiveTask}s, when subtasks
 * return a collection of elements which are to be combined into one flat
 * collection.<p>
 * The characteristics of this implementation include the following:
 * <ul>
 * <li>It provides {@code  O(1)} time concatenation.</li>
 * <li>It supports {@code  null} values.</li>
 * <li>It is ordered (e.g. {@link #iterator() iterator()} returns the elements
 * in the order they were added).</li>
 * <li>It is not synchronized.</li>
 * <li>It is {@link Cloneable}.</li>
 * <li>The iterator supports the {@link Iterator#remove()} operation in
 * {@code  O(1)} time.</li>
 * <li>The {@link  Spliterator} is optimized for lower temporary memory usage
 * compared to the default Spliterator. It is
 * {@link Spliterator#ORDERED} | {@link Spliterator#SIZED} | {@link Spliterator#SUBSIZED}.
 * </li>
 * </ul>
 *
 * @author Bernd Michaely
 * @param <T> the type of the collection elements
 */
public class ConcatCollection<T> extends AbstractCollection<T> implements Cloneable, Serializable
{
	private static final Logger logger = Logger.getLogger(ConcatCollection.class.getName());
	private static final String MSG_EXCEPTION_CONCURRENT = "ConcatCollection modified during comparison";
	private static final int CHUNK_SIZE = 1 << 10;

	private static class Node<T>
	{
		private final T element;
		private Node<T> nodeNext;

		private Node(T element)
		{
			this.element = element;
		}
	}

	private static class NodeSpliterator<T> implements Spliterator<T>
	{
		private int counterSplit;
		private Node<T> nodeSpliter;
		private final Node<T> nodeFence;
		private long size;

		/**
		 * Spliterator for ConcatCollections.
		 *
		 * @param nodeSpliter first node to iterate over
		 * @param nodeFence last node to iterate over
		 * @param size the number of nodes from first to last node both inclusively
		 */
		private NodeSpliterator(Node<T> nodeSpliter, Node<T> nodeFence, long size)
		{
			this.nodeSpliter = nodeSpliter;
			this.nodeFence = nodeFence;
			this.size = size;
		}

		@Override
		public boolean tryAdvance(Consumer<? super T> action)
		{
			// iterate from nodeSpliter to nodeFence both inclusively:
			boolean hasRemaining = (this.nodeSpliter != null);
			if (hasRemaining)
			{
				if ((this.nodeSpliter.nodeNext == null) && (this.nodeSpliter != this.nodeFence))
				{
					throw new ConcurrentModificationException(MSG_EXCEPTION_CONCURRENT);
				}
				if (action != null)
				{
					action.accept(this.nodeSpliter.element);
				}
				this.nodeSpliter = (this.nodeSpliter != this.nodeFence) ?
					this.nodeSpliter.nodeNext : null;
			}
			return hasRemaining;
		}

		@Override
		public Spliterator<T> trySplit()
		{
			if ((this.nodeSpliter == null) || (this.size < 2 * CHUNK_SIZE))
			{
				return null;
			}
			Node<T> node = this.nodeSpliter;
			// count nodes from first to last node both inclusively:
			int counterNodes = 1;
			while ((counterNodes < CHUNK_SIZE) && (node != null) && (node != this.nodeFence))
			{
				node = node.nodeNext;
				counterNodes++;
			}
			if ((counterNodes < CHUNK_SIZE) || (node == null) || (node.nodeNext == null) || (node == this.nodeFence))
			{
				return null;
			}
			final Spliterator<T> result = new NodeSpliterator<>(this.nodeSpliter, node, counterNodes);
			this.nodeSpliter = node.nodeNext;
			this.size -= counterNodes;
			logger.log(Level.FINE, "{0} : SPLIT #{1} with {2} new and {3} remaining elements",
				new Object[]
				{
					getClass().getName(), this.counterSplit++, counterNodes, this.size
				});
			return result;
		}

		@Override
		public long estimateSize()
		{
			return this.size;
		}

		@Override
		public int characteristics()
		{
			return ORDERED | SIZED | SUBSIZED;
		}
	}
	private Node<T> nodeFirst;
	private Node<T> nodeLast;
	private int size;

	/**
	 * Creates a new empty ConcatCollection instance.
	 */
	public ConcatCollection()
	{
	}

	/**
	 * Creates a new ConcatCollection instance and adds all elements of the given
	 * collection.
	 *
	 * @param collection if not null, all elements of the collection are added to
	 * this ConcatCollection
	 */
	public ConcatCollection(Collection<? extends T> collection)
	{
		if (collection != null)
		{
			addAll(collection);
		}
	}

	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			private Node<T> nodeIter;
			private Node<T> nodePred;
			private boolean canRemove;

			@Override
			public boolean hasNext()
			{
				return (this.nodeIter != null) ?
					(this.nodeIter.nodeNext != null) : (nodeFirst != null);
			}

			@Override
			public T next()
			{
				if (!hasNext())
				{
					throw new NoSuchElementException();
				}
				this.nodePred = this.nodeIter;
				this.nodeIter = (this.nodeIter != null) ? this.nodeIter.nodeNext : nodeFirst;
				this.canRemove = true;
				return this.nodeIter.element;
			}

			@Override
			public void remove()
			{
				if (!this.canRemove)
				{
					throw new IllegalStateException("Iterator.remove() not possible");
				}
				if (this.nodePred != null)
				{
					this.nodePred.nodeNext = this.nodeIter.nodeNext;
				}
				else
				{
					nodeFirst = this.nodeIter.nodeNext;
				}
				size--;
				this.canRemove = false;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <strong>NOTE:</strong>
	 * While the {@link #iterator() iterator} supports a save
	 * {@link Iterator#remove() remove} operation, the Spliterator does not! This
	 * collection must not be changed during spliteration.
	 * <p>
	 * <strong>Implementation note:</strong>
	 * <p>
	 * This Spliterator is more memory efficient than the default
	 * {@link java.util.Spliterators#spliterator(Collection, int) Spliterators.spliterator(Collection, int)}
	 * by avoiding to copy all elements into an array and directly operating on
	 * internal node objects.
	 *
	 * @throws ConcurrentModificationException if the collections change during
	 * comparison
	 */
	@Override
	public Spliterator<T> spliterator()
	{
		return new NodeSpliterator<>(this.nodeFirst, this.nodeLast, size());
	}

	@Override
	public int size()
	{
		return this.size;
	}

	@Override
	public boolean add(T newElement)
	{
		final Node<T> node = new Node<>(newElement);
		if (isEmpty())
		{
			this.nodeFirst = node;
		}
		else
		{
			this.nodeLast.nodeNext = node;
		}
		this.nodeLast = node;
		this.size++;
		return true;
	}

	@Override
	public void clear()
	{
		this.nodeFirst = null;
		this.nodeLast = null;
		this.size = 0;
	}

	/**
	 * Returns the concatenation of this and the other collection. The
	 * concatenation is performed in {@code  O(1)} time by direct connection of
	 * internal nodes. Note, that changes made to the other collection after the
	 * concatenation will be reflected in the resulting collection.
	 * <p>
	 * <strong>NOTE:</strong> The caller is responsible for not concatenating the
	 * same ConcatCollection instance twice, otherwise [spl]iteration will result
	 * in endless loops!
	 *
	 * @param other the other collection
	 * @see #detectCycle()
	 */
	public void concat(ConcatCollection<T> other)
	{
		if ((other != null) && !other.isEmpty())
		{
			if (isEmpty())
			{
				this.nodeFirst = other.nodeFirst;
			}
			else
			{
				this.nodeLast.nodeNext = other.nodeFirst;
			}
			this.nodeLast = other.nodeLast;
			this.size += other.size;
		}
	}

	/**
	 * Returns true, if an internal cycle is detected. A cycle can occur, if the
	 * same ConcatCollection is {@link #concat(ConcatCollection) concatenated}
	 * twice. This method is primarily for testing and debugging purposes.
	 *
	 * @return true, if an internal cycle is detected
	 * @see #concat(ConcatCollection)
	 */
	public boolean detectCycle()
	{
		final IdentityHashMap<Node<T>, Void> map = new IdentityHashMap<>(size());
		for (Node<T> node = this.nodeFirst; node != null; node = node.nodeNext)
		{
			if (map.containsKey(node))
			{
				return true;
			}
			else
			{
				map.put(node, null);
			}
		}
		return false;
	}

	/**
	 * Compares this collection with the given object. Returns true, if the other
	 * object is of the same type, contains the same number of elements and the
	 * equals method for the contained elements in the iterated order pairwise
	 * returns true. Returns false otherwise.
	 *
	 * @param obj the other object to compare
	 * @return true, if the other object is a ConcatCollection with the same
	 * content
	 * @throws ConcurrentModificationException if the collections change during
	 * comparison
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ConcatCollection)
		{
			final ConcatCollection other = (ConcatCollection) obj;
			if (this.size() != other.size())
			{
				return false;
			}
			final Iterator i1 = this.iterator();
			final Iterator i2 = other.iterator();
			for (int i = 0; i < size(); i++)
			{
				try
				{
					if (!i1.next().equals(i2.next()))
					{
						return false;
					}
				}
				catch (NoSuchElementException ex)
				{
					throw new ConcurrentModificationException(MSG_EXCEPTION_CONCURRENT, ex);
				}
			}
			if (i1.hasNext() || i2.hasNext())
			{
				throw new ConcurrentModificationException(MSG_EXCEPTION_CONCURRENT);
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return this.size;
	}

	@SuppressWarnings("unchecked")
	private ConcatCollection<T> cloneAndCastInstance()
	{
		try
		{
			return (ConcatCollection<T>) super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			// this should never happen
			throw new InternalError(ex);
		}
	}

	/**
	 * Returns a shallow copy of this collection.
	 *
	 * @return a cloned collection
	 * @throws CloneNotSupportedException this class will never throw a
	 * CloneNotSupportedException, whereas a specialized class might do so
	 */
	@Override
	public ConcatCollection<T> clone() throws CloneNotSupportedException
	{
		final ConcatCollection<T> cloned = cloneAndCastInstance();
		cloned.clear();
		cloned.addAll(this);
		return cloned;
	}
	private static final long serialVersionUID = 5618589819153324296L;

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		final int n = size();
		out.write(n);
		final Iterator<T> iterator = iterator();
		for (int i = 0; i < n; i++)
		{
			try
			{
				out.writeObject(iterator.next());
			}
			catch (NoSuchElementException ex)
			{
				throw new IOException(new ConcurrentModificationException(MSG_EXCEPTION_CONCURRENT, ex));
			}
		}
		if (iterator.hasNext())
		{
			throw new IOException(new ConcurrentModificationException(MSG_EXCEPTION_CONCURRENT));
		}
	}

	@SuppressWarnings("unchecked")
	private T readElementFromStream(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		return (T) in.readObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		clear();
		final int n = in.readInt();
		for (int i = 0; i < n; i++)
		{
			add(readElementFromStream(in));
		}
	}
}
