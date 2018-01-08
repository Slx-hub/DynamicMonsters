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

import de.minetropolis.monsters.math.Calculation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

	private final Map<String, Calculation> worlds = new HashMap<>();
	private final Map<EntityType, Set<EntityVariation>> entities = new HashMap<>();

	/**
	 *
	 */
	public MonsterSpawnEventListener () {
	}

	public void setWorldsConfiguration (Map<String, Calculation> worldsConfiguration) {
		this.worlds.clear();
		this.worlds.putAll(worldsConfiguration);
	}

	public void setEntitiesConfiguration (Map<EntityType, Set<EntityVariation>> entitiesConfiguration) {
		this.entities.clear();
		this.entities.putAll(entitiesConfiguration);
	}

	/**
	 *
	 * @param spawnEvent
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMonsterSpawn (final CreatureSpawnEvent spawnEvent) {
		LivingEntity entity = spawnEvent.getEntity();
		String world = entity.getWorld().getName();
		EntityType type = entity.getType();
		if (!this.worlds.containsKey(world) || !this.entities.containsKey(type)) {
			return;
		}
		Map<String, Double> variables = new HashMap<>();
		variables.put("x", entity.getLocation().getX());
		variables.put("y", entity.getLocation().getY());
		variables.put("z", entity.getLocation().getZ());
		worlds.get(world).executeCalculation(variables);

		int level = Math.toIntExact(Math.round(variables.get("level")));

		EntityVariation.modifyEntityWeighted(entities.get(type), entity, variables, level);
	}
}
