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
package de.minetropolis.monsters.configuration;

import de.minetropolis.monsters.DistanceMethod;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public final class ConfigurationParser {

	private final Plugin plugin;
	private Configuration config;
	private final AtomicBoolean parsed = new AtomicBoolean(false);

	private Map<EntityType, Consumer<LivingEntity>> spawnRules;

	public ConfigurationParser (Plugin plugin) {
		this.plugin = plugin;
	}

	public Map<EntityType, Consumer<LivingEntity>> getSpawnRules () {
		if (!parsed.get()) {
			throw new IllegalStateException("config not parsed");
		}
		return Collections.unmodifiableMap(spawnRules);
	}

	public boolean isParsed () {
		return this.parsed.get();
	}

	public void parseCurrentConfig () {
		this.parsed.set(false);
		cleanUpOldParse();
		loadConfiguration();
		try {
			parseConfig();
			this.parsed.set(true);
		} catch (InvalidConfigurationException exception) {
			this.plugin.getLogger().log(Level.SEVERE, "Invalid configuration: " + exception.getMessage(), exception);
			this.plugin.getLogger().log(Level.SEVERE, "Disabling plugin.");
		}

	}

	private void cleanUpOldParse () {
		this.spawnRules = new HashMap<>();
	}

	private void loadConfiguration () {
		this.plugin.saveDefaultConfig();
		this.plugin.reloadConfig();
		this.config = this.plugin.getConfig();
	}

	private void parseConfig () throws InvalidConfigurationException {
		loadConfig();
	}

	private void loadConfig () throws InvalidConfigurationException {
		ConfigurationSection worldsSection = ConfigurationUtil.loadOptionalConfigurationSection(config, "worlds")
				.orElseThrow(() -> new MissingEntryException("No active worlds defined."));
		ConfigurationSection entitiesSection = ConfigurationUtil.loadOptionalConfigurationSection(config, "entities")
				.orElseThrow(() -> new MissingEntryException("No active entities defined."));
		loadWorlds(worldsSection);
		loadEntities();
	}
	
	private void loadWorlds (ConfigurationSection worldsSection) throws InvalidConfigurationException {
		Map<String, ConfigurationSection> worldSections = ConfigurationUtil.loadConfigurationSectionGroup(worldsSection);
		if (worldSections.isEmpty()) {
			throw new MissingEntryException("No world configuration found.");
		}
		for (String worldName : worldSections.keySet()) {
			loadWorld(worldSections.get(worldName));
		}
	}

	private void loadWorld (ConfigurationSection worldSection) throws InvalidConfigurationException {
		Map<String, ConfigurationSection> hotspotSections = ConfigurationUtil.loadConfigurationSectionGroup(worldSection);
		for (String hotspotName : hotspotSections.keySet()) {
			loadHotspot(hotspotSections.get(hotspotName));
		}
	}

	private void loadHotspot (ConfigurationSection hotspotSection) throws InvalidConfigurationException {
		final Vector center = loadCenter(ConfigurationUtil.loadConfigurationSection(hotspotSection, "center"));
		final int baseLevel = ConfigurationUtil.loadInteger(hotspotSection, "base-level", 0); // TODO use loaded values
		Optional<ConfigurationSection> horizontal = ConfigurationUtil.loadOptionalConfigurationSection(hotspotSection, "horizontal");
		Optional<ConfigurationSection> vertical = ConfigurationUtil.loadOptionalConfigurationSection(hotspotSection, "vertical");
		if (!horizontal.isPresent() && !vertical.isPresent()) {
			throw new MissingEntryException("No modifier section (horizontal/vertical) found.");
		}
		if (horizontal.isPresent()) {
			loadHorizontal(horizontal.get());
		}
		if (vertical.isPresent()) {
			loadVertical(vertical.get());
		}
		final LevelBorder border;
		Optional<ConfigurationSection> bordersSection = ConfigurationUtil.loadOptionalConfigurationSection(hotspotSection, "borders");
		if (bordersSection.isPresent()) {
			border = loadBorders(bordersSection.get());
		} else {
			border = new LevelBorder();
		}
	}

	private Vector loadCenter (ConfigurationSection centerSection) throws InvalidConfigurationException {
		final double x = ConfigurationUtil.loadDouble(centerSection, "x");
		final double y = ConfigurationUtil.loadDouble(centerSection, "y");
		final double z = ConfigurationUtil.loadDouble(centerSection, "z");
		return new Vector(x, y, z);
	}

	private void loadHorizontal (ConfigurationSection horizontalSection) throws InvalidConfigurationException {
		loadLevelChange(horizontalSection);
		final DistanceMethod method = ConfigurationUtil.loadEnumValue(horizontalSection, "distance-method", DistanceMethod.class, DistanceMethod.MINECRAFT);
		
	}

	private void loadVertical (ConfigurationSection verticalSection) throws InvalidConfigurationException {
		loadLevelChange(verticalSection);
		Optional<ConfigurationSection> increaseSection = ConfigurationUtil.loadOptionalConfigurationSection(verticalSection, "increase");
		if (increaseSection.isPresent()) {
			loadIncrease(increaseSection.get());
		}
	}
	
	private void loadIncrease (ConfigurationSection increaseSection) throws InvalidConfigurationException {
		final boolean upwards = ConfigurationUtil.loadBoolean(increaseSection, "upwards", true); // TODO use loaded values
		final boolean downwards = ConfigurationUtil.loadBoolean(increaseSection, "downwards", true); // TODO use loaded values
	}
	
	private void loadLevelChange (ConfigurationSection changeSection) throws InvalidConfigurationException {
		final int levelChange = ConfigurationUtil.loadInteger(changeSection, "level-change"); // TODO use loaded values
		final double distancePerChange = ConfigurationUtil.loadDouble(changeSection, "distance-per-change"); // TODO use loaded values
		final double distanceOffset = ConfigurationUtil.loadDouble(changeSection, "distance-offset", 0.0); // TODO use loaded values
		final LevelBorder border; // TODO use loaded values
		Optional<ConfigurationSection> bordersSection = ConfigurationUtil.loadOptionalConfigurationSection(changeSection, "borders");
		if (bordersSection.isPresent()) {
			border = loadBorders(bordersSection.get());
		} else {
			border = new LevelBorder();
		}
	}
	
	private LevelBorder loadBorders (ConfigurationSection bordersSection) throws InvalidConfigurationException {
		return new LevelBorder(ConfigurationUtil.loadOptionalInteger(bordersSection, "min"), ConfigurationUtil.loadOptionalInteger(bordersSection, "max"));
	}
	
	private void loadEntities () {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
