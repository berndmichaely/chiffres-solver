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
package de.bernd_michaely.chiffres.calc;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.bernd_michaely.chiffres.calc.Operator.*;

/**
 * Immutable class to represent a complete calculation, that is a stack of
 * operations.
 */
public class Solution implements Comparable<Solution>
{
	private static final Pattern PATTERN_EXPRESSION = Pattern.compile(
		"\\s*(@?)(\\d+)\\s*([+\\-*/])\\s*(@?)(\\d+)\\s*");
	static final OperationsComparator COMPARATOR = new OperationsComparator();
	private static final Operand ZERO = new Operand(0);
	private final Operation[] operations;
	private final boolean redundant;

	/**
	 * Class to represent an equivalence class for solutions.
	 */
	public static class EquivalenceClass implements Comparable<EquivalenceClass>
	{
		private final Operation[] operations;

		EquivalenceClass(Solution solution)
		{
			if (solution == null)
			{
				throw new IllegalArgumentException("Solution is null");
			}
			this.operations = solution.operations.clone();
			Arrays.sort(this.operations);
		}

		@Override
		public int compareTo(EquivalenceClass other)
		{
			return COMPARATOR.compare(this.operations, other.operations);
		}

		@Override
		public boolean equals(Object other)
		{
			return (other instanceof EquivalenceClass) ?
				(compareTo((EquivalenceClass) other) == 0) : false;
		}

		@Override
		public int hashCode()
		{
			return Arrays.deepHashCode(this.operations);
		}
	}

	static class OperationsComparator implements Comparator<Operation[]>
	{
		@Override
		public int compare(Operation[] operations1, Operation[] operations2)
		{
			if (operations1 == null)
			{
				return (operations2 == null) ? 0 : -1;
			}
			if (operations2 == null)
			{
				return 1;
			}
			final int depth = operations1.length;
			final int depth2 = operations2.length;
			final int compDepth = Integer.compare(depth, depth2);
			if (compDepth != 0)
			{
				return compDepth;
			}
			int i = 0;
			while (i < depth)
			{
				final int compOp = operations1[i].compareTo(operations2[i]);
				if (compOp != 0)
				{
					return compOp;
				}
				i++;
			}
			return 0;
		}
	}

	/**
	 * Constructor creating a Solutions object.
	 *
	 * @param operations the operations the solution consists of
	 */
	Solution(Operation... operations)
	{
		this(operations.length, operations);
	}

	/**
	 * Constructor creating a Solutions object with a given number of operations.
	 *
	 * @param length     the given number of operations
	 * @param operations the operations the solution consists of
	 */
	Solution(final int length, Operation... operations)
	{
		if (operations == null)
		{
			throw new IllegalArgumentException("Operation[] is null");
		}
		if (length < 1)
		{
			throw new IllegalArgumentException("invalid length");
		}
		this.operations = new Operation[length];
		System.arraycopy(operations, 0, this.operations, 0, length);
		this.redundant = calcRedundancy();
	}

	/**
	 * Constructor for easy creation of test cases. For example the String
	 * {@literal "3*7,@0+4"} describes a solution consisting of two operations,
	 * the first to multiply 3 with 7, and the second to add 4 to the result of 21
	 * by referencing the first operation through {@literal "@0"}, resulting in a
	 * value of 25.
	 *
	 * @param string a string to describe a solution
	 */
	Solution(String string)
	{
		final String[] strOperations = string.split(",");
		final int length = strOperations.length;
		this.operations = new Operation[length];
		for (int i = 0; i < length; i++)
		{
			final String expression = strOperations[i];
			final Matcher matcher = PATTERN_EXPRESSION.matcher(expression);
			if (matcher.matches())
			{
				final boolean isOp1Ref = !matcher.group(1).isEmpty();
				final int op1 = Integer.parseInt(matcher.group(2));
				final boolean isOp2Ref = !matcher.group(4).isEmpty();
				final int op2 = Integer.parseInt(matcher.group(5));
				final String strOp = matcher.group(3);
				final Operator operator;
				switch (strOp)
				{
					case "+":
						operator = ADD;
						break;
					case "-":
						operator = SUB;
						break;
					case "*":
						operator = MUL;
						break;
					case "/":
						operator = DIV;
						break;
					default:
						throw new AssertionError();
				}
				if (isOp1Ref && (op1 >= i))
				{
					throw new IllegalArgumentException(String.format(
						"Invalid forward reference @%d in expression %s at index %d",
						op1, expression, i));
				}
				if (isOp2Ref && (op2 >= i))
				{
					throw new IllegalArgumentException(String.format(
						"Invalid forward reference @%d in expression %s at index %d",
						op2, expression, i));
				}
				Operand operand1 = isOp1Ref ? this.operations[op1] : new Operand(op1);
				Operand operand2 = isOp2Ref ? this.operations[op2] : new Operand(op2);
				if (operand2.getValue() > operand1.getValue())
				{
					final Operand opSwitch = operand1;
					operand1 = operand2;
					operand2 = opSwitch;
				}
				this.operations[i] = new Operation(operand1, operator, operand2,
					operator.calculate(operand1.value, operand2.value));
			}
			else
			{
				throw new IllegalArgumentException("Invalid solution expression");
			}
		}
		this.redundant = calcRedundancy();
	}

	/**
	 * Returns the number of operations.
	 *
	 * @return the number of operations
	 */
	public int getDepth()
	{
		return this.operations.length;
	}

