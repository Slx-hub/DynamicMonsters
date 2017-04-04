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

import java.util.Objects;

import org.bukkit.util.Vector;

/**
 * strategy to calculate level.
 */
public abstract class AbstractLevelChangeStrategy implements LevelChangeStrategy {

    /**
     * amount of levels added per step.
     */
    private final int levelPerChange;
    /**
     * distance between steps.
     */
    private final double distancePerChange;
    /**
     * offsets distance/ and thus, level calculation.
     */
    private final double offset;
    /**
     * min/max level for mobs.
     */
    private final LevelBorder border;

    /**
     * new strategy.
     *
     * @param levelPerChange    amount of levels added per step
     * @param distancePerChange distance between steps
     * @param offset            offsets distance/ and thus, level calculation
     * @param border            min/max level for mobs
     */
    public AbstractLevelChangeStrategy(int levelPerChange, double distancePerChange, double offset, LevelBorder border) {
        this.levelPerChange = levelPerChange;
        this.distancePerChange = distancePerChange;
        this.offset = offset;
        this.border = Objects.requireNonNull(border);
    }

    @Override
    public final int calculateLevelDelta(Vector center, Vector target) throws ArithmeticException {
        double distance = Math.abs(calculateDistance(center, target));
        double changingDistance = Math.max(0, distance - this.offset);
        int changes = Math.toIntExact(Math.round(Math.floor(changingDistance / this.distancePerChange)));
        return this.border.fit(Math.multiplyExact(changes, this.levelPerChange));
    }

    /**
     * calculates distance.
     *
     * @param center position of center
     * @param target position of target
     * @return distance
     */
    protected abstract double calculateDistance(Vector center, Vector target);
}
