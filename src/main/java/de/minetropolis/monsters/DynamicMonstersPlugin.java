/*
 * Copyright (C) 2017 seyfahni
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

import de.minetropolis.monsters.configuration.ConfigurationParser;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 */
public final class DynamicMonstersPlugin extends JavaPlugin {

	/**
	 *
	 */
	public DynamicMonstersPlugin () {
	}

	@Override
	public void onEnable () {
		this.saveDefaultConfig();
		final Set<String> configKeys = this.getConfig().getKeys(true);
		this.getLogger().config("Dumping config keys:");
		configKeys.forEach(key -> this.getLogger().config(key));

		final ConfigurationParser parser = new ConfigurationParser(this);
		parser.parseCurrentConfig();

		if (!parser.isParsed()) {
			getLogger().log(Level.SEVERE, "Disabling plugin.");
			setEnabled(false);
			return;
		}

		final MonsterSpawnEventListener listener = new MonsterSpawnEventListener();
		listener.setWorldsConfiguration(parser.getWorldsConfiguration());
		listener.setEntitiesConfiguration(parser.getEntitiesConfiguration());

		getServer().getPluginManager().registerEvents(listener, this);
		getServer().getPluginManager().registerEvents(new MonsterDeathEventListener(), this);
		getCommand("dynamicmonster").setExecutor(this::dynamicMonsterCommand);
	}

	/**
	 *
	 * @param sender
	 * @param command
	 * @param label
	 * @param args
	 * @return
	 */
	public boolean dynamicMonsterCommand (final CommandSender sender, final Command command,
										  final String label, final String[] args) {
		return true;
	}

}
