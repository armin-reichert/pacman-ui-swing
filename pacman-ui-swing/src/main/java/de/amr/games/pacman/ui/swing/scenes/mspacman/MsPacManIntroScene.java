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
import de.amr.games.pacman.controller.MsPacManIntro;
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

	private MsPacManIntro sceneController;
	private MsPacManIntro.Context ctx;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new MsPacManIntro(gameController);
		ctx = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.restart(MsPacManIntro.State.START);
		ctx.msPacMan.setAnimations(new PacAnimations(ctx.msPacMan, gss));
		ctx.msPacMan.animations().ifPresent(AnimationMap::ensureRunning);
		ctx.ghosts.forEach(ghost -> {
			ghost.setAnimations(new GhostAnimations(ghost, gss));
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
		gss.drawScores(g, gameController.game(), true);
		drawTitle(g);
		drawMarquee(g);
		if (sceneController.state() == MsPacManIntro.State.GHOSTS) {
			drawGhostText(g);
		} else if (sceneController.state() == MsPacManIntro.State.MSPACMAN
				|| sceneController.state() == MsPacManIntro.State.READY_TO_PLAY) {
			drawMsPacManText(g);
		}
		ctx.ghosts.forEach(ghost -> gss.drawGhost(g, ghost));
		gss.drawPac(g, ctx.msPacMan);
		gss.drawCopyright(g, TS * (6), TS * (28));
		gss.drawCredit(g, game.credit());
		if (game.hasCredit()) {
			gss.drawLevelCounter(g, game.levelCounter());
		}
	}

	private void drawMarquee(Graphics2D g) {
		var on = ctx.marqueeState();
		for (int i = 0; i < ctx.numBulbs; ++i) {
			g.setColor(on.get(i) ? new Color(222, 222, 255) : Color.RED);
			if (i <= 33) {
				g.fillRect(60 + 4 * i, 148, 2, 2);
			} else if (i <= 48) {
				g.fillRect(192, 280 - 4 * i, 2, 2);
			} else if (i <= 81) {
				g.fillRect(384 - 4 * i, 88, 2, 2);
			} else {
				g.fillRect(60, 4 * i - 236, 2, 2);
			}
		}
	}

	private void drawTitle(Graphics2D g) {
		var tx = sceneController.context().titlePosition.x();
		var ty = sceneController.context().titlePosition.y();
		g.setFont(gss.getArcadeFont());
		g.setColor(Color.ORANGE);
		g.drawString("\"MS PAC-MAN\"", tx, ty);
	}

	private void drawGhostText(Graphics2D g) {
		var tx = sceneController.context().titlePosition.x();
		var y0 = sceneController.context().stopY;
		g.setColor(Color.WHITE);
		g.setFont(gss.getArcadeFont());
		if (ctx.ghostIndex == 0) {
			g.drawString("WITH", tx, y0 + TS * 3);
		}
		Ghost ghost = ctx.ghosts.get(ctx.ghostIndex);
		g.setColor(gss.getGhostColor(ghost.id()));
		g.drawString(ghost.name().toUpperCase(), TS * (14 - ghost.name().length() / 2), y0 + TS * 6);
	}

	private void drawMsPacManText(Graphics2D g) {
		var tx = sceneController.context().titlePosition.x();
		var y0 = sceneController.context().stopY;
		g.setColor(Color.WHITE);
		g.setFont(gss.getArcadeFont());
		g.drawString("STARRING", tx, y0 + TS * 3);
		g.setColor(Color.YELLOW);
		g.drawString("MS PAC-MAN", tx, y0 + TS * 6);
	}

}