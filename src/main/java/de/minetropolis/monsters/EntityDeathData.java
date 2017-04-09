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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 *
 */
public class EntityDeathData {

	private final int experience;
	private final List<ItemStack> drops;

	public EntityDeathData (int experience, List<ItemStack> drops) {
		this.experience = experience;
		this.drops = new ArrayList<>(drops);
	}
	
	public int getDroppedExp() {
		return experience;
	}
	
	public List<ItemStack> getDrops() {
		return Collections.unmodifiableList(drops);
	}
}
