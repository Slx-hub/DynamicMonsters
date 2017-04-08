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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author niklas
 */
public class CalculationNodeTest {

	public CalculationNodeTest () {
	}

	private Expression createExpressionOf (String expression, Set<String> variables) {
		return new ExpressionBuilder(expression).operator(AdditionalMathOperations.getAdditionalOperator())
				.functions(AdditionalMathOperations.getAdditionalFunctions()).variables(variables).build();
	}

	@Test
	public void testSortedCalculation () {
		Set<String> variableNames = new HashSet<>(Arrays.asList("x", "y", "z", "centerX", "centerY", "centerZ",
																"horizontalDistancePerLevel", "offset",
																"verticalDistancePerLevel", "distance", "actualDistance",
																"levelHorizontal", "levelVertical", "level"));
		Calculation calculation = new Calculation(new HashSet<>(Arrays.asList("x", "y", "z")));
		calculation.addNode(new CalculationNode("centerX", createExpressionOf("123", variableNames)));
		calculation.addNode(new CalculationNode("centerY", createExpressionOf("62", variableNames)));
		calculation.addNode(new CalculationNode("centerZ", createExpressionOf("-672", variableNames)));
		calculation.addNode(new CalculationNode("horizontalDistancePerLevel", createExpressionOf("80", variableNames)));
		calculation.addNode(new CalculationNode("offset", createExpressionOf("40", variableNames)));
		calculation.addNode(new CalculationNode("verticalDistancePerLevel", createExpressionOf("10", variableNames)));
		calculation.addNode(new CalculationNode("distance", createExpressionOf("sqrt((x-centerX)^2+(z-centerZ)^2)", variableNames)));
		calculation.addNode(new CalculationNode("actualDistance", createExpressionOf("max(0,distance-offset)", variableNames)));
		calculation.addNode(new CalculationNode("levelHorizontal", createExpressionOf("min(100,floor(actualDistance/horizontalDistancePerLevel))", variableNames)));
		calculation.addNode(new CalculationNode("levelVertical", createExpressionOf("floor(max(0,centerY-y)/verticalDistancePerLevel)", variableNames)));
		calculation.addNode(new CalculationNode("level", createExpressionOf("levelHorizontal+levelVertical", variableNames)));

		Map<String, Double> variables = new HashMap<>();
		variables.put("x", 0d);
		variables.put("y", 0d);
		variables.put("z", 0d);
		calculation.executeCalculation(variables);
		assertEquals(14d, variables.get("level"), 0d);
	}

}
