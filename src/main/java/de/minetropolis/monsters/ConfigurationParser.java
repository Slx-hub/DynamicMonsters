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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

/**
 *
 */
public final class ConfigurationParser {

	private final Plugin plugin;
	private Configuration config;
	private final AtomicBoolean parsed = new AtomicBoolean(false);

	private Map<EntityType, Consumer<LivingEntity>> spawnRules;
	
	/**
	 *
	 * @param plugin
	 */
	public ConfigurationParser (Plugin plugin) {
		this.plugin = plugin;
	}


	public Map<EntityType, Consumer<LivingEntity>> getSpawnRules () {
		if (!parsed.get()) {
			throw new IllegalStateException("config not parsed");
		}
		return Collections.unmodifiableMap(spawnRules);
	}

	/**
	 *
	 * @return
	 */
	public boolean isParsed () {
		return this.parsed.get();
	}

	/**
	 *
	 */
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
		this.spawnRules = new HashMap<>();
	}

	private void loadConfiguration () {
		this.plugin.saveDefaultConfig();
		this.plugin.reloadConfig();
		this.config = this.plugin.getConfig();
	}

	private void parseConfig () throws InvalidConfigurationException {
		final Map<String, ToIntFunction<Location>> worldsLevelMethods = loadWorlds();
		final Map<EntityType, Map<String, Variant>> entityVariantMethods = loadEntities();
		
		entityVariantMethods.forEach((type, variantMap) -> spawnRules.put(type, entity -> variantMap.values().stream().findAny().get().accept(entity, worldsLevelMethods.get(entity.getLocation().getWorld().getName()).applyAsInt(entity.getLocation()))));
	}

	private Map<String, ToIntFunction<Location>> loadWorlds () throws InvalidConfigurationException {
		final ConfigurationSection worldsSection = this.config.getConfigurationSection("worlds");
		if (worldsSection == null || worldsSection.getKeys(false).isEmpty()) {
			throw new InvalidConfigurationException("worlds are not defined");
		}
		final Set<String> worldNames = worldsSection.getKeys(false);
		final Map<String, ToIntFunction<Location>> worlds = new HashMap<>();
		for (final String worldName : worldNames) {
			final ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldName);
			if (worldSection == null) {
				throw new InvalidConfigurationException(worldName + " is no world configuration");
			}
			worlds.put(worldName, loadWorld(worldSection));
		}
		return worlds;
	}

	private ToIntFunction<Location> loadWorld (final ConfigurationSection worldSection)
			throws InvalidConfigurationException {
		final double x, y, z;
		try {
			x  = ConfigurationUtil.loadDouble(worldSection, "x");
			y  = ConfigurationUtil.loadDouble(worldSection, "y");
			z  = ConfigurationUtil.loadDouble(worldSection, "z");
		} catch (NoSuchElementException exception) {
			throw new InvalidConfigurationException("center of world " + worldSection.getName() + " is not properly defined", exception);
		}
		final ToIntBiFunction<Double, Double> horizontalLevel
				= loadHorizontalLevelMethod(worldSection, x, z);
		final DoubleToIntFunction verticalLevel = loadVerticalLevelMethod(worldSection, y);
		return loc -> 1 + horizontalLevel.applyAsInt(loc.getX(), loc.getZ()) + verticalLevel.applyAsInt(loc.getY());
	}

	private ToIntBiFunction<Double, Double> loadHorizontalLevelMethod (final ConfigurationSection worldSection, final double centerX, final double centerZ)
			throws InvalidConfigurationException {
		if (worldSection.contains("horizontal", true)) {
			if (!worldSection.isDouble("horizontal")) {
				throw new InvalidConfigurationException("horizontal level increase of world " + worldSection.getName()
						+ " is not valid");
			}
			final double horizontal = worldSection.getDouble("horizontal");
			if (horizontal < +0.0) {
				throw new InvalidConfigurationException("horizontal level-increase per meter of world " + worldSection.getName()
						+ " may not be negative");
			}
			if (horizontal > +0.0) {
				try {
					final String rawMethod = worldSection.getString("distance-method", "MINECRAFT");
					final DistanceMethod distanceMethod = DistanceMethod.valueOf(rawMethod);
					return (x, z) -> floorInteger(distanceMethod.distanceOf(centerX, centerZ, x, z) * horizontal);
				} catch (IllegalArgumentException exception) {
					throw new InvalidConfigurationException("distance-method of world " + worldSection.getName() + " is not valid",
															exception);
				}
			}
		}
		return (x, z) -> 0;
	}

	private DoubleToIntFunction loadVerticalLevelMethod (final ConfigurationSection worldSection, double centerY)
			throws InvalidConfigurationException {
		if (worldSection.contains("vertical", true)) {
			if (!worldSection.isDouble("vertical")) {
				throw new InvalidConfigurationException("vertical level increase of world " + worldSection.getName()
						+ " is not valid");
			}
			final double vertical = worldSection.getDouble("vertical");
			if (vertical < +0.0) {
				throw new InvalidConfigurationException("vertical level-increase per meter of world " + worldSection.getName()
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

	private Map<EntityType, Map<String, Variant>> loadEntities () throws InvalidConfigurationException {
		final ConfigurationSection entitiesSection = this.config.getConfigurationSection("entities");
		if (entitiesSection == null || entitiesSection.getKeys(false).isEmpty()) {
			return Collections.emptyMap();
		}
		final Set<String> entityTypes = entitiesSection.getKeys(false);
		final Map<EntityType, Map<String, Variant>> entities = new HashMap<>();
		for (final String entityTypeName : entityTypes) {
			final EntityType type = loadEntityType(entityTypeName);
			if (!type.isAlive()) {
				throw new InvalidConfigurationException(entityTypeName + " is not a LivingEntity type");
			}
			final ConfigurationSection entitySection = entitiesSection.getConfigurationSection(entityTypeName);
			if (entitySection == null) {
				throw new InvalidConfigurationException(entityTypeName + " is no entity configuration");
			}
			entities.put(type, loadEntity(entitySection));
		}
		return entities;
	}

	private EntityType loadEntityType (final String entityTypeName) throws InvalidConfigurationException {
		try {
			return EntityType.valueOf(entityTypeName);
		} catch (IllegalArgumentException exception) {
			throw new InvalidConfigurationException("entity-type " + entityTypeName + " is not valid", exception);
		}
	}

	private Map<String, Variant> loadEntity (final ConfigurationSection entitySection) throws InvalidConfigurationException {
		Set<String> entityVariants = entitySection.getKeys(false);
		if (entityVariants.isEmpty()) {
			throw new InvalidConfigurationException(entitySection.getName() + " has no variants defined");
		}
		Map<String, Variant> variants = new HashMap<>();
		for (String variant : entityVariants) {
			ConfigurationSection variantSection = entitySection.getConfigurationSection(variant);
			if (variantSection == null) {
				throw new InvalidConfigurationException(variant + " is no " + entitySection.getName() + " variant configuration");
			}
			variants.put(variant, loadVariant(variantSection, entitySection.getName()));
		}
		return variants;
	}

	private Variant loadVariant (final ConfigurationSection variantSection, final String entityType) throws InvalidConfigurationException {
		final ObjIntConsumer<LivingEntity> name = getName(variantSection, entityType);
		final ObjIntConsumer<LivingEntity> attributes = getAttributes(variantSection, entityType);
		final ObjIntConsumer<LivingEntity> loot = getLoot(variantSection, entityType);
		final ObjIntConsumer<LivingEntity> equippment = getEquippment(variantSection, entityType);
		final ObjIntConsumer<LivingEntity> entityModifier = (entity, level) -> {
			name.accept(entity, level);
			attributes.accept(entity, level);
			loot.accept(entity, level);
			equippment.accept(entity, level);
		};
		// TODO default and probability
		return new Variant(entityModifier);
	}

	private ObjIntConsumer<LivingEntity> getName (final ConfigurationSection variantSection, final String entityType) throws InvalidConfigurationException {
		if (variantSection.contains("name", true)) {
			ConfigurationSection nameSection = variantSection.getConfigurationSection("name");
			if (nameSection == null) {
				throw new InvalidConfigurationException("name configuration of variant" + variantSection.getName() + " of entity " + entityType + " is invalid");
			}
			return loadName(nameSection);
		} else {
			return (entity, level) -> {
				entity.setCustomName(entity.getName() + " Level " + level);
				entity.setCustomNameVisible(true);
			};
		}
	}

	private ObjIntConsumer<LivingEntity> loadName (final ConfigurationSection nameSection) throws InvalidConfigurationException {
		final boolean showName = ConfigurationUtil.loadBoolean(nameSection, "show-name", true);
		final boolean showLevel = ConfigurationUtil.loadBoolean(nameSection, "show-level", true);
		final String customName = ConfigurationUtil.loadString(nameSection, "custom-name", null);
		final String prefix = ConfigurationUtil.loadString(nameSection, "prefix", "");
		if (showLevel || customName != null || !prefix.isEmpty()) {
			return (entity, level) -> {
				entity.setCustomNameVisible(showName);
				final String name = prefix
						+ (customName != null ? customName : entity.getName())
						+ (showLevel ? " Level " + level : "");
				entity.setCustomName(name);
			};
		} else {
			return (entity, level) -> entity.setCustomNameVisible(showName);
		}
	}

	private ObjIntConsumer<LivingEntity> getAttributes (final ConfigurationSection variantSection, final String entityType) throws InvalidConfigurationException {
		final ObjIntConsumer<LivingEntity> attributes;
		if (variantSection.contains("attributes", true)) {
			ConfigurationSection attributesSection = variantSection.getConfigurationSection("attributes");
			if (attributesSection == null) {
				throw new InvalidConfigurationException("attributes configuration of variant" + variantSection.getName() + " of entity " + entityType + " is invalid");
			}
			attributes = loadAttributes(attributesSection);
		} else {
			attributes = (entity, level) -> {
			};
		}
		return attributes;
	}

	private ObjIntConsumer<LivingEntity> loadAttributes (final ConfigurationSection attributesSection) throws InvalidConfigurationException {
		final Set<String> attributes = attributesSection.getKeys(false);
		final List<ObjIntConsumer<LivingEntity>> modifierAppliers = new ArrayList<>();
		for (final String attributeName : attributes) {
			final Attribute attribute = parseAttribute(attributeName);
			final ConfigurationSection attributeSection = attributesSection.getConfigurationSection(attributeName);
			if (attributeSection == null) {
				throw new InvalidConfigurationException(attributeName + " is no attribute configuration");
			}
			final OptionalDouble base = ConfigurationUtil.loadOptionalDouble(attributeSection, "base");
			IntFunction<AttributeModifier> modifier = loadAttribute(attributeSection);
			modifierAppliers.add(createModifierApplier(attribute, base, modifier));
		}
		return (entity, level) -> modifierAppliers.forEach(applier -> applier.accept(entity, level));
	}

	private Attribute parseAttribute (final String attributeName) throws InvalidConfigurationException {
		try {
			return Attribute.valueOf(attributeName);
		} catch (IllegalArgumentException exception) {
			throw new InvalidConfigurationException("attribute " + attributeName + " is not valid", exception);
		}
	}
	
	private IntFunction<AttributeModifier> loadAttribute (final ConfigurationSection attributeSection) throws InvalidConfigurationException {
		final double perLevel;
		try {
			perLevel = ConfigurationUtil.loadDouble(attributeSection, "per-level");
		} catch (NoSuchElementException exception) {
			return null;
		}
		final AttributeModifier.Operation attributeOperation
				= ConfigurationUtil.loadEnumValue(attributeSection, "operation", AttributeModifier.Operation.class, AttributeModifier.Operation.ADD_NUMBER);
		final DoubleUnaryOperator modifierBorders = loadModifierBorders(attributeSection);
		return level -> new AttributeModifier("dynamicModifier", modifierBorders.applyAsDouble(level * perLevel), attributeOperation);
	}

	private DoubleUnaryOperator loadModifierBorders (final ConfigurationSection attributeSection) throws InvalidConfigurationException {
		final OptionalDouble min = ConfigurationUtil.loadOptionalDouble(attributeSection, "min");
		final OptionalDouble max = ConfigurationUtil.loadOptionalDouble(attributeSection, "max");
		if (min.isPresent()) {
			if (max.isPresent()) {
				if (min.getAsDouble() > max.getAsDouble()) {
					throw new InvalidConfigurationException("minimum is larger than maximum");
				}
				return modifier -> Math.min(Math.max(modifier, min.getAsDouble()), max.getAsDouble());
			} else {
				return modfier -> Math.max(modfier, min.getAsDouble());
			}
		} else {
			if (max.isPresent()) {
				return modfier -> Math.min(modfier, max.getAsDouble());
			} else {
				return DoubleUnaryOperator.identity();
			}
		}
	}

	private ObjIntConsumer<LivingEntity> createModifierApplier (Attribute attribute, OptionalDouble base, IntFunction<AttributeModifier> modifier) {
		if (modifier == null && !base.isPresent()) {
			return (entity, level) -> {
			};
		}
		return (entity, level) -> {
			final AttributeInstance attributeInstance = entity.getAttribute(attribute);
			if (attributeInstance != null) {
				if (modifier != null) {
					attributeInstance.addModifier(modifier.apply(level));
				}
				if (base.isPresent()) {
					attributeInstance.setBaseValue(base.getAsDouble());
					if (attribute == Attribute.GENERIC_MAX_HEALTH) {
						entity.setHealth(attributeInstance.getValue());
					}
				}
			}
		};
	}
	
	private ObjIntConsumer<LivingEntity> getLoot (final ConfigurationSection variantSection, final String entityType) throws InvalidConfigurationException {
		final ObjIntConsumer<LivingEntity> loot;
		if (variantSection.contains("loot", true)) {
			ConfigurationSection lootSection = variantSection.getConfigurationSection("loot");
			if (lootSection == null) {
				throw new InvalidConfigurationException("loot configuration of variant" + variantSection.getName() + " of entity " + entityType + " is invalid");
			}
			loot = (entity, level) -> {
			};//loadLoot(lootSection);
		} else {
			loot = (entity, level) -> {
			};
		}
		return loot;
	}

	private ObjIntConsumer<LivingEntity> loadLoot (final ConfigurationSection lootSection) throws InvalidConfigurationException {
		boolean override = ConfigurationUtil.loadBoolean(lootSection, "override", false);
		Optional<ConfigurationSection> optionalItemsSection = ConfigurationUtil.loadOptionalConfigurationSection(lootSection, "items");
		if (optionalItemsSection.isPresent()) {
			ConfigurationSection itemsSection = optionalItemsSection.get();
			return (entity, level) -> {
			};
		} else {
			if (override) {
				return (entity, level) -> {
					entity.setMetadata("custom-drops", new FixedMetadataValue(plugin, null));
				};
			} else {
				return (entity, level) -> {
				};
			}
		}
	}

	private ObjIntConsumer<LivingEntity> getEquippment (final ConfigurationSection variantSection, final String entityType) throws InvalidConfigurationException {
		final ObjIntConsumer<LivingEntity> equippment;
		if (variantSection.contains("equippment", true)) {
			ConfigurationSection equippmentSection = variantSection.getConfigurationSection("equippment");
			if (equippmentSection == null) {
				throw new InvalidConfigurationException("equippment configuration of variant" + variantSection.getName() + " of entity " + entityType + " is invalid");
			}
			equippment = loadEquippment(equippmentSection);
		} else {
			equippment = (entity, level) -> {
			};
		}
		return equippment;
	}

	private ObjIntConsumer<LivingEntity> loadEquippment (final ConfigurationSection equippmentSection) throws InvalidConfigurationException {
		return (entity, level) -> {
			};
	}

	/**
	 *
	 * @param value
	 * @return
	 */
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
