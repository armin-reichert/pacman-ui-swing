/*
MIT License

Copyright (c) 2021 Armin Reichert

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

import static de.amr.games.pacman.model.common.GameVariant.PACMAN;
import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.awt.Dimension;
import java.awt.Graphics2D;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.controller.pacman.Intermission3Controller;
import de.amr.games.pacman.controller.pacman.Intermission3Controller.IntermissionState;
import de.amr.games.pacman.ui.PacManGameSound;
import de.amr.games.pacman.ui.swing.PacManGameUI_Swing;
import de.amr.games.pacman.ui.swing.rendering.common.Player2D;
import de.amr.games.pacman.ui.swing.rendering.pacman.PacManGameRendering;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Third intermission scene: Blinky in shred dress chases Pac-Man, comes back half-naked drawing dress over the floor.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene3 extends GameScene {

	private class SceneController extends Intermission3Controller {

		public SceneController(PacManGameController gameController) {
			super(gameController);
		}

		@Override
		public void playIntermissionSound() {
			sounds.loop(PacManGameSound.INTERMISSION_3, 2);
		}
	}

	private SceneController sceneController;
	private Player2D pacMan2D;

	public PacMan_IntermissionScene3(PacManGameController controller, Dimension size) {
		super(controller, size, PacManGameUI_Swing.RENDERING_PACMAN, PacManGameUI_Swing.SOUND.get(PACMAN));
	}

	@Override
	public void init() {
		sceneController = new SceneController(gameController);
		sceneController.init();
		pacMan2D = new Player2D(sceneController.pac);
		pacMan2D.setMunchingAnimations(rendering.createPlayerMunchingAnimations());
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void end() {
	}

	@Override
	public void render(Graphics2D g) {
		PacManGameRendering r = (PacManGameRendering) rendering;
		r.drawLevelCounter(g, gameController.game(), t(25), t(34));
		pacMan2D.render(g);
		if (sceneController.currentStateID == IntermissionState.CHASING_PACMAN) {
			r.drawBlinkyPatched(g, sceneController.blinky);
		} else {
			r.drawBlinkyNaked(g, sceneController.blinky);
		}
	}
}