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

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.AnimationMethods;
import de.amr.games.pacman.lib.animation.GenericAnimation;
import de.amr.games.pacman.lib.animation.GenericAnimationMap;
import de.amr.games.pacman.lib.animation.GenericAnimationCollection;
import de.amr.games.pacman.model.common.actors.Ghost;

/**
 * @author Armin Reichert
 */
public class GhostAnimations implements GenericAnimationCollection<Ghost, GhostAnimation, BufferedImage> {

	private GhostAnimation selectedKey;
	private GenericAnimationMap<Direction, BufferedImage> eyes;
	private GenericAnimation<BufferedImage> flashing;
	private GenericAnimation<BufferedImage> blue;
	private GenericAnimationMap<Direction, BufferedImage> color;
	private GenericAnimation<BufferedImage> numbers;

	public GhostAnimations(int ghostID, Rendering2D r2D) {
		eyes = r2D.createGhostEyesAnimation();
		flashing = r2D.createGhostFlashingAnimation();
		blue = r2D.createGhostBlueAnimation();
		color = r2D.createGhostColorAnimation(ghostID);
		numbers = new GenericAnimation<>(r2D.getNumberSprite(200), r2D.getNumberSprite(400), r2D.getNumberSprite(800),
				r2D.getNumberSprite(1600));
	}

	public void startFlashing(int numFlashes, long ticksTotal) {
		long frameTicks = ticksTotal / (numFlashes * flashing.numFrames());
		flashing.frameDuration(frameTicks);
		flashing.repetitions(numFlashes);
		flashing.restart();
	}

	@Override
	public void ensureRunning() {
		// TODO what?
	}

	@Override
	public void setFrameIndex(int index) {
		// TODO what?
	}

	@Override
	public GhostAnimation selectedKey() {
		return selectedKey;
	}

	@Override
	public void select(GhostAnimation key) {
		selectedKey = key;
		selectedAnimation().ensureRunning();
	}

	@Override
	public AnimationMethods animation(GhostAnimation key) {
		return switch (key) {
		case EYES -> eyes;
		case FLASHING -> flashing;
		case BLUE -> blue;
		case COLOR -> color;
		case VALUE -> numbers;
		};
	}

	@Override
	public Stream<AnimationMethods> animations() {
		return Stream.of(eyes, flashing, blue, color, numbers);
	}

	@Override
	public BufferedImage currentSprite(Ghost ghost) {
		return switch (selectedKey) {
		case EYES -> eyes.get(ghost.wishDir()).frame();
		case FLASHING -> flashing.animate();
		case BLUE -> blue.animate();
		case COLOR -> {
			var sprite = color.get(ghost.wishDir()).frame();
			if (ghost.velocity.length() > 0) {
				color.get(ghost.wishDir()).advance();
			}
			yield sprite;
		}
		case VALUE -> numbers.frame(numberFrame(ghost.bounty));
		};
	}

	private int numberFrame(int number) {
		return switch (number) {
		case 200 -> 0;
		case 400 -> 1;
		case 800 -> 2;
		case 1600 -> 3;
		default -> throw new IllegalArgumentException("Illegal number: " + number); // TODO avoid this to happen
		};
	}
}