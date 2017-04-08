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
package de.minetropolis.monsters;

import de.minetropolis.monsters.math.CalculationNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.objecthunter.exp4j.Expression;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

/**
 *
 */
public class Variation {

	private static final Random RANDOM = new Random();

	private final String variationName;
	private CalculationNode weightCalculation;
	private boolean nameVisible = false;
	private String namePattern = null;

	public Variation (String variationName, Expression weight) {
		this.variationName = Objects.requireNonNull(variationName);
		if (variationName.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.weightCalculation = new CalculationNode("weight", Objects.requireNonNull(weight));
	}

	public static void modifyEntityWeighted (Set<Variation> variations, LivingEntity entity, Map<String, Double> variables, int level) {
		List<Variation> variationsList = new ArrayList<>(variations);
		List<Integer> weightList = new ArrayList<>();
		int totalWeight = 0;
		for (int index = 0; index < variationsList.size(); index++) {
			int weight = Math.toIntExact(Math.round(variationsList.get(index).calculateWeight(variables)));
			weightList.add(index, weight);
			totalWeight += weight;
		}
		int targetVariation = RANDOM.nextInt(totalWeight);
		for (int index = 0; index < weightList.size(); index++) {
			targetVariation -= weightList.get(index);
			if (targetVariation < 0) {
				variationsList.get(index).modifyEntity(entity, variables, level);
				return;
			}
		}
	}

	public String getName () {
		return this.variationName;
	}

	public double calculateWeight (Map<String, Double> variables) {
		return this.weightCalculation.calculateVariable(variables);
	}

	public void addAttribute (Attribute attribute) {
		throw new UnsupportedOperationException();
	}

	public void addEquipment () {
		throw new UnsupportedOperationException();
	}

	public void addDrop () {
		throw new UnsupportedOperationException();
	}

	public void setWeight (Expression weight) {
		this.weightCalculation = new CalculationNode("weight", Objects.requireNonNull(weight));
	}

	public void setNamePattern (String pattern) {
		this.namePattern = pattern;
	}

	public void setNameVisible (boolean visible) {
		this.nameVisible = visible;
	}

	public void modifyEntity (LivingEntity entity, Map<String, Double> variables, int level) {
		if (this.namePattern != null) {
			entity.setCustomName(generateNameFromPattern(entity, level));
		}
		entity.setCustomNameVisible(this.nameVisible);
	}

	private String generateNameFromPattern (LivingEntity entity, int level) {
		return this.namePattern
				.replaceAll("(^|[^$])\\$level", "$1" + String.valueOf(level))
				.replaceAll("(^|[^$])\\$variation", "$1" + variationName)
				.replaceAll("(^|[^$])\\$type", "$1" + entity.getName());
	}

	@Override
	public int hashCode () {
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.variationName);
		return hash;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Variation other = (Variation) obj;
		return Objects.equals(this.variationName, other.getName());
	}

	@Override
	public String toString () {
		return "Variation{" + "variationName=" + this.variationName + '}';
	}
}
