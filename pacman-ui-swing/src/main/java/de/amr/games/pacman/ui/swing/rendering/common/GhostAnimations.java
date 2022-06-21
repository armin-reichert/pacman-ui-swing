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
import de.amr.games.pacman.lib.animation.SpriteAnimationMap;
import de.amr.games.pacman.lib.animation.SpriteAnimations;
import de.amr.games.pacman.model.common.actors.AnimKeys;
import de.amr.games.pacman.model.common.actors.Ghost;

/**
 * @author Armin Reichert
 */
public class GhostAnimations extends SpriteAnimations<Ghost> {

	public GhostAnimations(int ghostID, Rendering2D r2D) {
		animationsByName = new HashMap<>();
		animationsByName.put(AnimKeys.GHOST_EYES, r2D.createGhostEyesAnimationMap());
		animationsByName.put(AnimKeys.GHOST_FLASHING, r2D.createGhostFlashingAnimation());
		animationsByName.put(AnimKeys.GHOST_BLUE, r2D.createGhostBlueAnimation());
		animationsByName.put(AnimKeys.GHOST_COLOR, r2D.createGhostColorAnimationMap(ghostID));
		animationsByName.put(AnimKeys.GHOST_VALUE, r2D.createGhostValueList());
		select(AnimKeys.GHOST_COLOR);
	}

	@Override
	public BufferedImage current(Ghost ghost) {
		return (BufferedImage) switch (selected) {
		case AnimKeys.GHOST_EYES -> toMap(AnimKeys.GHOST_EYES).get(ghost.wishDir()).animate();
		case AnimKeys.GHOST_COLOR -> toMap(AnimKeys.GHOST_COLOR).get(ghost.wishDir()).animate();
		default -> selectedAnimation().animate();
		};
	}

	private SpriteAnimationMap<Direction, BufferedImage> toMap(String name) {
		return super.<Direction, BufferedImage>castToMap(name);
	}
}