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

import java.util.function.BiFunction;

/**
 * This enum contains different methods for calculating a distance. These methods represent different kinds of distance.
 */
public enum DistanceMethod {

	/**
	 * The best known and mathematicaly exact distance. All points of the same distance create a circle.
	 */
	PYTHAGORAS((x, z) -> Math.sqrt(x * x + z * z)),

	/**
	 * Minecrafts distance method. All points of the same distance create a square.
	 */
	MINECRAFT((x, z) -> Math.max(Math.abs(x), Math.abs(z)));

	private final BiFunction<Double, Double, Double> distanceCalculation;

	DistanceMethod (BiFunction<Double, Double, Double> distanceCalculation) {
		this.distanceCalculation = distanceCalculation;
	}

	/**
	 * Calculate the distance by using x and z distance between these points.
	 *
	 * @param deltaX x-distance between two points
	 * @param deltaZ z-distance between two points
	 * @return the distance according to the used distance method
	 */
	public double distanceOf (double deltaX, double deltaZ) {
		return this.distanceCalculation.apply(deltaX, deltaZ);
	}

	/**
	 * Calculate the distance between two points
	 *
	 * @param fromX x coordinate of first point
	 * @param fromZ z coordinate of first point
	 * @param toX x coordinate of second point
	 * @param toZ z coordinate of second point
	 * @return the distance according to the used distance method
	 */
	public double distanceOf (double fromX, double fromZ, double toX, double toZ) {
		return distanceOf(toX - fromX, toZ - fromZ);
	}
}
