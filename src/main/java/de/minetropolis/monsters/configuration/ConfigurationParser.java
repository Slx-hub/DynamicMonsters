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

import de.minetropolis.monsters.DropVariation;
import de.minetropolis.monsters.EntityVariation;
import de.minetropolis.monsters.math.AdditionalMathOperations;
import de.minetropolis.monsters.math.Calculation;
import de.minetropolis.monsters.math.CalculationNode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

/**
 * Parses the config.
 */
public final class ConfigurationParser {

	private final Lock lock = new ReentrantLock();
	private final AtomicBoolean parsed = new AtomicBoolean(false);

	private final Plugin plugin;
	private Configuration config;

	private Map<String, Calculation> worldsConfiguration;
	private Map<EntityType, Set<EntityVariation>> entitiesConfiguration;

	/**
	 * Create a parser for the given plugin.
	 *
	 * @param plugin plugin to parse config for
	 */
	public ConfigurationParser (Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Checks whether the config is parsed or not.
	 *
	 * @return returns whether the config is parsed or not
	 */
	public boolean isParsed () {
		if (lock.tryLock()) {
			boolean isParsed;
			try {
				isParsed = this.parsed.get();
			} finally {
				lock.unlock();
			}
			return isParsed;
		} else {
			return false;
		}
	}

	public Map<String, Calculation> getWorldsConfiguration () {
		if (lock.tryLock()) {
			Map<String, Calculation> configuration = null;
			try {
				if (this.parsed.get()) {
					configuration = Collections.unmodifiableMap(this.worldsConfiguration);
				}
			} finally {
				lock.unlock();
			}
			return configuration;
		} else {
			return null;
		}
	}

	public Map<EntityType, Set<EntityVariation>> getEntitiesConfiguration () {
		if (lock.tryLock()) {
			Map<EntityType, Set<EntityVariation>> configuration = null;
			try {
				if (this.parsed.get()) {
					configuration = Collections.unmodifiableMap(this.entitiesConfiguration);
				}
			} finally {
				lock.unlock();
			}
			return configuration;
		} else {
			return null;
		}
	}

	/**
	 * Loads and parses the current configuration.
	 */
	public void parseCurrentConfig () {
		if (lock.tryLock()) {
			try {
				this.parsed.set(false);
				//cleanUpOldParse();
				loadConfiguration();
				parseConfig();
				this.parsed.set(true);
			} catch (InvalidConfigurationException exception) {
				this.plugin.getLogger().log(Level.SEVERE, "Invalid configuration: {0}", exception.getMessage());
				this.plugin.getLogger().log(Level.FINE, null, exception);
			} finally {
				lock.unlock();
			}
		} else {
			throw new IllegalStateException("already parsing");
		}
	}

	private void cleanUpOldParse () {
		this.worldsConfiguration = null;
		this.entitiesConfiguration = null;
	}

	private void loadConfiguration () {
		this.plugin.saveDefaultConfig();
		this.plugin.reloadConfig();
		this.config = this.plugin.getConfig();
	}

	private void parseConfig () throws InvalidConfigurationException {
		ConfigurationSection worldsSection = ConfigurationUtil.loadOptionalConfigurationSection(config, "worlds")
				.orElseThrow(() -> new MissingEntryException("no active worlds"));
		ConfigurationSection entitiesSection = ConfigurationUtil.loadOptionalConfigurationSection(config, "entities")
				.orElseThrow(() -> new MissingEntryException("no active entities"));
		this.worldsConfiguration = loadWorlds(worldsSection);
		this.entitiesConfiguration = loadEntities(entitiesSection);
	}

	private Map<String, Calculation> loadWorlds (ConfigurationSection worldsSection) throws InvalidConfigurationException {
		Map<String, ConfigurationSection> worldSections = ConfigurationUtil.loadConfigurationSectionGroup(worldsSection);
		if (worldSections.isEmpty()) {
			throw new MissingEntryException("no active worlds");
		}
		Map<String, Calculation> worlds = new HashMap<>();
		for (String worldName : worldSections.keySet()) {
			worlds.put(worldName, loadWorld(worldSections.get(worldName)));
		}
		return worlds;
	}

	private Calculation loadWorld (ConfigurationSection worldSection) throws InvalidConfigurationException {
		Set<String> calculationVariables = worldSection.getKeys(false);
		if (!calculationVariables.contains("level")) {
			throw new MissingEntryException("no final variable 'level' defined");
		}
		if (calculationVariables.contains("x") || calculationVariables.contains("y") || calculationVariables.contains("z")) {
			throw new IllegalEntryTypeException("'x', 'y' and 'z' are reserved values");
		}
		Set<String> variables = new HashSet<>();
		variables.add("x");
		variables.add("y");
		variables.add("z");
		Calculation calculation = new Calculation(variables);
		variables.addAll(calculationVariables);
		for (String calculationStep : calculationVariables) {
			Expression expression = new ExpressionBuilder(ConfigurationUtil.loadString(worldSection, calculationStep))
					.functions(AdditionalMathOperations.getAdditionalFunctions())
					.variables(variables)
					.build();
			calculation.addNode(new CalculationNode(calculationStep, expression));
		}
		return calculation;
	}

	private Map<EntityType, Set<EntityVariation>> loadEntities (ConfigurationSection entitiesSection) throws InvalidConfigurationException {
		Map<String, ConfigurationSection> entitySections = ConfigurationUtil.loadConfigurationSectionGroup(entitiesSection);
		if (entitySections.isEmpty()) {
			throw new MissingEntryException("no active entities");
		}
		Map<EntityType, Set<EntityVariation>> entities = new HashMap<>();
		for (String entityType : entitySections.keySet()) {
			entities.put(EntityType.valueOf(entityType), loadEntityVariations(entitySections.get(entityType)));
		}
		return entities;
	}

	private Set<EntityVariation> loadEntityVariations (ConfigurationSection entitySection) throws InvalidConfigurationException {
		Map<String, ConfigurationSection> variationSections = ConfigurationUtil.loadConfigurationSectionGroup(entitySection);
		if (variationSections.isEmpty()) {
			throw new MissingEntryException("no active entities");
		}
		Set<EntityVariation> variations = new HashSet<>();
		for (String variationName : variationSections.keySet()) {
			variations.add(loadVariation(variationSections.get(variationName), variationName));
		}
		return variations;
	}

	private EntityVariation loadVariation (ConfigurationSection variationSection, String variationName) throws InvalidConfigurationException {
		Expression weight = createExpressionOf(ConfigurationUtil.loadString(variationSection, "weight"), new HashSet<>(Arrays.asList("level", "x", "y", "z")));
		EntityVariation variation = new EntityVariation(variationName, weight, plugin);
		variation.setNameVisible(ConfigurationUtil.loadBoolean(variationSection, "name-visible", false));
		variation.setNamePattern(ConfigurationUtil.loadString(variationSection, "name", null));
		variation.setExpDrop(createExpressionOf(ConfigurationUtil.loadString(variationSection, "experience", "-1"), new HashSet<>(Arrays.asList("level", "x", "y", "z"))));
		Optional<ConfigurationSection> drops = ConfigurationUtil.loadOptionalConfigurationSection(variationSection, "loot");
		if (drops.isPresent()) {
			loadDrops(drops.get(), variation);
		}
		Optional<ConfigurationSection> attributes = ConfigurationUtil.loadOptionalConfigurationSection(variationSection, "attributes");
		if (attributes.isPresent()) {
			loadAttributes(attributes.get(), variation);
		}
		return variation;
	}

	private void loadDrops (ConfigurationSection dropsSection, EntityVariation variation) throws InvalidConfigurationException {
		Map<String, ConfigurationSection> dropSections = ConfigurationUtil.loadConfigurationSectionGroup(dropsSection);
		if (dropSections.isEmpty()) {
			return;
		}
		for (String dropIdentifier : dropSections.keySet()) {
			variation.addDrop(loadDrop(dropSections.get(dropIdentifier), dropIdentifier));
		}
	}

	private DropVariation loadDrop (ConfigurationSection dropSection, String dropIdentifier) throws InvalidConfigurationException {
		Material type = ConfigurationUtil.loadEnumValue(dropSection, "type", Material.class);
		Expression dropChance = createExpressionOf(ConfigurationUtil.loadString(dropSection, "drop-chance", "1"), new HashSet<>(Arrays.asList("level", "x", "y", "z")));
		DropVariation drop = new DropVariation(dropIdentifier, dropChance, type);
        drop.setName(ConfigurationUtil.loadString(dropSection, "item-name", null));
        drop.setLore(ConfigurationUtil.loadString(dropSection, "item-lore", null));
		drop.setAmount(createExpressionOf(ConfigurationUtil.loadString(dropSection, "amount", "1"), new HashSet<>(Arrays.asList("level", "x", "y", "z"))));
		drop.setDamage(createExpressionOf(ConfigurationUtil.loadString(dropSection, "damage", "0"), new HashSet<>(Arrays.asList("level", "x", "y", "z"))));
		drop.setData(createExpressionOf(ConfigurationUtil.loadString(dropSection, "data", "0"), new HashSet<>(Arrays.asList("level", "x", "y", "z"))));
		return drop;
	}

	private void loadAttributes (ConfigurationSection attributesSection, EntityVariation variation) throws InvalidConfigurationException {
		Set<String> attributes = attributesSection.getKeys(false);
		for (String attribute : attributes) {
			variation.addAttribute(Attribute.valueOf(attribute), createExpressionOf(ConfigurationUtil.loadString(attributesSection, attribute), new HashSet<>(Arrays.asList("level", "x", "y", "z"))));
		}
	}

	private Expression createExpressionOf (String expression, Set<String> variables) {
		return new ExpressionBuilder(expression).operator(AdditionalMathOperations.getAdditionalOperator())
				.functions(AdditionalMathOperations.getAdditionalFunctions()).variables(variables).build();
	}
}
