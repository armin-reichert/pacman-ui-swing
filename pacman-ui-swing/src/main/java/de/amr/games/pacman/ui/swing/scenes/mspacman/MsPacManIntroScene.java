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

import static de.amr.games.pacman.lib.Globals.TS;

import java.awt.Color;
import java.awt.Graphics2D;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.MsPacManIntroController;
import de.amr.games.pacman.controller.MsPacManIntroData;
import de.amr.games.pacman.controller.MsPacManIntroState;
import de.amr.games.pacman.lib.anim.AnimationMap;
import de.amr.games.pacman.model.actors.Ghost;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * Intro scene of the Ms. Pac-Man game. The ghosts and Ms. Pac-Man are introduced one after another.
 * 
 * @author Armin Reichert
 */
public class MsPacManIntroScene extends GameScene {

	private MsPacManIntroController sceneController;
	private MsPacManIntroData ctx;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new MsPacManIntroController(gameController);
		ctx = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.restart(MsPacManIntroState.START);
		ctx.msPacMan.setAnimations(new PacAnimations(ctx.msPacMan, r2D));
		ctx.msPacMan.animations().ifPresent(AnimationMap::ensureRunning);
		ctx.ghosts.forEach(ghost -> {
			ghost.setAnimations(new GhostAnimations(ghost, r2D));
			ghost.animations().ifPresent(AnimationMap::ensureRunning);
		});
	}

	@Override
	public void update() {
		if (Keyboard.keyPressed("1")) {
			gameController.startPlaying();
		} else if (Keyboard.keyPressed("5")) {
			gameController.addCredit();
		} else {
			sceneController.update();
		}
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawScores(g, gameController.game(), true);
		drawTitle(g);
		drawLights(g, 32, 16);
		if (sceneController.state() == MsPacManIntroState.GHOSTS) {
			drawGhostText(g);
		} else if (sceneController.state() == MsPacManIntroState.MSPACMAN
				|| sceneController.state() == MsPacManIntroState.READY_TO_PLAY) {
			drawMsPacManText(g);
		}
		ctx.ghosts.forEach(ghost -> r2D.drawGhost(g, ghost));
		r2D.drawPac(g, ctx.msPacMan);
		r2D.drawCopyright(g, TS * (6), TS * (28));
		r2D.drawCredit(g, game.credit());
		if (game.hasCredit()) {
			r2D.drawLevelCounter(g, game.levelCounter());
		}
	}

	private void drawTitle(Graphics2D g) {
		var tx = sceneController.context().titlePosition.x();
		var ty = sceneController.context().titlePosition.y();
		g.setFont(r2D.getArcadeFont());
		g.setColor(Color.ORANGE);
		g.drawString("\"MS PAC-MAN\"", tx, ty);
	}

	private void drawGhostText(Graphics2D g) {
		var tx = sceneController.context().titlePosition.x();
		var y0 = sceneController.context().blinkyEndPosition.y();
		g.setColor(Color.WHITE);
		g.setFont(r2D.getArcadeFont());
		if (ctx.ghostIndex() == 0) {
			g.drawString("WITH", tx, y0 + TS * 3);
		}
		Ghost ghost = ctx.ghosts.get(ctx.ghostIndex());
		g.setColor(r2D.getGhostColor(ghost.id()));
		g.drawString(ghost.name().toUpperCase(), TS * (14 - ghost.name().length() / 2), y0 + TS * 6);
	}

	private void drawMsPacManText(Graphics2D g) {
		var tx = sceneController.context().titlePosition.x();
		var y0 = sceneController.context().blinkyEndPosition.y();
		g.setColor(Color.WHITE);
		g.setFont(r2D.getArcadeFont());
		g.drawString("STARRING", tx, y0 + TS * 3);
		g.setColor(Color.YELLOW);
		g.drawString("MS PAC-MAN", tx, y0 + TS * 6);
	}

	private void drawLights(Graphics2D g, int numDotsX, int numDotsY) {
		var x0 = sceneController.context().blinkyEndPosition.x();
		var y0 = sceneController.context().blinkyEndPosition.y();
		long time = ctx.marqueeTimer.tick();
		int light = (int) (time / 2) % (numDotsX / 2);
		for (int dot = 0; dot < 2 * (numDotsX + numDotsY); ++dot) {
			int x = 0;
			int y = 0;
			if (dot <= numDotsX) {
				x = dot;
			} else if (dot < numDotsX + numDotsY) {
				x = numDotsX;
				y = dot - numDotsX;
			} else if (dot < 2 * numDotsX + numDotsY + 1) {
				x = 2 * numDotsX + numDotsY - dot;
				y = numDotsY;
			} else {
				y = 2 * (numDotsX + numDotsY) - dot;
			}
			g.setColor((dot + light) % (numDotsX / 2) == 0 ? Color.PINK : Color.RED);
			g.fillRect((int) x0 + 4 * x, (int) y0 + 4 * y, 2, 2);
		}
	}
}