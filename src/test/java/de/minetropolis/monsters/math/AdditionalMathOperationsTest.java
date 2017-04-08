/*
 * Copyright (C) 2017 Minetropolis
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
package de.minetropolis.monsters.math;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author niklas
 */
public class AdditionalMathOperationsTest {

	public AdditionalMathOperationsTest () {
	}

	@Test
	public void testMinFunction () {
		Expression expression = new ExpressionBuilder("min(-134,843)").functions(AdditionalMathOperations.MIN).build();
		assertEquals(-134d, expression.evaluate(), 0d);
	}

	@Test
	public void testMaxFunction () {
		Expression expression = new ExpressionBuilder("max(-134,843)").functions(AdditionalMathOperations.MAX).build();
		assertEquals(843d, expression.evaluate(), 0d);
	}

	@Test
	public void testRandomIntFunction () {
		Expression expression = new ExpressionBuilder("randomInt(5)").functions(AdditionalMathOperations.RANDOM_INT).build();
		for (int count = 0; count < 1000; count++) {
			double result = expression.evaluate();
			assertTrue(Math.round(result) == result);
			assertTrue(result >= 0d);
			assertTrue(result <= 4d);
		}
	}

	@Test
	public void testRandomFuncton () {
		Expression expression = new ExpressionBuilder("random").functions(AdditionalMathOperations.RANDOM).build();
		for (int count = 0; count < 1000; count++) {
			double result = expression.evaluate();
			assertTrue(result >= 0d);
			assertTrue(result <= 1d);
		}
	}

	@Test
	public void testRandomFunctonResultChanges () {
		Expression expression = new ExpressionBuilder("random").functions(AdditionalMathOperations.RANDOM).build();
		double result = expression.evaluate();
		int count = 0;
		while (result == expression.evaluate()) {
			if (count > 10000) {
				fail();
			}
			count++;
		}
	}

	@Test
	public void testGreaterThanOperator () {
		Expression trueExpression = new ExpressionBuilder("2 > 1").operator(AdditionalMathOperations.GREATER_THAN).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("1 > 1").operator(AdditionalMathOperations.GREATER_THAN).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

	@Test
	public void testGreaterThanOrEqualOperator () {
		Expression trueExpression = new ExpressionBuilder("1 >= 1").operator(AdditionalMathOperations.GREATER_THAN_OR_EQUAL).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("1 >= 2").operator(AdditionalMathOperations.GREATER_THAN_OR_EQUAL).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

	@Test
	public void testSmallerThanOperator () {
		Expression trueExpression = new ExpressionBuilder("1 < 2").operator(AdditionalMathOperations.SMALLER_THAN).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("1 < 1").operator(AdditionalMathOperations.SMALLER_THAN).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

	@Test
	public void testSmallerThanOrEqualOperator () {
		Expression trueExpression = new ExpressionBuilder("1 <= 1").operator(AdditionalMathOperations.SMALLER_THAN_OR_EQUAL).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("2 <= 1").operator(AdditionalMathOperations.SMALLER_THAN_OR_EQUAL).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

	@Test
	public void testEqualsOperator () {
		Expression trueExpression = new ExpressionBuilder("1 == 1").operator(AdditionalMathOperations.EQUALS).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("1.1 == 1").operator(AdditionalMathOperations.EQUALS).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

	@Test
	public void testEqualsNotOperator () {
		Expression trueExpression = new ExpressionBuilder("1.1 != 1").operator(AdditionalMathOperations.EQUALS_NOT).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("1 != 1").operator(AdditionalMathOperations.EQUALS_NOT).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

	@Test
	public void testAndOperator () {
		Expression trueExpression = new ExpressionBuilder("1 & 1").operator(AdditionalMathOperations.AND).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("0 & 1").operator(AdditionalMathOperations.AND).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

	@Test
	public void testOrOperator () {
		Expression trueExpression = new ExpressionBuilder("0 | 1").operator(AdditionalMathOperations.OR).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("0 | 0").operator(AdditionalMathOperations.OR).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

	@Test
	public void testNotOperator () {
		Expression trueExpression = new ExpressionBuilder("~0").operator(AdditionalMathOperations.NOT).build();
		assertEquals(1d, trueExpression.evaluate(), 0d);
		Expression falseExpression = new ExpressionBuilder("~1").operator(AdditionalMathOperations.NOT).build();
		assertEquals(0d, falseExpression.evaluate(), 0d);
	}

}
