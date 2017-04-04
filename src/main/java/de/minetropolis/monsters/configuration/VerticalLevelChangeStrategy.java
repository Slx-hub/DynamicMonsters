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

import org.bukkit.util.Vector;


public final class VerticalLevelChangeStrategy extends AbstractLevelChangeStrategy {

    public VerticalLevelChangeStrategy(int levelPerChange, double distancePerChange) {
        this(levelPerChange, distancePerChange, 0.0d, new LevelBorder());
    }
    
    public VerticalLevelChangeStrategy(int levelPerChange, double distancePerChange, double offset) {
        this(levelPerChange, distancePerChange, offset, new LevelBorder());
    }
    
    public VerticalLevelChangeStrategy(int levelPerChange, double distancePerChange, LevelBorder border) {
        this(levelPerChange, distancePerChange, 0.0d, border);
    }
    
    public VerticalLevelChangeStrategy(int levelPerChange, double distancePerChange, double offset, LevelBorder border) {
        super(levelPerChange, distancePerChange, offset, border);
    }

    @Override
    protected double calculateDistance(Vector center, Vector target) {
        return center.getY() - target.getY();
    }
}
