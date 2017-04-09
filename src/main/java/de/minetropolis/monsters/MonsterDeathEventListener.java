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

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;

/**
 *
 */
public class MonsterDeathEventListener implements Listener {

	public MonsterDeathEventListener () {
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMonsterDeath(final EntityDeathEvent event) {
		LivingEntity entity= event.getEntity();
		if (!entity.hasMetadata("dynamicMonstersDrops")) {
			return;
		}
		MetadataValue meta = entity.getMetadata("dynamicMonstersDrops").get(0);
		if (!(meta.value() instanceof EntityDeathData)) {
			return;
		}
		EntityDeathData deathData = (EntityDeathData) meta.value();
		event.getDrops().addAll(deathData.getDrops());
		if (deathData.getDroppedExp() >= 0) {
			event.setDroppedExp(deathData.getDroppedExp());
		}
	}
}
