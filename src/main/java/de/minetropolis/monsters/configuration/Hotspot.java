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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.util.Vector;

/**
 *
 */
public class Hotspot {

	private final String name;
	private final Vector center;
	private int baseLevel;
	private LevelBorder border;
	private final Set<LevelChangeStrategy> changeStrategies = new HashSet<>();

	public Hotspot (String name, Vector center) {
		this(name, center, 0, new LevelBorder());
	}

	public Hotspot (String name, Vector center, LevelChangeStrategy... strategies) {
		this(name, center, 0, new LevelBorder(), strategies);
	}

	public Hotspot (String name, Vector center, int baseLevel, LevelBorder border, LevelChangeStrategy... strategies) {
		this(name, center, baseLevel, border);
		this.changeStrategies.addAll(Arrays.asList(strategies));
	}

	public Hotspot (String name, Vector center, int baseLevel, LevelBorder border) {
		this.name = name;
		this.center = center.clone();
		this.baseLevel = baseLevel;
		this.border = border;
	}

	public String getName() {
		return this.name;
	}
	
	public Vector getCenter() {
		return this.center.clone();
	}
	
	public int getBaseLevel() {
		return this.baseLevel;
	}

	public void setBaseLevel(int baseLevel) {
		this.baseLevel = baseLevel;
	}
	
	public LevelBorder getLevelBorder() {
		return this.border;
	}
	
	public void setLevelBorder(LevelBorder border) {
		this.border = border;
	}
	
	public boolean addLevelChangeStrategy(LevelChangeStrategy changeStrategy) {
		return this.changeStrategies.add(changeStrategy);
	}
	
	public int calculateChange(Vector targetPosition) {
		return 0; // TODO missing LevelChangeStrategy methods
	}
}
