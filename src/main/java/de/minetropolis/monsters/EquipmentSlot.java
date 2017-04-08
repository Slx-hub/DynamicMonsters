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

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 *
 */
public enum EquipmentSlot {
	MAIN_HAND((equipment, item) -> equipment.setItemInMainHand(item),
			  (equipment, chance) -> equipment.setItemInMainHandDropChance(chance),
			  equipment -> equipment.getItemInMainHand(),
			  equipment -> equipment.getItemInMainHandDropChance()),
	OFF_HAND((equipment, item) -> equipment.setItemInOffHand(item),
			 (equipment, chance) -> equipment.setItemInOffHandDropChance(chance),
			 equipment -> equipment.getItemInOffHand(),
			 equipment -> equipment.getItemInOffHandDropChance()),
	HELMET((equipment, item) -> equipment.setHelmet(item),
		   (equipment, chance) -> equipment.setHelmetDropChance(chance),
		   equipment -> equipment.getHelmet(),
		   equipment -> equipment.getHelmetDropChance()),
	CHESTPLATE((equipment, item) -> equipment.setChestplate(item),
			   (equipment, chance) -> equipment.setChestplateDropChance(chance),
			   equipment -> equipment.getChestplate(),
			   equipment -> equipment.getChestplateDropChance()),
	LEGGINGS((equipment, item) -> equipment.setLeggings(item),
			 (equipment, chance) -> equipment.setLeggingsDropChance(chance),
			 equipment -> equipment.getLeggings(),
			 equipment -> equipment.getLeggingsDropChance()),
	BOOTS((equipment, item) -> equipment.setBoots(item),
		  (equipment, chance) -> equipment.setBootsDropChance(chance),
		  equipment -> equipment.getBoots(),
		  equipment -> equipment.getBootsDropChance()),;

	private final BiConsumer<EntityEquipment, ItemStack> setEquipment;
	private final ObjFloatConsumer<EntityEquipment> setDropChance;
	private final Function<EntityEquipment, ItemStack> getEquipment;
	private final ToFloatFunction<EntityEquipment> getDropChance;

	private EquipmentSlot (BiConsumer<EntityEquipment, ItemStack> setEquipment,
						   ObjFloatConsumer<EntityEquipment> setDropChance,
						   Function<EntityEquipment, ItemStack> getEquipment,
						   ToFloatFunction<EntityEquipment> getDropChance) {
		this.setEquipment = setEquipment;
		this.setDropChance = setDropChance;
		this.getEquipment = getEquipment;
		this.getDropChance = getDropChance;
	}

	public void setEquipment (EntityEquipment equipment, ItemStack item) {
		this.setEquipment.accept(equipment, item);
	}

	public void setDropChance (EntityEquipment equipment, float chance) {
		this.setDropChance.accept(equipment, chance);
	}

	public ItemStack getEquipment (EntityEquipment equipment) {
		return this.getEquipment.apply(equipment);
	}

	public float getDropChance (EntityEquipment equipment) {
		return this.getDropChance.apply(equipment);
	}

	private interface ObjFloatConsumer<T> {

		void accept (T t, float f);
	}

	private interface ToFloatFunction<T> {

		float apply (T t);
	}
}
