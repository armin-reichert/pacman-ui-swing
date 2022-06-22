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
package de.amr.games.pacman.ui.swing.scenes.mspacman;

import java.awt.Graphics2D;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.mspacman.Intermission2Controller;
import de.amr.games.pacman.lib.animation.SpriteAnimations;
import de.amr.games.pacman.model.common.actors.AnimKeys;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.mspacman.Spritesheet_MsPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intermission scene 2: "The chase".
 * <p>
 * Pac-Man and Ms. Pac-Man chase each other across the screen over and over. After three turns, they both rapidly run
 * from left to right and right to left. (Played after round 5)
 * 
 * @author Armin Reichert
 */
public class MsPacManIntermissionScene2 extends GameScene {

	private Intermission2Controller sceneController;
	private Intermission2Controller.Context $;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new Intermission2Controller(gameController);
		$ = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.restartInInitialState(Intermission2Controller.State.FLAP);
		$.flap.animation = Spritesheet_MsPacMan.get().createFlapAnimation();
		$.msPacMan.setAnimations(new PacAnimations(r2D));
		$.msPacMan.animations().ifPresent(SpriteAnimations::ensureRunning);
		$.pacMan.setAnimations(new PacAnimations(r2D));
		var husbandMunching = Spritesheet_MsPacMan.get().createHusbandMunchingAnimations();
		$.pacMan.animations().ifPresent(anims -> anims.put(AnimKeys.PAC_MUNCHING, husbandMunching));
		$.pacMan.animations().ifPresent(SpriteAnimations::ensureRunning);
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		((Spritesheet_MsPacMan) r2D).drawFlap(g, $.flap);
		r2D.drawPac(g, $.msPacMan);
		r2D.drawPac(g, $.pacMan);
	}
}