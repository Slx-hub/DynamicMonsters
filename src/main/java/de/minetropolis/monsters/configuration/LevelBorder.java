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

import java.util.OptionalInt;

/**
 *
 */
public class LevelBorder {

	private final int minimum;
	private final int maximum;

	private final boolean hasMinimum;
	private final boolean hasMaximum;
	
	public LevelBorder () {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE, false, false);
	}

	public LevelBorder (OptionalInt minimum, OptionalInt maximum) {
		this(minimum.orElse(Integer.MIN_VALUE), maximum.orElse(Integer.MAX_VALUE), minimum.isPresent(), maximum.isPresent());
	}

	public LevelBorder (int minimum, int maximum) {
		this(minimum, maximum, true, true);
	}
	
	public LevelBorder (int minimum, int maximum, boolean hasMin, boolean hasMax) {
		if (minimum > maximum) {
			throw new IllegalArgumentException("minimum greater than maximum");
		}
		this.minimum = minimum;
		this.maximum = maximum;
		this.hasMinimum = hasMin;
		this.hasMaximum = hasMax;
	}

	int fit(int value) {
		if (value < minimum) {
			return minimum;
		}
		if (value > maximum) {
			return maximum;
		}
		return value;
	}
}
