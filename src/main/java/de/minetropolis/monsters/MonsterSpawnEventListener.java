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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 */
public final class MonsterSpawnEventListener implements Listener {

	private final Map<EntityType, Consumer<LivingEntity>> modifications;

	/**
	 *
	 * @param modifications
	 */
	public MonsterSpawnEventListener (final Map<EntityType, Consumer<LivingEntity>> modifications) {
		this.modifications = new HashMap<>(modifications);
		this.modifications.forEach((type, mod) -> this.modifications.remove(type, null));
	}

	/**
	 *
	 * @param spanwEvent
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMonsterSpawn (final CreatureSpawnEvent spanwEvent) {
		final EntityType type = spanwEvent.getEntityType();
		if (this.modifications.containsKey(type) && this.modifications.get(type) != null) {
			this.modifications.get(type).accept(spanwEvent.getEntity());
		}
	}
}
