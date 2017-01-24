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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

/**
 *
 */
public final class ConfigurationParser {

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
		return Collections.unmodifiableMap(this.entityModifications);
	}

	public void parseCurrentConfig () {
		this.parsed.set(false);
		cleanUpOldParse();
		loadConfiguration();
		try {
			parseConfig();
			this.parsed.set(true);
		} catch (InvalidConfigurationException exception) {
			this.plugin.getLogger()
					.log(java.util.logging.Level.SEVERE, "Invalid configuration: {0}", exception.getMessage());
		}

	}

	private void cleanUpOldParse () {
		this.entityModifications = new HashMap<>();
	}

	private void loadConfiguration () {
		this.plugin.saveDefaultConfig();
		this.plugin.reloadConfig();
		this.config = this.plugin.getConfig();
	}

	private void parseConfig () throws InvalidConfigurationException {
		final Map<String, Function<Location, Integer>> worldsLevelMethods = loadWorlds();
	}

	private Map<String, Function<Location, Integer>> loadWorlds () throws InvalidConfigurationException {
		final ConfigurationSection worldsSection = this.config.getConfigurationSection("worlds");
		if (worldsSection == null || worldsSection.getKeys(false).isEmpty()) {
			throw new InvalidConfigurationException("worlds are not defined");
		}
		final Set<String> worldNames = worldsSection.getKeys(false);
		final Map<String, Function<Location, Integer>> worlds = new HashMap<>();
		for (String worldName : worldNames) {
			final ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldName);
			worlds.put(worldName, loadWorld(worldName, worldSection));
		}
		return worlds;
	}

	private Function<Location, Integer> loadWorld (final String worldName, final ConfigurationSection worldSection)
			throws InvalidConfigurationException {
		if (worldSection == null
				|| !worldSection.isDouble("x") || !worldSection.isDouble("y") || !worldSection.isDouble("z")) {
			throw new InvalidConfigurationException("center of world " + worldName + " is not defined");
		}
		final double x = worldSection.getDouble("x");
		final double y = worldSection.getDouble("y");
		final double z = worldSection.getDouble("z");
		final BiFunction<Double, Double, Integer> horizontalLevel
				= loadHorizontalLevelMethod(worldSection, worldName, x, z);
		final Function<Double, Integer> verticalLevel = loadVerticalLevelMethod(worldSection, worldName, y);
		return loc -> 1 + horizontalLevel.apply(loc.getX(), loc.getZ()) + verticalLevel.apply(loc.getY());
	}

	private BiFunction<Double, Double, Integer> loadHorizontalLevelMethod (final ConfigurationSection worldSection,
																		   final String worldName,
																		   final double centerX, final double centerZ)
			throws InvalidConfigurationException {
		if (worldSection.contains("horizontal", true)) {
			if (!worldSection.isDouble("horizontal")) {
				throw new InvalidConfigurationException("horizontal level increase of world " + worldName
						+ " is not valid");
			}
			final double horizontal = worldSection.getDouble("horizontal");
			if (horizontal < +0.0) {
				throw new InvalidConfigurationException("horizontal level-increase per meter of world " + worldName
						+ " may not be negative");
			}
			if (horizontal > +0.0) {
				try {
					final String rawMethod = worldSection.getString("distance-method", "MINECRAFT");
					final DistanceMethod distanceMethod = DistanceMethod.valueOf(rawMethod);
					return (x, z) -> floorInteger(distanceMethod.distanceOf(centerX, centerZ, x, z) * horizontal);
				} catch (IllegalArgumentException exception) {
					throw new InvalidConfigurationException("distance-method of world " + worldName + " is not valid",
															exception);
				}
			}
		}
		return (x, z) -> 0;
	}

	private Function<Double, Integer> loadVerticalLevelMethod (final ConfigurationSection worldSection,
															   final String worldName, double centerY)
			throws InvalidConfigurationException {
		if (worldSection.contains("vertical", true)) {
			if (!worldSection.isDouble("vertical")) {
				throw new InvalidConfigurationException("vertical level increase of world " + worldName
						+ " is not valid");
			}
			final double vertical = worldSection.getDouble("vertical");
			if (vertical < +0.0) {
				throw new InvalidConfigurationException("vertical level-increase per meter of world " + worldName
						+ " may not be negative");
			}
			if (vertical > +0.0) {
				if (worldSection.getBoolean("upwards-increase", false)) {
					return y -> floorInteger(Math.abs(centerY - y) * vertical);
				} else {
					return y -> floorInteger(Math.max(centerY - y, +0.0) * vertical);
				}
			}
		}
		return y -> 0;
	}

	public static int floorInteger (double value) {
		final long exact = Math.round(Math.floor(value));
		if (exact > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else if (exact < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		} else {
			return (int) exact;
		}
	}
}
