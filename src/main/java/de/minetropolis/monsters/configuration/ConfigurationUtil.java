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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Utils for configuration parsing.
 */
public final class ConfigurationUtil {

	private ConfigurationUtil () {
	}

	/**
	 * Loads a Boolean returns default if there was no Boolean at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Boolean
	 * @param def     default value
	 * @return Boolean when found, default else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static boolean loadBoolean (ConfigurationSection section, String path, boolean def)
			throws InvalidConfigurationException {
		try {
			return loadBoolean(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a Boolean or throws an exception if there was no Boolean at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Boolean
	 * @return Boolean when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static boolean loadBoolean (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isBoolean(path)) {
				throw new IllegalEntryTypeException(path + " is not a boolean");
			}
			return section.getBoolean(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}

	}

	/**
	 * Loads a Color returns default if there was no Color at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Color
	 * @param def     default value
	 * @return Color when found or default
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Color loadColor (ConfigurationSection section, String path, Color def)
			throws InvalidConfigurationException {
		try {
			return loadColor(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a Color or returns an empty Optional if there was no Color at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Color
	 * @return Color when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Optional<Color> loadOptionalColor (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadColor(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 * Loads a Color or throws an exception if there was no Color at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Color
	 * @return Color when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Color loadColor (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isColor(path)) {
				throw new IllegalEntryTypeException(path + " is not a color");
			}
			return section.getColor(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}

	}

	/**
	 * Loads a ConfigurationSelection or returns a default section if there was no section at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested ConfigurationSelection
	 * @return ConfigurationSelection when found, default else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static ConfigurationSection loadConfigurationSection (ConfigurationSection section, String path, ConfigurationSection def)
			throws InvalidConfigurationException {
		try {
			return loadConfigurationSection(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a ConfigurationSelection or returns an empty Optional if there was no section at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested ConfigurationSelection
	 * @return ConfigurationSelection when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Optional<ConfigurationSection> loadOptionalConfigurationSection (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadConfigurationSection(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 * Loads all subsections of a ConfigurationSection.
	 *
	 * @param section section to load from
	 * @return map of subsections
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Map<String, ConfigurationSection> loadConfigurationSectionGroup (ConfigurationSection section) throws InvalidConfigurationException {
		Map<String, ConfigurationSection> subSections = new HashMap<>();
		for (String subSection : section.getKeys(false)) {
			subSections.put(subSection, loadConfigurationSection(section, subSection));
		}
		return subSections;
	}

	/**
	 * Loads a ConfigurationSelection or throws an exception if there was no section at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested ConfigurationSelection
	 * @return ConfigurationSelection when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static ConfigurationSection loadConfigurationSection (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isConfigurationSection(path)) {
				throw new IllegalEntryTypeException(path + " is not a configuration section");
			}
			return section.getConfigurationSection(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}
	}

	/**
	 * Loads a Double returns default if there was no Double at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Double
	 * @param def     default value
	 * @return Double when found, default else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static double loadDouble (ConfigurationSection section, String path, double def)
			throws InvalidConfigurationException {
		try {
			return loadDouble(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a Double or returns an empty Optional if there was no Double at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Double
	 * @return Double when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static OptionalDouble loadOptionalDouble (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return OptionalDouble.of(loadDouble(section, path));
		} catch (MissingEntryException exception) {
			return OptionalDouble.empty();
		}
	}

	/**
	 * Loads a Double or throws an exception if there was no Double at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Double
	 * @return Double when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static double loadDouble (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isDouble(path)) {
				throw new IllegalEntryTypeException(path + " is not a double");
			}
			return section.getDouble(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}
	}

	/**
	 * Loads a Integer returns default if there was no Integer at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Integer
	 * @param def     default value
	 * @return Integer when found, default else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static int loadInteger (ConfigurationSection section, String path, int def)
			throws InvalidConfigurationException {
		try {
			return loadInteger(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a Integer or returns an empty Optional if there was no Integer at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Integer
	 * @return Integer when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static OptionalInt loadOptionalInteger (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return OptionalInt.of(loadInteger(section, path));
		} catch (MissingEntryException exception) {
			return OptionalInt.empty();
		}
	}

	/**
	 * Loads a Integer or throws an exception if there was no Integer at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Integer
	 * @return Integer when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static int loadInteger (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isInt(path)) {
				throw new IllegalEntryTypeException(path + " is not an integer");
			}
			return section.getInt(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}
	}

	/**
	 * Loads a ItemStack returns default if there was no ItemStack at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested ItemStack
	 * @param def     default value
	 * @return ItemStack when found or default
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static ItemStack loadItemStack (ConfigurationSection section, String path, ItemStack def)
			throws InvalidConfigurationException {
		try {
			return loadItemStack(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a ItemStack or returns an empty Optional if there was no ItemStack at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested ItemStack
	 * @return ItemStack when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Optional<ItemStack> loadOptionalItemStack (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadItemStack(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 * Loads a ItemStack or throws an exception if there was no ItemStack at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested ItemStack
	 * @return ItemStack when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static ItemStack loadItemStack (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isItemStack(path)) {
				throw new IllegalEntryTypeException(path + " is not a item stack");
			}
			return section.getItemStack(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}

	}

	/**
	 * Loads a List returns default if there was no List at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested List
	 * @param def     default value
	 * @return List when found or default
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static List<?> loadList (ConfigurationSection section, String path, List<?> def)
			throws InvalidConfigurationException {
		try {
			return loadList(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a List or throws an exception if there was no List at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested List
	 * @return List when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static List<?> loadList (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isList(path)) {
				throw new IllegalEntryTypeException(path + " is not a list");
			}
			return section.getList(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}

	}

	/**
	 * Loads a Long returns default if there was no Long at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Long
	 * @param def     default value
	 * @return Long when found or default
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static long loadLong (ConfigurationSection section, String path, long def)
			throws InvalidConfigurationException {
		try {
			return loadLong(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a Long or returns an empty Optional if there was no Long at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Long
	 * @return Long when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static OptionalLong loadOptionalLong (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return OptionalLong.of(loadLong(section, path));
		} catch (MissingEntryException exception) {
			return OptionalLong.empty();
		}
	}

	/**
	 * Loads a Long or throws an exception if there was no Long at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Long
	 * @return Long when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static long loadLong (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isLong(path)) {
				throw new IllegalEntryTypeException(path + " is not a long");
			}
			return section.getLong(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}

	}

	/**
	 * Loads a OfflinePlayer returns default if there was no OfflinePlayer at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested OfflinePlayer
	 * @param def     default value
	 * @return OfflinePlayer when found or default
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static OfflinePlayer loadPlayer (ConfigurationSection section, String path, OfflinePlayer def)
			throws InvalidConfigurationException {
		try {
			return loadPlayer(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a OfflinePlayer or returns an empty Optional if there was no OfflinePlayer at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested OfflinePlayer
	 * @return OfflinePlayer when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Optional<OfflinePlayer> loadOptionalOfflinePlayer (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadPlayer(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 * Loads a OfflinePlayer or throws an exception if there was no OfflinePlayer at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested OfflinePlayer
	 * @return OfflinePlayer when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static OfflinePlayer loadPlayer (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isOfflinePlayer(path)) {
				throw new IllegalEntryTypeException(path + " is not a player");
			}
			return section.getOfflinePlayer(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}

	}

	/**
	 * Loads a String returns default if there was no String at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested String
	 * @param def     default value
	 * @return String when found or default
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static String loadString (ConfigurationSection section, String path, String def)
			throws InvalidConfigurationException {
		try {
			return loadString(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a String or returns an empty Optional if there was no String at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested String
	 * @return String when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Optional<String> loadOptionalString (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadString(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 * Loads a String or throws an exception if there was no String at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested String
	 * @return String when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static String loadString (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isString(path)) {
				throw new IllegalEntryTypeException(path + " is not a string");
			}
			return section.getString(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}
	}

	/**
	 * Loads a Vector returns default if there was no Vector at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Vector
	 * @param def     default value
	 * @return Vector when found or default
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Vector loadVector (ConfigurationSection section, String path, Vector def)
			throws InvalidConfigurationException {
		try {
			return loadVector(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a Vector or returns an empty Optional if there was no Vector at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Vector
	 * @return Vector when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Optional<Vector> loadOptionalVector (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadVector(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 * Loads a Vector or throws an exception if there was no Vector at specified location.
	 *
	 * @param section section to load from
	 * @param path    path of requested Vector
	 * @return Vector when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static Vector loadVector (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (section.contains(path, true)) {
			if (!section.isVector(path)) {
				throw new IllegalEntryTypeException(path + " is not a vector");
			}
			return section.getVector(path);
		} else {
			throw new MissingEntryException(path + " is not defined");
		}
	}

	/**
	 * Loads a EnumValue returns default if there was no EnumValue at specified location.
	 *
	 * @param <T>       generic
	 * @param section   section to load from
	 * @param path      path of requested EnumValue
	 * @param enumClass class of expected enum
	 * @param def       default value
	 * @return EnumValue when found or default
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static <T extends Enum<T>> T loadEnumValue (ConfigurationSection section, String path, Class<T> enumClass, T def)
			throws InvalidConfigurationException {
		try {
			return loadEnumValue(section, path, enumClass);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	/**
	 * Loads a EnumValue or returns an empty Optional if there was no EnumValue at specified location.
	 *
	 * @param <T>       generic
	 * @param section   section to load from
	 * @param path      path of requested EnumValue
	 * @param enumClass class of expected enum
	 * @return EnumValue when found, empty Optional else
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static <T extends Enum<T>> Optional<T> loadOptionalEnumValue (ConfigurationSection section, String path, Class<T> enumClass)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadEnumValue(section, path, enumClass));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 * Loads a EnumValue or throws an exception if there was no EnumValue at specified location.
	 *
	 * @param <T>       generic
	 * @param section   section to load from
	 * @param path      path of requested EnumValue
	 * @param enumClass class of expected enum
	 * @return EnumValue when found
	 * @throws InvalidConfigurationException when section is invalid
	 */
	public static <T extends Enum<T>> T loadEnumValue (ConfigurationSection section, String path, Class<T> enumClass)
			throws InvalidConfigurationException {
		checkArguments(section, path);
		if (enumClass == null) {
			throw new IllegalArgumentException("no enum type given");
		}
		try {
			String name = loadString(section, path);
			return Enum.valueOf(enumClass, name);
		} catch (NullPointerException | IllegalArgumentException | InvalidConfigurationException exception) {
			throw new IllegalEntryTypeException(path + " is not an enum value of " + enumClass.getSimpleName());
		}
	}

	private static void checkArguments (ConfigurationSection section, String path) throws IllegalArgumentException {
		if (section == null) {
			throw new IllegalArgumentException("no configuration section");
		}
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("invalid path given");
		}
	}
}
