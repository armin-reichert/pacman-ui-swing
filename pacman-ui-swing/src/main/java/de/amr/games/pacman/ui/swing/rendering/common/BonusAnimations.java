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

import de.amr.games.pacman.lib.animation.GenericAnimation;
import de.amr.games.pacman.lib.animation.GenericAnimationCollection;
import de.amr.games.pacman.lib.animation.SingleGenericAnimation;
import de.amr.games.pacman.model.common.actors.Bonus;
import de.amr.games.pacman.ui.swing.rendering.common.BonusAnimations.Key;

/**
 * @author Armin Reichert
 */
public class BonusAnimations extends GenericAnimationCollection<Bonus, Key, BufferedImage> {

	public enum Key {
		ANIM_SYMBOL, ANIM_VALUE;
	}

	public final SingleGenericAnimation<BufferedImage> symbolAnimation;
	public final SingleGenericAnimation<BufferedImage> valueAnimation;
	public final SingleGenericAnimation<Integer> jumpAnimation;

	public BonusAnimations(Rendering2D r2D) {
		symbolAnimation = r2D.createBonusSymbolAnimation();
		valueAnimation = r2D.createBonusValueAnimation();
		jumpAnimation = new SingleGenericAnimation<>(2, -2);
		jumpAnimation.frameDuration(10);
		jumpAnimation.repeatForever();
		selectedKey = null;
	}

	@Override
	public GenericAnimation<BufferedImage> getByKey(Key key) {
		return switch (key) {
		case ANIM_SYMBOL -> symbolAnimation;
		case ANIM_VALUE -> valueAnimation;
		};
	}

	@Override
	public Stream<GenericAnimation<BufferedImage>> all() {
		return Stream.of(symbolAnimation, valueAnimation);
	}

	@Override
	public BufferedImage currentSprite(Bonus bonus) {
		if (selectedKey == null) {
			return null;
		}
		return switch (selectedKey) {
		case ANIM_SYMBOL -> symbolAnimation.frame(bonus.symbol());
		case ANIM_VALUE -> valueAnimation.frame(bonus.symbol());
		};
	}
}