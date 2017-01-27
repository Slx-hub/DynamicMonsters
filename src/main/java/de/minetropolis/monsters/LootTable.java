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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.inventory.ItemStack;

/**
 *
 */
public final class LootTable {

	private final boolean override;
	private final Set<Supplier<ItemStack>> lootSuppliers;
	
	public LootTable (boolean override, Set<Supplier<ItemStack>> lootSuppliers) {
		this.override = override;
		this.lootSuppliers = Objects.requireNonNull(lootSuppliers);
	}

	public List<ItemStack> getLoot() {
		return lootSuppliers.stream()
				.map(supplier -> supplier.get())
				.filter(item -> item != null)
				.collect(Collectors.toList());
	}
	
	public boolean isOverriding() {
		return override;
	}
}
