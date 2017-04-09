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
package de.minetropolis.monsters.math;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;

/**
 *
 */
public final class AdditionalMathOperations {

	public static final Function MIN = new Function("min", 2) {
		@Override
		public double apply (double... args) {
			return Math.min(args[0], args[1]);
		}
	};

	public static final Function MAX = new Function("max", 2) {
		@Override
		public double apply (double... args) {
			return Math.max(args[0], args[1]);
		}
	};

	public static final Function RANDOM_INT = new Function("randomInt", 1) {
		private final Random random = new Random();

		@Override
		public double apply (double... args) {
			return random.nextInt(Math.toIntExact(Math.round(args[0])));
		}
	};

	public static final Function RANDOM = new Function("random", 0) {
		private final Random random = new Random();

		@Override
		public double apply (double... args) {
			return random.nextDouble();
		}
	};

	public static final Operator GREATER_THAN = new Operator(">", 2, true, 250) {
		@Override
		public double apply (double... values) {
			return values[0] > values[1] ? 1 : 0;
		}
	};

	public static final Operator GREATER_THAN_OR_EQUAL = new Operator(">=", 2, true, 250) {
		@Override
		public double apply (double... values) {
			return values[0] >= values[1] ? 1 : 0;
		}
	};

	public static final Operator SMALLER_THAN = new Operator("<", 2, true, 250) {
		@Override
		public double apply (double... values) {
			return values[0] < values[1] ? 1 : 0;
		}
	};

	public static final Operator SMALLER_THAN_OR_EQUAL = new Operator("<=", 2, true, 250) {
		@Override
		public double apply (double... values) {
			return values[0] <= values[1] ? 1 : 0;
		}
	};

	public static final Operator EQUALS = new Operator("==", 2, true, 250) {
		@Override
		public double apply (double... values) {
			return Math.abs(values[0] - values[1]) <= 1e-9 ? 1 : 0;
		}
	};

	public static final Operator EQUALS_NOT = new Operator("!=", 2, true, 250) {
		@Override
		public double apply (double... values) {
			return Math.abs(values[0] - values[1]) > 1e-9 ? 1 : 0;
		}
	};

	public static final Operator AND = new Operator("&", 2, true, 120) {
		@Override
		public double apply (double... values) {
			return values[0] >= 1 && values[1] >= 1 ? 1 : 0;
		}
	};

	public static final Operator OR = new Operator("|", 2, true, 125) {
		@Override
		public double apply (double... values) {
			return values[0] >= 1 || values[1] >= 1 ? 1 : 0;
		}
	};

	public static final Operator NOT = new Operator("~", 1, true, 200) {
		@Override
		public double apply (double... values) {
			return values[0] < 1 ? 1 : 0;
		}
	};

	private static final List<Function> FUNCTIONS = Collections.unmodifiableList(
			Arrays.asList(MIN, MAX, RANDOM_INT, RANDOM));
	private static final List<Operator> OPERATORS = Collections.unmodifiableList(
			Arrays.asList(GREATER_THAN, GREATER_THAN_OR_EQUAL, SMALLER_THAN, SMALLER_THAN_OR_EQUAL, EQUALS, EQUALS_NOT,
						  AND, OR, NOT));

	private AdditionalMathOperations () {
		throw new UnsupportedOperationException("utility class");
	}

	public static List<Function> getAdditionalFunctions () {
		return FUNCTIONS;
	}

	public static List<Operator> getAdditionalOperator () {
		return OPERATORS;
	}

}
