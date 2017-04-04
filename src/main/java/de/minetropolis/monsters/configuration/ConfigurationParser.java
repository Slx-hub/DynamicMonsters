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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * parses the config.
 */
public final class ConfigurationParser {

    /**
     * plugin parser is used for.
     */
    private final Plugin plugin;
    /**
     * configuration.
     */
    private Configuration config;
    /**
     * boolean to check if config has been parsed.
     */
    private final AtomicBoolean parsed = new AtomicBoolean(false);

    /**
     * spawnrules.
     */
    private Map<EntityType, Consumer<LivingEntity>> spawnRules;

    /**
     * @param plugin plugin parser is used for.
     */
    public ConfigurationParser(Plugin plugin) {
        this.plugin = plugin;
    }


    /**
     * retrieves spawnrules from parsed config.
     *
     * @return ???
     */
    public Map<EntityType, Consumer<LivingEntity>> getSpawnRules() {
        if (!parsed.get()) {
            throw new IllegalStateException("config not parsed");
        }
        return Collections.unmodifiableMap(spawnRules);
    }

    /**
     * checks whether the config is parsed or not.
     *
     * @return returns whether the config is parsed or not
     */
    public boolean isParsed() {
        return this.parsed.get();
    }

    /**
     * parses configuration.
     */
    public void parseCurrentConfig() {
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

    /**
     * removes previous spawnrules.
     */
    private void cleanUpOldParse() {
        this.spawnRules = new HashMap<>();
    }

    /**
     * retrieves config from plugin.
     */
    private void loadConfiguration() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
    }

    /**
     * parses the config.
     *
     * @throws InvalidConfigurationException exception
     */
    private void parseConfig() throws InvalidConfigurationException {
        ConfigurationSection worldsSection = ConfigurationUtil.loadOptionalConfigurationSection(config, "worlds")
                .orElseThrow(() -> new MissingEntryException("no active worlds"));
        ConfigurationSection entitiesSection = ConfigurationUtil.loadOptionalConfigurationSection(config, "entities")
                .orElseThrow(() -> new MissingEntryException("no active entities"));
        Map<String, Set<Hotspot>> worldsConfiguration = loadWorlds(worldsSection); // TODO use loaded values
        loadEntities(entitiesSection);
    }

    /**
     * merges returned sets from loadWorld into a map.
     *
     * @param worldsSection worlds-section loaded from config
     * @return successfully parsed worlds
     * @throws InvalidConfigurationException exception
     */
    private Map<String, Set<Hotspot>> loadWorlds(ConfigurationSection worldsSection) throws InvalidConfigurationException {
        Map<String, ConfigurationSection> worldSections = ConfigurationUtil.loadConfigurationSectionGroup(worldsSection);
        if (worldSections.isEmpty()) {
            throw new MissingEntryException("no active worlds");
        }
        Map<String, Set<Hotspot>> worlds = new HashMap<>();
        for (String worldName : worldSections.keySet()) {
            worlds.put(worldName, loadWorld(worldSections.get(worldName)));
        }
        return worlds;
    }

    /**
     * converts world-specifications into hotspots.
     *
     * @param worldSection worlds-section loaded from config
     * @return hotspots from world
     * @throws InvalidConfigurationException exception
     */
    private Set<Hotspot> loadWorld(ConfigurationSection worldSection) throws InvalidConfigurationException {
        Map<String, ConfigurationSection> hotspotSections = ConfigurationUtil.loadConfigurationSectionGroup(worldSection);
        if (hotspotSections.isEmpty()) {
            throw new MissingEntryException("missing hotspot configuration");
        }
        Set<Hotspot> hotspots = new HashSet<>();
        for (String hotspotName : hotspotSections.keySet()) {
            hotspots.add(loadHotspot(hotspotSections.get(hotspotName)));
        }
        return hotspots;
    }

