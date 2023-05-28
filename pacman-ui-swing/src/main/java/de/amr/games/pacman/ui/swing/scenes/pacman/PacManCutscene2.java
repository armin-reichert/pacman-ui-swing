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

package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.lib.Globals.TS;
import static de.amr.games.pacman.lib.Globals.v2i;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.event.GameEvents;
import de.amr.games.pacman.lib.anim.Animated;
import de.amr.games.pacman.lib.anim.SimpleAnimation;
import de.amr.games.pacman.lib.steering.Direction;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.model.actors.Ghost;
import de.amr.games.pacman.model.actors.Pac;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.pacman.SpritesheetPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * @author Armin Reichert
 */
public class PacManCutscene2 extends GameScene {

	private int initialDelay;
	private int frame;
	private Pac pac;
	private Ghost blinky;
	private SimpleAnimation<BufferedImage> stretched;

	@Override
	public void init() {
		frame = -1;
		initialDelay = 120;

		pac = new Pac("Pac-Man");
		pac.setAnimations(new PacAnimations(pac, gss));
		pac.animations().ifPresent(anims -> anims.select(GameModel.AK_PAC_MUNCHING));
		pac.animation(GameModel.AK_PAC_MUNCHING).ifPresent(Animated::restart);
		pac.placeAtTile(v2i(29, 20), 0, 0);
		pac.setMoveDir(Direction.LEFT);
		pac.setPixelSpeed(1.15f);
		pac.show();

		stretched = SpritesheetPacMan.get().createBlinkyStretchedAnimation();
		blinky = new Ghost(GameModel.RED_GHOST, "Blinky");
		blinky.setAnimations(new GhostAnimations(blinky, gss));
		var damagedBlinkyAnimation = SpritesheetPacMan.get().createBlinkyDamagedAnimation();
		blinky.animations().ifPresent(anims -> anims.put(GameModel.AK_BLINKY_DAMAGED, damagedBlinkyAnimation));
		blinky.animations().ifPresent(anims -> anims.select(GameModel.AK_GHOST_COLOR));
		blinky.animation(GameModel.AK_GHOST_COLOR).ifPresent(Animated::restart);
		blinky.placeAtTile(v2i(28, 20), 0, 0);
		blinky.setMoveAndWishDir(Direction.LEFT);
		blinky.setPixelSpeed(0);
		blinky.hide();
	}

	@Override
	public void update() {
		if (initialDelay > 0) {
			--initialDelay;
			return;
		}
		++frame;
		if (frame == 0) {
			GameEvents.publishSoundEvent(GameModel.SE_START_INTERMISSION_2);
		} else if (frame == 110) {
			blinky.setPixelSpeed(1.25f);
			blinky.show();
		} else if (frame == 196) {
			blinky.setPixelSpeed(0.17f);
			stretched.setFrameIndex(1);
		} else if (frame == 226) {
			stretched.setFrameIndex(2);
		} else if (frame == 248) {
			blinky.setPixelSpeed(0);
			blinky.animations().ifPresent(anims -> anims.selectedAnimation().get().stop());
			stretched.setFrameIndex(3);
		} else if (frame == 328) {
			stretched.setFrameIndex(4);
		} else if (frame == 329) {
			blinky.animations().ifPresent(anims -> anims.select(GameModel.AK_BLINKY_DAMAGED));
			blinky.animation(GameModel.AK_BLINKY_DAMAGED).ifPresent(damaged -> damaged.setFrameIndex(0));
		} else if (frame == 389) {
			blinky.animation(GameModel.AK_BLINKY_DAMAGED).ifPresent(damaged -> damaged.setFrameIndex(1));
		} else if (frame == 508) {
			stretched = null;
		} else if (frame == 509) {
			gameController.terminateCurrentState();
			return;
		}
		pac.move();
		pac.animate();
		blinky.move();
		blinky.animate();
	}

	@Override
	public void render(Graphics2D g) {
		if (stretched != null) {
			gss.drawSprite(g, stretched.frame(), TS * (14), TS * (19) + 3);
		}
		gss.drawGhost(g, blinky);
		gss.drawPac(g, pac);
	}
}