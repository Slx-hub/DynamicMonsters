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

import java.util.*;
import java.util.function.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 *
 */
public class MonsterSpawnEventListener implements Listener {

	private final Map<EntityType, Consumer<Entity>> modifications;
	
	public MonsterSpawnEventListener (Map<EntityType, Consumer<Entity>> modifications) {
		this.modifications = new HashMap<>(modifications);
		this.modifications.forEach((type, mod) -> this.modifications.remove(type, null));
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMonsterSpawn(CreatureSpawnEvent spanwEvent) {
		EntityType type = spanwEvent.getEntityType();
		if (modifications.containsKey(type) && modifications.get(type) != null) {
			modifications.get(type).accept(spanwEvent.getEntity());
		}
	}
}
