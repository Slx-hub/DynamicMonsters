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

/**
 * An InvalidConfigurationException will be thrown if a configuration file is illformed.
 */
public class InvalidConfigurationException extends Exception {

	private static final long serialVersionUID = -7513049321923843610L;

	/**
	 * Creates a new instance of <code>InvalidConfigurationException</code> without detail message.
	 */
	public InvalidConfigurationException () {
	}

	/**
	 * Constructs an instance of <code>InvalidConfigurationException</code> with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public InvalidConfigurationException (String message) {
		super(message);
	}

	/**
	 * Constructs an instance of <code>InvalidConfigurationException</code> with the specified cause.
	 *
	 * @param cause the cause
	 */
	public InvalidConfigurationException (Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs an instance of <code>InvalidConfigurationException</code> with the specified detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause   the cause
	 */
	public InvalidConfigurationException (String message, Throwable cause) {
		super(message, cause);
	}
}
