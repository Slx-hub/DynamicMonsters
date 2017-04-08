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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public class Calculation {

	private final List<CalculationNode> nodes = new ArrayList<>();
	private final Set<String> providedVariables = new HashSet<>();
	private final Set<String> requiredVariables = new HashSet<>();

	private final Set<CalculationNode> unresolved = new HashSet<>();

	public Calculation () {
	}

	public Calculation (Set<String> providedVariables) {
		this.providedVariables.addAll(providedVariables);
		this.requiredVariables.addAll(providedVariables);
	}

	public void addNode (CalculationNode node) {
		Objects.requireNonNull(node);
		if (nodes.contains(node)) {
			throw new IllegalArgumentException();
		}
		if (!tryNode(node)) {
			this.unresolved.add(node);
			return;
		}
		addUnresolvedIfPossible();
	}

	public void addVariable(String variable) {
		Objects.requireNonNull(variable);
		if (variable.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.providedVariables.add(variable);
		this.requiredVariables.add(variable);
		addUnresolvedIfPossible();
	}
	
	private void addUnresolvedIfPossible () {
		for (Iterator<CalculationNode> iterator = this.unresolved.iterator(); iterator.hasNext();) {
			if (tryNode(iterator.next())) {
				iterator.remove();
			}
		}
	}
	
	private boolean tryNode (CalculationNode node) {
		if (this.providedVariables.containsAll(node.getRequiredVariables())) {
			this.nodes.add(node);
			this.providedVariables.add(node.getProvidedVariable());
			return true;
		}
		return false;
	}

	public void executeCalculation (Map<String, Double> variables) {
		if (!variables.keySet().containsAll(this.requiredVariables)) {
			throw new IllegalArgumentException("missing variables");
		}
		this.nodes.forEach(calculationNode -> calculationNode.calculateAndAddVariable(variables));
	}
	
	public boolean hasUnresolvedCalculationNodes() {
		return !this.unresolved.isEmpty();
	}
}
