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

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.objecthunter.exp4j.Expression;

/**
 *
 */
public class CalculationNode implements Comparable<CalculationNode> {

    private final String variableName;
    private final Expression expression;

    public CalculationNode(String variableName, Expression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    public void calculateAndAddVariable(Map<String, Double> variables) {
        if (!variables.keySet().containsAll(getRequiredVariables())) {
            throw new IllegalArgumentException("missing variables");
        }
        variables.put(variableName, expression.setVariables(variables).evaluate());
    }
    
    public String getProvidedVariable() {
        return variableName;
    }

    public Set<String> getRequiredVariables() {
        return expression.getVariableNames();
    }

    @Override
    public int compareTo(CalculationNode other) {
        if (this.getRequiredVariables().contains(other.getProvidedVariable())) {
            if (other.getRequiredVariables().contains(this.getProvidedVariable())) {
                throw new IllegalArgumentException("cyclic dependency");
            } else {
                return -1;
            }
        } else {
            if (other.getRequiredVariables().contains(this.getProvidedVariable())) {
                return 1;
            } else {
                return this.getProvidedVariable().compareTo(other.getProvidedVariable());
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.variableName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CalculationNode other = (CalculationNode) obj;
        return Objects.equals(this.variableName, other.variableName);
    }
}
