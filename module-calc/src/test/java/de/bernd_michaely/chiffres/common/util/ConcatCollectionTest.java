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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ConcatCollection class.
 *
 * @author Bernd Michaely
 */
public class ConcatCollectionTest
{
	private static final int NUM_ELEMENTS = 1 << 20;

	@Test
	public void testSize()
	{
		System.out.println("testSize()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		assertTrue(c.isEmpty());
		assertEquals(c.size(), 0);
		c.add(17);
		assertFalse(c.isEmpty());
		assertEquals(c.size(), 1);
		c.clear();
		assertTrue(c.isEmpty());
		assertEquals(c.size(), 0);
	}

	@Test
	public void testIterator()
	{
		System.out.println("testIterator()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		c.add(1);
		c.add(2);
		c.add(3);
		assertFalse(c.isEmpty());
		assertEquals(c.size(), 3);
		final Iterator<Integer> iter = c.iterator();
		assertTrue(iter.hasNext());
		int nextValue;
		assertTrue(iter.hasNext());
		nextValue = iter.next();
		assertEquals(nextValue, 1);
		assertTrue(iter.hasNext());
		nextValue = iter.next();
		assertEquals(nextValue, 2);
		assertTrue(iter.hasNext());
		nextValue = iter.next();
		assertEquals(nextValue, 3);
		assertFalse(iter.hasNext());
	}

	@Test
	public void testSpliteratorEmpty()
	{
		System.out.println("testSpliteratorEmpty()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		assertTrue(c.isEmpty());
		final Spliterator<Integer> iter = c.spliterator();
		assertEquals(iter.estimateSize(), 0);
		assertEquals(iter.getExactSizeIfKnown(), 0);
		assertEquals(c.parallelStream().count(), 0);
	}

	@Test
	public void testSpliterator()
	{
		System.out.println("testSpliterator()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		for (int i = 0; i < NUM_ELEMENTS; i++)
		{
			c.add(i);
		}
		assertFalse(c.isEmpty());
		assertEquals(c.size(), NUM_ELEMENTS);
		final Spliterator<Integer> iter = c.spliterator();
		assertEquals(iter.estimateSize(), NUM_ELEMENTS);
		assertEquals(iter.getExactSizeIfKnown(), NUM_ELEMENTS);
		assertEquals(c.parallelStream().count(), NUM_ELEMENTS);
		final Integer[] values = c.parallelStream().toArray(Integer[]::new);
		for (int k = 0; k < NUM_ELEMENTS; k++)
		{
			assertTrue(iter.tryAdvance(i -> values[i] = i));
		}
		assertFalse(iter.tryAdvance(i -> System.out.println(i)));
		for (int m = 0; m < NUM_ELEMENTS; m++)
		{
			assertEquals(values[m].intValue(), m);
		}
	}

	@Test
	public void testIteratorLarge()
	{
		System.out.println("testIteratorLarge()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		for (int i = 0; i < NUM_ELEMENTS; i++)
		{
			c.add(i);
		}
		assertFalse(c.isEmpty());
		assertEquals(c.size(), NUM_ELEMENTS);
		final Iterator<Integer> iter = c.iterator();
		final int[] values = new int[NUM_ELEMENTS];
		for (int k = 0; k < NUM_ELEMENTS; k++)
		{
			assertTrue(iter.hasNext());
			final int index = iter.next();
			values[index] = index;
		}
		assertFalse(iter.hasNext());
		for (int m = 0; m < NUM_ELEMENTS; m++)
		{
			assertEquals(values[m], m);
		}
	}

	public void testIteratorRemove_001()
	{
		System.out.println("testIteratorRemove_001()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		c.add(1);
		c.add(2);
		c.add(3);
		assertFalse(c.isEmpty());
		assertEquals(c.size(), 3);
		final Iterator<Integer> iter = c.iterator();
		assertTrue(iter.hasNext());
		assertThrows(IllegalStateException.class, () -> iter.remove());
	}

	@Test
	public void testIteratorRemove_002()
	{
		System.out.println("testIteratorRemove_002()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		c.add(1);
		c.add(2);
		c.add(3);
		assertFalse(c.isEmpty());
		assertEquals(c.size(), 3);
		final Iterator<Integer> iter = c.iterator();
		assertTrue(iter.hasNext());
		assertEquals(iter.next().intValue(), 1);
		iter.remove();
		assertEquals(c.size(), 2);
		assertEquals(iter.next().intValue(), 2);
		iter.remove();
		assertEquals(c.size(), 1);
		assertEquals(iter.next().intValue(), 3);
		iter.remove();
		assertEquals(c.size(), 0);
		assertFalse(iter.hasNext());
	}

	public void testIteratorRemove_003()
	{
		System.out.println("testIteratorRemove_003()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		c.add(1);
		c.add(2);
		c.add(3);
		assertFalse(c.isEmpty());
		assertEquals(c.size(), 3);
		final Iterator<Integer> iter = c.iterator();
		assertTrue(iter.hasNext());
		assertEquals(iter.next().intValue(), 1);
		iter.remove();
		assertEquals(c.size(), 2);
		assertThrows(IllegalStateException.class, () -> iter.remove());
	}

	private void testIteratorRemoveAtIndex(int numElements, int indexRemove)
	{
		System.out.println("Test Iterator.remove() with " + numElements +
			" elements and remove() @ index " + indexRemove);
		assertTrue(numElements > 0);
		assertTrue(indexRemove > 0);
		assertTrue(indexRemove <= numElements);
		// fill collection:
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		assertTrue(c.isEmpty());
		for (int i = 1; i <= numElements; i++)
		{
			c.add(i);
			assertEquals(c.size(), i);
		}
		assertFalse(c.isEmpty());
		// remove element @ index:
		final Iterator<Integer> iter1 = c.iterator();
		for (int i = 1; i <= numElements; i++)
		{
			assertTrue(iter1.hasNext());
			assertEquals(i, iter1.next().intValue());
			if (i == indexRemove)
			{
				iter1.remove();
			}
		}
		// check changed collection:
		assertEquals(c.size(), numElements - 1);
		final Iterator<Integer> iter2 = c.iterator();
		for (int i = 1; i <= numElements; i++)
		{
			if (i != indexRemove)
			{
				assertTrue(iter2.hasNext());
				assertEquals(i, iter2.next().intValue());
			}
		}
		assertFalse(iter2.hasNext());
	}

	@Test
	public void testIteratorRemoveAtIndex()
	{
		System.out.println("testIteratorRemoveAtIndex()");
		for (int i = 1; i <= 10; i++)
		{
			for (int k = 1; k <= i; k++)
			{
				testIteratorRemoveAtIndex(i, k);
			}
		}
	}

	@Test
	public void testConcat()
	{
		System.out.println("testConcat()");
		final int numMaxElements = 9;
		for (int i = 0; i <= numMaxElements; i++)
		{
			System.out.print("--> Create ConcatCollections with sizes :");
			final List<ConcatCollection<Integer>> collections = new ArrayList<>();
			int counterItemsAll = 0;
			int counterSizeColl = i;
			boolean up = true;
			for (int k = 0; k < 2 * numMaxElements; k++)
			{
				System.out.print(" " + counterSizeColl);
				final ConcatCollection<Integer> concatCollection = new ConcatCollection<>();
				collections.add(concatCollection);
				for (int m = 1; m <= counterSizeColl; m++)
				{
					concatCollection.add(++counterItemsAll);
				}
				if (up && (counterSizeColl == numMaxElements))
				{
					up = false;
				}
				else if (!up && (counterSizeColl == 0))
				{
					up = true;
				}
				counterSizeColl = up ? counterSizeColl + 1 : counterSizeColl - 1;
			}
			System.out.print(" and concat() : ");
			// check concatenated collection:
			final ConcatCollection<Integer> cAll = new ConcatCollection<>();
			collections.forEach(c -> cAll.concat(c));
			assertEquals(cAll.size(), counterItemsAll);
			final Iterator<Integer> iter = cAll.iterator();
			for (int n = 1; n <= counterItemsAll; n++)
			{
				assertTrue(iter.hasNext());
				assertEquals(n, iter.next().intValue());
			}
			assertFalse(iter.hasNext());
			assertFalse(cAll.detectCycle());
			System.out.println("OK");
		}
	}

	@Test
	public void testEqualsTrue()
	{
		System.out.println("testEqualsTrue()");
		final ConcatCollection<Integer> c1 = new ConcatCollection<>();
		c1.add(1);
		c1.add(2);
		c1.add(3);
		assertFalse(c1.isEmpty());
		assertEquals(c1.size(), 3);
		final ConcatCollection<Integer> c2 = new ConcatCollection<>();
		c2.add(1);
		c2.add(2);
		c2.add(3);
		assertFalse(c2.isEmpty());
		assertEquals(c2.size(), 3);
		assertTrue(c1.equals(c2));
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	@Test
	public void testEqualsFalse_001()
	{
		System.out.println("testEqualsFalse_001()");
		final ConcatCollection<Integer> c1 = new ConcatCollection<>();
		c1.add(1);
		c1.add(2);
		c1.add(3);
		assertFalse(c1.isEmpty());
		assertEquals(c1.size(), 3);
		final ConcatCollection<Integer> c2 = new ConcatCollection<>();
		c2.add(4);
		c2.add(2);
		c2.add(3);
		assertFalse(c2.isEmpty());
		assertEquals(c2.size(), 3);
		assertFalse(c1.equals(c2));
	}

	@Test
	public void testEqualsFalse_002()
	{
		System.out.println("testEqualsFalse_002()");
		final ConcatCollection<Integer> c1 = new ConcatCollection<>();
		c1.add(1);
		c1.add(2);
		c1.add(3);
		assertFalse(c1.isEmpty());
		assertEquals(c1.size(), 3);
		final ConcatCollection<Integer> c2 = new ConcatCollection<>();
		c2.add(1);
		c2.add(2);
		c2.add(3);
		c2.add(4);
		assertFalse(c2.isEmpty());
		assertEquals(c2.size(), 4);
		assertFalse(c1.equals(c2));
	}

	@Test
	public void testClone_001() throws CloneNotSupportedException
	{
		System.out.println("testClone_001()");
		final ConcatCollection<Integer> c1 = new ConcatCollection<>();
		c1.add(1);
		c1.add(2);
		c1.add(3);
		assertFalse(c1.isEmpty());
		assertEquals(c1.size(), 3);
		final ConcatCollection<Integer> c2 = c1.clone();
		assertNotSame(c1, c2);
		assertSame(c1.getClass(), c2.getClass());
		assertEquals(c1, c2);
	}

	@Test
	public void testClone_002() throws CloneNotSupportedException
	{
		System.out.println("testClone_002()");
		class ConcatCollectionSpecialized<T> extends ConcatCollection<T>
		{
			private static final long serialVersionUID = 6079478114399290838L;

			@Override
			public ConcatCollectionSpecialized<T> clone() throws CloneNotSupportedException
			{
				return (ConcatCollectionSpecialized<T>) super.clone();
			}
		}
		final ConcatCollectionSpecialized<Integer> c1 = new ConcatCollectionSpecialized<>();
		c1.add(1);
		c1.add(2);
		c1.add(3);
		assertFalse(c1.isEmpty());
		assertEquals(c1.size(), 3);
		// the following clone() call might cause a ClassCastException,
		// if the implementation does not call super.clone() properly:
		final ConcatCollectionSpecialized<Integer> c2 = c1.clone();
		assertNotSame(c1, c2);
		assertSame(c1.getClass(), c2.getClass());
		assertEquals(c1, c2);
	}

	@Test
	public void testDetectCycle()
	{
		System.out.println("testDetectCycle()");
		final ConcatCollection<Integer> c = new ConcatCollection<>();
		c.add(1);
		c.add(2);
		c.add(3);
		assertFalse(c.isEmpty());
		c.concat(c);
		assertFalse(c.isEmpty());
		assertTrue(c.detectCycle());
	}

	private byte[] writeSerializable(int n) throws IOException
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			try (ObjectOutputStream oos = new ObjectOutputStream(baos))
			{
				for (int i = 1; i <= n; i++)
				{
					final int p = i * i;
					oos.writeUTF(i + " * " + i + " = " + p);
					oos.writeInt(p);
				}
			}
			return baos.toByteArray();
		}
	}

	@Test
	public void testSerializable() throws IOException
	{
		final int numItems = 20;
		final byte[] bytes = writeSerializable(numItems);
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes))
		{
			try (ObjectInputStream ois = new ObjectInputStream(bais))
			{
				for (int i = 1; i <= numItems; i++)
				{
					final String str = ois.readUTF();
					final int num = ois.readInt();
					final int numExpected = i * i;
					final String strExpected = i + " * " + i + " = " + numExpected;
					System.out.println(String.format("%2d : %s", i, str));
					assertEquals(str, strExpected);
					assertEquals(num, numExpected);
				}
			}
		}
	}
}
