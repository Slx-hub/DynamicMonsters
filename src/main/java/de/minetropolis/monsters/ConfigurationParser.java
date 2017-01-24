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
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

/**
 *
 */
public class ConfigurationParser {

	private final Plugin plugin;
	private Configuration config;
	private final AtomicBoolean parsed = new AtomicBoolean(false);

	private Map<EntityType, Consumer<Entity>> entityModifications;

	public ConfigurationParser (Plugin plugin) {
		this.plugin = plugin;
	}

	public boolean isParsed () {
		return this.parsed.get();
	}

	public Map<EntityType, Consumer<Entity>> getEntityModifications () {
		if (!this.parsed.get()) {
			throw new IllegalStateException("not parsed");
		}
		return Collections.unmodifiableMap(entityModifications);
	}

	public void parseCurrentConfig () {
		parsed.set(false);
		cleanUpOldParse();
		loadConfiguration();
		try {
			parseConfig();
			parsed.set(true);
		} catch (InvalidConfigurationException exception) {
			plugin.getLogger().log(Level.SEVERE, "Invalid configuration: {0}", exception.getMessage());
		}
		
	}

	private void cleanUpOldParse () {
		this.entityModifications = new HashMap<>();
	}

	private void loadConfiguration () {
		plugin.saveDefaultConfig();
		plugin.reloadConfig();
		this.config = plugin.getConfig();
	}

	private boolean isValidEntityTypeSection (String entity) {
		return true;
	}

	private void parseConfig () throws InvalidConfigurationException {
		throw new InvalidConfigurationException("centers has no words defined");
	}
}
