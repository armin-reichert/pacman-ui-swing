/*
MIT License

Copyright (c) 2021-2023 Armin Reichert

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

import de.amr.games.pacman.lib.anim.AnimationMap;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.model.actors.Ghost;

/**
 * @author Armin Reichert
 */
public class GhostAnimations extends AnimationMap {

	public GhostAnimations(Ghost ghost, Rendering2D r2D) {
		super(GameModel.ANIMATION_MAP_CAPACITY);
		put(GameModel.AK_GHOST_EYES, r2D.createGhostEyesAnimationMap(ghost));
		put(GameModel.AK_GHOST_FLASHING, r2D.createGhostFlashingAnimation());
		put(GameModel.AK_GHOST_BLUE, r2D.createGhostBlueAnimation());
		put(GameModel.AK_GHOST_COLOR, r2D.createGhostColorAnimationMap(ghost));
		put(GameModel.AK_GHOST_VALUE, r2D.createGhostValueList());
		select(GameModel.AK_GHOST_COLOR);
	}
}