    /**
     * TODO use loaded values
     * generates hotspot from config-specifications.
     *
     * @param hotspotSection hotspot-section loaded from config
     * @return hotspot
     * @throws InvalidConfigurationException exception
     */
    private Hotspot loadHotspot(ConfigurationSection hotspotSection) throws InvalidConfigurationException {
        final Vector center = loadCenter(ConfigurationUtil.loadConfigurationSection(hotspotSection, "center"));
        final Hotspot hotspot = new Hotspot(hotspotSection.getName(), center);
        final int baseLevel = ConfigurationUtil.loadInteger(hotspotSection, "base-level", 0); // TODO use loaded values
        hotspot.setBaseLevel(baseLevel);
        Optional<ConfigurationSection> horizontal = ConfigurationUtil.loadOptionalConfigurationSection(hotspotSection, "horizontal");
        Optional<ConfigurationSection> vertical = ConfigurationUtil.loadOptionalConfigurationSection(hotspotSection, "vertical");
        if (!horizontal.isPresent() && !vertical.isPresent()) {
            throw new MissingEntryException("missing level change configuration");
        }
        if (horizontal.isPresent()) {
            hotspot.addLevelChangeStrategy(loadHorizontal(horizontal.get()));
        }
        if (vertical.isPresent()) {
            hotspot.addLevelChangeStrategy(loadVertical(vertical.get()));
        }
        Optional<ConfigurationSection> bordersSection = ConfigurationUtil.loadOptionalConfigurationSection(hotspotSection, "borders");
        if (bordersSection.isPresent()) {
            hotspot.setLevelBorder(loadBorders(bordersSection.get()));
        }
        return hotspot;
    }

    /**
     * parses center of world.
     *
     * @param centerSection config-section of center
     * @return vector of center
     * @throws InvalidConfigurationException exception
     */
    private Vector loadCenter(ConfigurationSection centerSection) throws InvalidConfigurationException {
        final double x = ConfigurationUtil.loadDouble(centerSection, "x");
        final double y = ConfigurationUtil.loadDouble(centerSection, "y");
        final double z = ConfigurationUtil.loadDouble(centerSection, "z");
        return new Vector(x, y, z);
    }

    /**
     * loads the horizontal section.
     *
     * @param horizontalSection config-section of horizontal specifications
     * @return level change strategy
     * @throws InvalidConfigurationException exception
     */
    private LevelChangeStrategy loadHorizontal(ConfigurationSection horizontalSection) throws InvalidConfigurationException {
        final DistanceMethod method = ConfigurationUtil.loadEnumValue(horizontalSection, "distance-method", DistanceMethod.class, DistanceMethod.MINECRAFT);
        return loadLevelChange(horizontalSection, (c, d, o, b) -> new HorizontalLevelChangeStrategy(c, d, o, b, method));
    }

    /**
     * loads the vertical section.
     *
     * @param verticalSection config-section of vertical specifications
     * @return level change strategy
     * @throws InvalidConfigurationException exception
     */
    private LevelChangeStrategy loadVertical(ConfigurationSection verticalSection) throws InvalidConfigurationException {
        return loadLevelChange(verticalSection, (c, d, o, b) -> new VerticalLevelChangeStrategy(c, d, o, b));
    }

    /**
     * loads the level change.
     *
     * @param changeSection config-section of level change specifications
     * @param factory       factory to create LevelChangeStrategys
     * @return level change strategy
     * @throws InvalidConfigurationException exception
     */
    private LevelChangeStrategy loadLevelChange(ConfigurationSection changeSection, LevelChangeFactory factory) throws InvalidConfigurationException {
        final int levelChange = ConfigurationUtil.loadInteger(changeSection, "level-change");
        final double distancePerChange = ConfigurationUtil.loadDouble(changeSection, "distance-per-change");
        final double distanceOffset = ConfigurationUtil.loadDouble(changeSection, "distance-offset", 0.0);
        final LevelBorder border;
        Optional<ConfigurationSection> bordersSection = ConfigurationUtil.loadOptionalConfigurationSection(changeSection, "borders");
        if (bordersSection.isPresent()) {
            border = loadBorders(bordersSection.get());
        } else {
            border = new LevelBorder();
        }
        return factory.create(levelChange, distancePerChange, distanceOffset, border);
    }

    /**
     * loads the borders.
     *
     * @param bordersSection config-section of border specifications
     * @return border
     * @throws InvalidConfigurationException exception
     */
    private LevelBorder loadBorders(ConfigurationSection bordersSection) throws InvalidConfigurationException {
        return new LevelBorder(ConfigurationUtil.loadOptionalInteger(bordersSection, "min"), ConfigurationUtil.loadOptionalInteger(bordersSection, "max"));
    }

    /**
     * TODO Not supported yet
     * loads the entities.
     *
     * @param entitiesSection config-section of entity specifications
     */
    private void loadEntities(ConfigurationSection entitiesSection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * creates LevelChangeStrategys.
     */
    private interface LevelChangeFactory {

        /**
         * creates LevelChangeStrategy.
         *
         * @param levelChange       amount of levels added per step
         * @param distancePerChange distance between steps
         * @param distanceOffset    offsets distance/ and thus, level calculation
         * @param border            min/max level for mobs
         * @return level change strategy
         */
        public abstract LevelChangeStrategy create(int levelChange, double distancePerChange, double distanceOffset, LevelBorder border);

    }
}
