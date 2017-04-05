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
package de.minetropolis.monsters.exp4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author niklas
 */
public class Exp4jTest {
    
    @Test
    public void testDefaultConfigExpressions() {
        Function min = new Function("min", 2) {
            @Override
            public double apply(double... args) {
                return Math.min(args[0], args[1]);
            }
        };
        
        Function max = new Function("max", 2) {
            @Override
            public double apply(double... args) {
                return Math.max(args[0], args[1]);
            }
        };
        
        Map<String, Double> variables = new HashMap();
        variables.put("x", 0d);
        variables.put("y", 0d);
        variables.put("z", 0d);

        Expression centerX = new ExpressionBuilder("123").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("centerX", centerX.evaluate());
        assertEquals(123d, variables.get("centerX"), 0d);
        
        Expression centerY = new ExpressionBuilder("62").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("centerY", centerY.evaluate());
        assertEquals(62d, variables.get("centerY"), 0d);
        
        Expression centerZ = new ExpressionBuilder("-672").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("centerZ", centerZ.evaluate());
        assertEquals(-672d, variables.get("centerZ"), 0d);
        
        Expression horizontalDistancePerLevel = new ExpressionBuilder("80").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("horizontalDistancePerLevel", horizontalDistancePerLevel.evaluate());
        assertEquals(80d, variables.get("horizontalDistancePerLevel"), 0d);
        
        Expression offset = new ExpressionBuilder("40").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("offset", offset.evaluate());
        assertEquals(40d, variables.get("offset"), 0d);
        
        Expression verticalDistancePerLevel = new ExpressionBuilder("10").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("verticalDistancePerLevel", verticalDistancePerLevel.evaluate());
        assertEquals(10d, variables.get("verticalDistancePerLevel"), 0d);
        
        Expression distance = new ExpressionBuilder("sqrt((x-centerX)^2+(z-centerZ)^2)").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("distance", distance.evaluate());
        assertEquals(683.163962749792591838890843911d, variables.get("distance"), 1e-8d);

        Expression actualDistance = new ExpressionBuilder("max(0,distance-offset)").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("actualDistance", actualDistance.evaluate());
        assertEquals(643.163962749792591838890843911d, variables.get("actualDistance"), 1e-8d);

        Expression levelHorizontal = new ExpressionBuilder("min(100,floor(actualDistance/horizontalDistancePerLevel))").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("levelHorizontal", levelHorizontal.evaluate());
        assertEquals(8d, variables.get("levelHorizontal"), 0d);

        Expression levelVertical = new ExpressionBuilder("floor(max(0,centerY-y)/verticalDistancePerLevel)").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("levelVertical", levelVertical.evaluate());
        assertEquals(6d, variables.get("levelVertical"), 0d);

        Expression level = new ExpressionBuilder("levelHorizontal+levelVertical").variables(variables.keySet()).functions(min, max).build().setVariables(variables);
        variables.put("level", level.evaluate());
        assertEquals(14d, variables.get("level"), 0d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingToken() {
        Expression expression = new ExpressionBuilder("floor(max(0,centerY-y)/verticalDistancePerLevel)").build();
    }

    @Test
    public void testTooManyVariables() {
        Expression expression = new ExpressionBuilder("815+3*9-15^2/21").variables("x", "y", "z", "a", "b", "muhaha").build();
        assertEquals(new HashSet<>(), expression.getVariableNames());
    }
}
