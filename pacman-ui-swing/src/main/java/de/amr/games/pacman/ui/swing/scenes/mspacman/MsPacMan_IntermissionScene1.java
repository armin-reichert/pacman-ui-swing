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
package de.amr.games.pacman.ui.swing.scenes.mspacman;

import java.awt.Dimension;
import java.awt.Graphics2D;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.mspacman.Intermission1Controller;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.ui.GameSounds;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.entity.mspacman.Flap2D;
import de.amr.games.pacman.ui.swing.entity.mspacman.Heart2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI_Swing;

/**
 * Intermission scene 1: "They meet".
 * <p>
 * Pac-Man leads Inky and Ms. Pac-Man leads Pinky. Soon, the two Pac-Men are about to collide, they quickly move
 * upwards, causing Inky and Pinky to collide and vanish. Finally, Pac-Man and Ms. Pac-Man face each other at the top of
 * the screen and a big pink heart appears above them. (Played after round 2)
 * 
 * @author Armin Reichert
 */
public class MsPacMan_IntermissionScene1 extends GameScene {

	private final Intermission1Controller sceneController = new Intermission1Controller();

	private Player2D msPacMan2D;
	private Player2D pacMan2D;
	private Ghost2D inky2D;
	private Ghost2D pinky2D;
	private Flap2D flap2D;
	private Heart2D heart2D;

	public MsPacMan_IntermissionScene1(PacManGameUI_Swing ui, Dimension size) {
		super(ui, size, ScenesMsPacMan.RENDERING, ScenesMsPacMan.SOUNDS);
	}

	@Override
	public void init(GameController gameController) {
		super.init(gameController);

		sceneController.playIntermissionSound = () -> sounds.play(GameSounds.INTERMISSION_1);
		sceneController.playFlapAnimation = () -> flap2D.animation.restart();
		sceneController.init(gameController);

		flap2D = new Flap2D(sceneController.flap, r2D);
		msPacMan2D = new Player2D(sceneController.msPac, game, r2D);
		pacMan2D = new Player2D(sceneController.pacMan, game, r2D);
		// overwrite by Pac-Man instead of Ms. Pac-Man sprites:
		pacMan2D.munchings = r2D.createSpouseMunchingAnimations();
		inky2D = new Ghost2D(sceneController.inky, game, r2D);
		pinky2D = new Ghost2D(sceneController.pinky, game, r2D);
		heart2D = new Heart2D(sceneController.heart);
		heart2D.setImage(r2D.getHeart());

		// start animations
		msPacMan2D.munchings.values().forEach(TimedSeq::restart);
		pacMan2D.munchings.values().forEach(TimedSeq::restart);
		inky2D.animKicking.values().forEach(TimedSeq::restart);
		pinky2D.animKicking.values().forEach(TimedSeq::restart);
	}

	@Override
	public void update() {
		sceneController.updateState();
	}

	@Override
	public void render(Graphics2D g) {
		flap2D.render(g);
		msPacMan2D.render(g);
		pacMan2D.render(g);
		inky2D.render(g);
		pinky2D.render(g);
		heart2D.render(g);
	}
}