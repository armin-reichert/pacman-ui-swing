/*
MIT License

Copyright (c) 2021-22 Armin Reichert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package de.amr.games.pacman.ui.swing.app;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.games.pacman.model.common.GameVariant;

/**
 * @author Armin Reichert
 */
class Options {

	private static final Logger logger = LogManager.getFormatterLogger();

	//@formatter:off
	private static final String OPT_HEIGHT           = "-height";
	private static final String OPT_VARIANT_MSPACMAN = "-mspacman";
	private static final String OPT_VARIANT_PACMAN   = "-pacman";
	//@formatter:on

	private static final List<String> ALL_OPTIONS = List.of(OPT_HEIGHT, OPT_VARIANT_MSPACMAN, OPT_VARIANT_PACMAN);

	public double height = 576;
	public GameVariant gameVariant = GameVariant.PACMAN;

	private int i;

	public Options(List<String> args) {
		i = 0;
		while (i < args.size()) {
			option1(args, OPT_HEIGHT, Double::valueOf).ifPresent(value -> height = value);
			option0(args, OPT_VARIANT_MSPACMAN, Options::convertGameVariant).ifPresent(value -> gameVariant = value);
			option0(args, OPT_VARIANT_PACMAN, Options::convertGameVariant).ifPresent(value -> gameVariant = value);
			++i;
		}
	}

	private <T> Optional<T> option1(List<String> args, String name, Function<String, T> fnConvert) {
		if (name.equals(args.get(i))) {
			if (i + 1 == args.size() || ALL_OPTIONS.contains(args.get(i + 1))) {
				logger.error("!!! Error: missing value for parameter '%s'.", name);
			} else {
				++i;
				try {
					T value = fnConvert.apply(args.get(i));
					logger.info("Found parameter %s = %s", name, value);
					return Optional.ofNullable(value);
				} catch (Exception x) {
					logger.error("!!! Error: '%s' is no legal value for parameter '%s'.", args.get(i), name);
				}
			}
		}
		return Optional.empty();
	}

	private <T> Optional<T> option0(List<String> args, String name, Function<String, T> fnConvert) {
		if (name.equals(args.get(i))) {
			logger.info("Found parameter %s", name);
			try {
				T value = fnConvert.apply(name);
				return Optional.ofNullable(value);
			} catch (Exception x) {
				logger.error("!!! Error: '%s' is no legal parameter.", name);
			}
		}
		return Optional.empty();
	}

	private static GameVariant convertGameVariant(String s) {
		return switch (s) {
		case OPT_VARIANT_MSPACMAN -> GameVariant.MS_PACMAN;
		case OPT_VARIANT_PACMAN -> GameVariant.PACMAN;
		default -> null;
		};
	}
}