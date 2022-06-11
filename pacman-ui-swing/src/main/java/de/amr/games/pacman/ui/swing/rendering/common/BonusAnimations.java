/*
MIT License

Copyright (c) 2022 Armin Reichert

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

package de.amr.games.pacman.ui.swing.rendering.common;

import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import de.amr.games.pacman.lib.animation.ThingAnimation;
import de.amr.games.pacman.lib.animation.ThingAnimationCollection;
import de.amr.games.pacman.lib.animation.ThingList;
import de.amr.games.pacman.model.common.actors.Bonus;
import de.amr.games.pacman.model.common.actors.BonusAnimationKey;

/**
 * @author Armin Reichert
 */
public class BonusAnimations extends ThingAnimationCollection<Bonus, BonusAnimationKey, BufferedImage> {

	public final ThingList<BufferedImage> symbolAnimation;
	public final ThingList<BufferedImage> valueAnimation;

	public BonusAnimations(Rendering2D r2D) {
		symbolAnimation = r2D.createBonusSymbolList();
		valueAnimation = r2D.createBonusValueList();
		select(BonusAnimationKey.ANIM_NONE);
	}

	@Override
	public ThingAnimation<BufferedImage> byKey(BonusAnimationKey key) {
		return switch (key) {
		case ANIM_NONE -> null;
		case ANIM_SYMBOL -> symbolAnimation;
		case ANIM_VALUE -> valueAnimation;
		};
	}

	@Override
	public Stream<ThingAnimation<BufferedImage>> all() {
		return Stream.of(symbolAnimation, valueAnimation);
	}

	@Override
	public BufferedImage current(Bonus bonus) {
		return switch (selectedKey) {
		case ANIM_NONE -> null;
		case ANIM_SYMBOL -> symbolAnimation.frame(bonus.symbol());
		case ANIM_VALUE -> valueAnimation.frame(bonus.symbol());
		};
	}
}