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
 *
 */
public final class ConfigurationUtil {

	private ConfigurationUtil () {
	}
	
	/**
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static Color loadColor (ConfigurationSection section, String path, Color def)
			throws InvalidConfigurationException {
		try {
			return loadColor(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static Optional<Color> loadOptionalColor (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadColor(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static ConfigurationSection loadConfigurationSection (ConfigurationSection section, String path, ConfigurationSection def)
			throws InvalidConfigurationException {
		try {
			return loadConfigurationSection(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static Optional<ConfigurationSection> loadOptionalConfigurationSection (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadConfigurationSection(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	public static Map<String, ConfigurationSection> loadConfigurationSectionGroup (ConfigurationSection section) throws InvalidConfigurationException{
		Map<String, ConfigurationSection> subSections = new HashMap<>();
		for (String subSection : section.getKeys(false)) {
			subSections.put(subSection, loadConfigurationSection(section, subSection));
		}
		return subSections;
	}
	
	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static double loadDouble (ConfigurationSection section, String path, double def)
			throws InvalidConfigurationException {
		try {
			return loadDouble(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static OptionalDouble loadOptionalDouble (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return OptionalDouble.of(loadDouble(section, path));
		} catch (MissingEntryException exception) {
			return OptionalDouble.empty();
		}
	}

	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static int loadInteger (ConfigurationSection section, String path, int def)
			throws InvalidConfigurationException {
		try {
			return loadInteger(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static OptionalInt loadOptionalInteger (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return OptionalInt.of(loadInteger(section, path));
		} catch (MissingEntryException exception) {
			return OptionalInt.empty();
		}
	}

	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static ItemStack loadItemStack (ConfigurationSection section, String path, ItemStack def)
			throws InvalidConfigurationException {
		try {
			return loadItemStack(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static Optional<ItemStack> loadOptionalItemStack (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadItemStack(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static long loadLong (ConfigurationSection section, String path, long def)
			throws InvalidConfigurationException {
		try {
			return loadLong(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static OptionalLong loadOptionalLong (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return OptionalLong.of(loadLong(section, path));
		} catch (MissingEntryException exception) {
			return OptionalLong.empty();
		}
	}

	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static OfflinePlayer loadPlayer (ConfigurationSection section, String path, OfflinePlayer def)
			throws InvalidConfigurationException {
		try {
			return loadPlayer(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static Optional<OfflinePlayer> loadOptionalOfflinePlayer (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadPlayer(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static String loadString (ConfigurationSection section, String path, String def)
			throws InvalidConfigurationException {
		try {
			return loadString(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static Optional<String> loadOptionalString (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadString(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param section
	 * @param path
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static Vector loadVector (ConfigurationSection section, String path, Vector def)
			throws InvalidConfigurationException {
		try {
			return loadVector(section, path);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static Optional<Vector> loadOptionalVector (ConfigurationSection section, String path)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadVector(section, path));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 *
	 * @param section
	 * @param path
	 * @return
	 * @throws InvalidConfigurationException
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
	 *
	 * @param <T>
	 * @param section
	 * @param path
	 * @param enumClass
	 * @param def
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static <T extends Enum<T>> T loadEnumValue (ConfigurationSection section, String path, Class<T> enumClass, T def)
			throws InvalidConfigurationException {
		try {
			return loadEnumValue(section, path, enumClass);
		} catch (MissingEntryException exception) {
			return def;
		}
	}

	public static <T extends Enum<T>> Optional<T> loadOptionalEnumValue (ConfigurationSection section, String path, Class<T> enumClass)
			throws InvalidConfigurationException {
		try {
			return Optional.ofNullable(loadEnumValue(section, path, enumClass));
		} catch (MissingEntryException exception) {
			return Optional.empty();
		}
	}

	/**
	 *
	 * @param <T>
	 * @param section
	 * @param path
	 * @param enumClass
	 * @return
	 * @throws InvalidConfigurationException
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
	
	private static void checkArguments(ConfigurationSection section, String path) throws IllegalArgumentException {
		if (section == null) {
			throw new IllegalArgumentException("no configuration section");
		}
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("invalid path given");
		}
	}
}
