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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.objecthunter.exp4j.Expression;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class DropVariation {
	protected static final Random RANDOM = new Random();

	private final String variationName;
	private CalculationNode dropChanceCalculation;
	private CalculationNode amountCalculation;
	private CalculationNode damageCalculation;
	private CalculationNode dataCalculation;
	private Material type;

	public DropVariation (String variationName, Expression dropChance, Material type) {
		this.variationName = Objects.requireNonNull(variationName);
		if (variationName.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.dropChanceCalculation = new CalculationNode("dropChance", Objects.requireNonNull(dropChance));
		this.type = Objects.requireNonNull(type);
	}

	public static List<ItemStack> generateLoot(Collection<DropVariation> drops, Map<String, Double> variables) {
		return drops.stream()
				.map(drop -> drop.generateItemByChance(variables))
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.collect(Collectors.toList());
	}
	
	public String getName () {
		return this.variationName;
	}

	public Material getType () {
		return type;
	}

	public void setDropChance (Expression dropChance) {
		this.dropChanceCalculation = new CalculationNode("dropChance", Objects.requireNonNull(dropChance));
	}

	public void setAmount (Expression amount) {
		this.amountCalculation = new CalculationNode("amount", Objects.requireNonNull(amount));
	}

	public void setDamage (Expression damage) {
		this.damageCalculation = new CalculationNode("damage", Objects.requireNonNull(damage));
	}

	public void setData (Expression data) {
		this.dataCalculation = new CalculationNode("data", Objects.requireNonNull(data));
	}

	public void setType (Material type) {
		this.type = Objects.requireNonNull(type);
	}

	public double calculateDropChance (Map<String, Double> variables) {
		return this.dropChanceCalculation.calculateVariable(variables);
	}

	public int calculateAmount (Map<String, Double> variables) {
		return Math.toIntExact(Math.round(this.amountCalculation.calculateVariable(variables)));
	}

	public short calculateDamage (Map<String, Double> variables) {
		long damage = Math.round(this.damageCalculation.calculateVariable(variables));
        if ((short) damage != damage) {
            throw new ArithmeticException("short overflow");
        }
		return (short) damage;
	}

	public byte calculateData (Map<String, Double> variables) {
		long data = Math.round(this.dataCalculation.calculateVariable(variables));
        if ((byte) data != data) {
            throw new ArithmeticException("integer overflow");
        }
		return (byte) data;
	}

	public Optional<ItemStack> generateItemByChance(Map<String, Double> variables) {
		if (RANDOM.nextDouble() > calculateDropChance(variables)) {
			return Optional.empty();
		}
		int amount = calculateAmount(variables);
		short damage = calculateDamage(variables);
		byte data = calculateData(variables);
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(this.type, amount, damage, data);
		return Optional.of(item);
	}

	@Override
	public int hashCode () {
		int hash = 5;
		hash = 53 * hash + Objects.hashCode(this.variationName);
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
		final DropVariation other = (DropVariation) obj;
		return Objects.equals(this.variationName, other.getName());
	}
}
