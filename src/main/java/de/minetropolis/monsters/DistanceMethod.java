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

import java.util.function.*;

/**
 *
 */
public enum DistanceMethod {
	PYTHAGORAS((x, z) -> Math.sqrt(x*x + z*z)),
	MINECRAFT((x, z) -> Math.max(Math.abs(x), Math.abs(z))),
	;

	private final BiFunction<Double, Double, Double> distanceCalculation;
	
	private DistanceMethod (BiFunction<Double, Double, Double> distanceCalculation) {
		this.distanceCalculation = distanceCalculation;
	}
	
	public double distanceOf(double deltaX, double deltaZ) {
		return distanceCalculation.apply(deltaX, deltaZ);
	}

	public double distanceOf(double fromX, double fromZ, double toX, double toZ) {
		return distanceOf(toX-fromX, toZ-fromZ);
	}
}