	/**
	 * Returns the operation at the given index.
	 *
	 * @param index the given index
	 * @return the operation at the given index or null, if index is out of range
	 */
	public Operation getOperation(int index)
	{
		return ((index >= 0) && (index < this.operations.length)) ?
			this.operations[index] : null;
	}

	Stream<Operation> operations()
	{
		return Arrays.stream(this.operations);
	}

	/**
	 * Returns the last operation giving the final result. That is it returns the
	 * top element of the operation stack, or the root of the operation tree.
	 *
	 * @return the last operation giving the final result
	 */
	public Operation getRootOperation()
	{
		return getOperation(getDepth() - 1);
	}

	/**
	 * Returns the result of the whole calculation.
	 *
	 * @return the result of the whole calculation as integer
	 */
	public int getValue()
	{
		return getRootOperation().getValue();
	}

	@Override
	public int compareTo(Solution other)
	{
		return COMPARATOR.compare(this.operations, other.operations);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof Solution) ?
			compareTo((Solution) obj) == 0 : false;
	}

	@Override
	public int hashCode()
	{
		return Arrays.deepHashCode(this.operations);
	}

	/**
	 * Return a String-description of the solution.
	 *
	 * @return a String-description of the solution
	 */
	@Override
	public String toString()
	{
		if (this.operations.length == 0)
		{
			return "[]";
		}
		final StringBuilder str = new StringBuilder();
		for (int i = this.operations.length - 1; i >= 0; i--)
		{
			str.append(String.format("%nOperation %2d : %s", i + 1, this.operations[i]));
		}
		return str.toString();
	}

	/**
	 * Returns true, if this solution is redundant.
	 *
	 * @return true, if this solution is redundant
	 * @see #calcRedundancy()
	 */
	public boolean isRedundant()
	{
		return this.redundant;
	}

	/**
	 * Checks, if this solution can be reduced by eliminating some unneccessary
	 * operations.
	 *
	 * @return true, if this solution is redundant
	 */
	private boolean calcRedundancy()
	{
		for (Operation operation : this.operations)
		{
			if (reduceCalculation(getValue(), reduceOperations(
				operation, true, this.operations)) != null)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks, if this solution can be reduced by eliminating some unnecessary
	 * operations. This is a parallelized version of {@link #calcRedundancy() }.
	 *
	 * @return true, if this solution is redundant
	 */
	private boolean calcRedundancyParallel()
	{
		return Arrays.asList(this.operations).parallelStream().anyMatch(operation ->
			(reduceCalculation(getValue(), reduceOperations(operation, true, this.operations)) != null));
	}

	/**
	 * Creates a modified version of the given operations array by leaving out a
	 * selected operation and optionally adding dummy identity operations for its
	 * non-calculated operands.
	 *
	 * @param selected        the selected operation to drop
	 * @param includeOperands indicates, if operations for the non-calculated
	 *                        operands of the selected operation should be
	 *                        generated
	 * @param operations      the original operations
	 * @return the modified reduced operations array
	 */
	private static Operation[] reduceOperations(Operation selected,
		boolean includeOperands, Operation... operations)
	{
		final boolean includeOp1 = includeOperands && !selected.isOp1Calculated();
		final boolean includeOp2 = includeOperands && !selected.isOp2Calculated();
		final int num = operations.length - 1 +
			(includeOp1 ? 1 : 0) + (includeOp2 ? 1 : 0);
		final Operation[] result = new Operation[num];
		int counter = 0;
		// create dummy identity operations for non calculated operands
		if (includeOp1)
		{
			final int op = selected.operand1.value;
			result[counter++] = new Operation(new Operand(op), Operator.ADD, ZERO, op);
		}
		if (includeOp2)
		{
			final int op = selected.operand2.value;
			result[counter++] = new Operation(new Operand(op), Operator.ADD, ZERO, op);
		}
		for (Operation operation : operations)
		{
			if (selected != operation)
			{
				result[counter++] = operation;
			}
		}
		return result;
	}

	/**
	 * Recursive method to reconstruct a valid calculation from the given
	 * operations.
	 *
	 * @param value      the resulting value for which a valid calculation is
	 *                   searched
	 * @param operations the given operations
	 * @return an array of remaining operations for the recursive call or null, if
	 *         a valid calculation is not possible
	 */
	private Operation[] reduceCalculation(int value, Operation... operations)
	{
		if (operations.length == 0)
		{
			return null;
		}
		for (Operation op : operations)
		{
			if (value == op.value)
			{
				final Operation[] reduced = reduceOperations(op, false, operations);
				final Operation[] operations1 = op.isOp1Calculated() ?
					reduceCalculation(op.operand1.value, reduced) : reduced;
				if (operations1 == null)
				{
					return null;
				}
				final Operation[] operations2 = op.isOp2Calculated() ?
					reduceCalculation(op.operand2.value, operations1) : operations1;
				if (operations2 == null)
				{
					return null;
				}
				return operations2;
			}
		}
		return null;
	}

	/**
	 * Returns a normalized variant of this solution or null, if it is redundant.
	 *
	 * @deprecated Work in progress …
	 * @return a normalized variant of this solution or null, if it is redundant
	 */
	@Deprecated
	Solution normalize()
	{
		return null;
//		if (isRedundant())
//		{
//			return null;
//		}
//		final Optional<Operation[]> minOp =
//			new TreeStructureVariants(this.operations).stream().
//				map(opAr -> opAr[opAr.length - 1]).
//				map(root -> new TopologicalSorter(root).getSortedNodes()).
//				min(Solution.COMPARATOR);
//		return minOp.isPresent() ? new Solution(minOp.get()) : null;
	}
}
