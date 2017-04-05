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


import de.minetropolis.monsters.math.AdditionalFunctions;
import de.minetropolis.monsters.math.CalculationNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * Parses the config.
 */
public final class ConfigurationParser {

    private final Plugin plugin;
    private Configuration config;
    private final AtomicBoolean parsed = new AtomicBoolean(false);

    /**
     * Create a parser for the given plugin.
     * 
     * @param plugin plugin to parse config for
     */
    public ConfigurationParser(Plugin plugin) {
        this.plugin = plugin;
    }


    /**
     * Checks whether the config is parsed or not.
     *
     * @return returns whether the config is parsed or not
     */
    public boolean isParsed() {
        return this.parsed.get();
    }

    /**
     * Loads and parses the current configuration.
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

    private void cleanUpOldParse() {
    }

    private void loadConfiguration() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
    }

    private void parseConfig() throws InvalidConfigurationException {
        ConfigurationSection worldsSection = ConfigurationUtil.loadOptionalConfigurationSection(config, "worlds")
                .orElseThrow(() -> new MissingEntryException("no active worlds"));
        ConfigurationSection entitiesSection = ConfigurationUtil.loadOptionalConfigurationSection(config, "entities")
                .orElseThrow(() -> new MissingEntryException("no active entities"));
        Map<String, SortedSet<CalculationNode>> worldsConfiguration = loadWorlds(worldsSection); // TODO use loaded values
        loadEntities(entitiesSection);
    }

    private Map<String, SortedSet<CalculationNode>> loadWorlds(ConfigurationSection worldsSection) throws InvalidConfigurationException {
        Map<String, ConfigurationSection> worldSections = ConfigurationUtil.loadConfigurationSectionGroup(worldsSection);
        if (worldSections.isEmpty()) {
            throw new MissingEntryException("no active worlds");
        }
        Map<String, SortedSet<CalculationNode>> worlds = new HashMap<>();
        for (String worldName : worldSections.keySet()) {
            worlds.put(worldName, loadWorld(worldSections.get(worldName)));
        }
        return worlds;
    }

    private SortedSet<CalculationNode> loadWorld(ConfigurationSection worldSection) throws InvalidConfigurationException {
        Map<String, ConfigurationSection> hotspotSections = ConfigurationUtil.loadConfigurationSectionGroup(worldSection);
        if (hotspotSections.isEmpty()) {
            throw new MissingEntryException("missing hotspot configuration");
        }
        Set<String> calculationVariables = worldSection.getKeys(false);
        if (!calculationVariables.contains("level")) {
            throw new MissingEntryException("no final variable 'level' defined");
        }
        if (calculationVariables.contains("x") || calculationVariables.contains("y") || calculationVariables.contains("z")) {
            throw new IllegalEntryTypeException("'x', 'y' and 'z' are reserved values");
        }
        Set<String> variables = new HashSet<>(calculationVariables);
        variables.add("x");
        variables.add("y");
        variables.add("z");
        SortedSet<CalculationNode> calculation = new TreeSet<>();
		for (String calculationStep : calculationVariables) {
			Expression expression = new ExpressionBuilder(ConfigurationUtil.loadString(worldSection, calculationStep))
                    .functions(AdditionalFunctions.getAdditionalFunctions())
                    .variables(variables)
                    .build();
            calculation.add(new CalculationNode(calculationStep, expression));
		}
        return Collections.unmodifiableSortedSet(calculation);
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
}
