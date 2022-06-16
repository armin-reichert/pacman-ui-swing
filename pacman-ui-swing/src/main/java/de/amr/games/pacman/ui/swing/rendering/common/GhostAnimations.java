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
import java.util.HashMap;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.Animations;
import de.amr.games.pacman.lib.animation.SimpleAnimation;
import de.amr.games.pacman.lib.animation.AnimationMap;
import de.amr.games.pacman.lib.animation.SpriteArray;
import de.amr.games.pacman.model.common.actors.Ghost;

/**
 * @author Armin Reichert
 */
public class GhostAnimations extends Animations<Ghost> {

	private AnimationMap<Direction, BufferedImage> eyes;
	private SimpleAnimation<BufferedImage> flashing;
	private SimpleAnimation<BufferedImage> blue;
	private AnimationMap<Direction, BufferedImage> color;
	private SpriteArray<BufferedImage> values;

	public GhostAnimations(int ghostID, Rendering2D r2D) {
		animationsByName = new HashMap<>(6);
		put("ghost-anim-eyes", eyes = r2D.createGhostEyesAnimation());
		put("ghost-anim-flashing", flashing = r2D.createGhostFlashingAnimation());
		put("ghost-anim-blue", blue = r2D.createGhostBlueAnimation());
		put("ghost-anim-color", color = r2D.createGhostColorAnimation(ghostID));
		put("ghost-anim-value", values = r2D.createGhostValueList());
		select("ghost-anim-color");
	}

	public void startFlashing(int numFlashes, long ticksTotal) {
		long frameTicks = ticksTotal / (numFlashes * flashing.numFrames());
		flashing.frameDuration(frameTicks);
		flashing.repeat(numFlashes);
		flashing.restart();
	}

	@Override
	public BufferedImage current(Ghost ghost) {
		return switch (selected) {
		case "ghost-anim-eyes" -> eyes.get(ghost.wishDir()).frame();
		case "ghost-anim-flashing" -> flashing.animate();
		case "ghost-anim-blue" -> blue.animate();
		case "ghost-anim-color" -> {
			var sprite = color.get(ghost.wishDir()).frame();
			if (ghost.velocity.length() > 0) {
				color.get(ghost.wishDir()).advance();
			}
			yield sprite;
		}
		case "ghost-anim-value" -> ghost.killIndex >= 0 ? values.frame(ghost.killIndex) : null;
		default -> null;
		};
	}
}