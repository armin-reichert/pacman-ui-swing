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
package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.lib.Globals.TS;

import java.awt.Color;
import java.awt.Graphics2D;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.PacManIntro;
import de.amr.games.pacman.controller.PacManIntro.Context;
import de.amr.games.pacman.controller.PacManIntro.State;
import de.amr.games.pacman.lib.anim.AnimationMap;
import de.amr.games.pacman.lib.steering.Direction;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.model.actors.Ghost;
import de.amr.games.pacman.model.actors.GhostState;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * Intro scene of the PacMan game.
 * <p>
 * The ghost are presented one after another, then Pac-Man is chased by the ghosts, turns the card and hunts the ghost
 * himself.
 * 
 * @author Armin Reichert
 */
public class PacManIntroScene extends GameScene {

	private PacManIntro intro;
	private Context ctx;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		intro = new PacManIntro(gameController);
		intro.addStateChangeListener(this::onSceneStateChange);
		ctx = intro.context();
	}

	@Override
	public void init() {
		intro.restart(State.START);
		ctx.pacMan.setAnimations(new PacAnimations(ctx.pacMan, gss));
		ctx.pacMan.animations().ifPresent(AnimationMap::ensureRunning);
		ctx.ghosts().forEach(ghost -> ghost.setAnimations(new GhostAnimations(ghost, gss)));
	}

	private void onSceneStateChange(State fromState, State toState) {
		if (fromState == State.CHASING_PAC && toState == State.CHASING_GHOSTS) {
			ctx.ghosts().forEach(ghost -> {
				ghost.animations().ifPresent(anims -> anims.select(GameModel.AK_GHOST_BLUE));
			});
		}
	}

	@Override
	public void update() {
		if (Keyboard.keyPressed("1")) {
			gameController.startPlaying();
		} else if (Keyboard.keyPressed("5")) {
			gameController.addCredit();
		} else {
			intro.update();
			updateAnimations();
		}
	}

	private void updateAnimations() {
		if (intro.state() == State.CHASING_GHOSTS) {
			ctx.ghosts().forEach(ghost -> {
				ghost.animations().ifPresent(anims -> {
					if (ghost.is(GhostState.EATEN)) {
						anims.select(GameModel.AK_GHOST_VALUE);
						anims.selectedAnimation().get().ensureRunning();
					} else {
						anims.select(GameModel.AK_GHOST_BLUE);
						anims.selectedAnimation().get().ensureRunning();
						if (ghost.velocity().length() == 0) {
							anims.animation(GameModel.AK_GHOST_BLUE).get().stop();
						}
					}
				});
			});
		}
	}

	@Override
	public void render(Graphics2D g) {

		switch (intro.state()) {
		case START, PRESENTING_GHOSTS -> {
			drawHUD(g);
			drawGallery(g);
		}
		case SHOWING_POINTS -> {
			drawHUD(g);
			drawGallery(g);
			drawPoints(g, 11, 25);
			var timer = intro.state().timer();
			if (timer.tick() > timer.secToTicks(1)) {
				drawEnergizer(g);
				gss.drawCopyright(g, TS * (3), TS * (32));
			}
		}
		case CHASING_PAC -> {
			drawHUD(g);
			drawGallery(g);
			drawPoints(g, 11, 25);
			gss.drawCopyright(g, TS * (3), TS * (32));
			if (Boolean.TRUE.equals(ctx.blinking.frame())) {
				drawEnergizer(g);
			}
			int offset = intro.state().timer().tick() % 5 < 2 ? 0 : -1;
			drawGuys(g, offset);
		}
		case CHASING_GHOSTS -> {
			drawHUD(g);
			drawGallery(g);
			drawPoints(g, 11, 25);
			gss.drawCopyright(g, TS * (3), TS * (32));
			drawGuys(g, 0);
		}
		case READY_TO_PLAY -> {
			drawHUD(g);
			drawGallery(g);
			drawPoints(g, 11, 25);
			drawGuys(g, 0);
		}
		default -> {
			// nothing to do
		}
		}
		if (game.hasCredit()) {
			gss.drawLevelCounter(g, game.levelCounter());
		}
	}

	private void drawHUD(Graphics2D g) {
		gss.drawScores(g, game, true);
		gss.drawCredit(g, game.credit());
		gss.drawLevelCounter(g, game.levelCounter());
	}

	private void drawGuys(Graphics2D g, int offset) {
		var ghosts = ctx.ghosts().toArray(Ghost[]::new);
		Graphics2D gg = (Graphics2D) g.create();
		gg.translate(offset, 0);
		gss.drawGhost(gg, ghosts[1]);
		gss.drawGhost(gg, ghosts[2]);
		gg.dispose();
		gss.drawGhost(g, ghosts[0]);
		gss.drawGhost(g, ghosts[3]);
		gss.drawPac(g, ctx.pacMan);
	}

	private void drawGallery(Graphics2D g) {
		g.setColor(new Color(222, 222, 255));
		g.setFont(gss.getArcadeFont());
		g.drawString("CHARACTER", TS * (6), TS * (6));
		g.drawString("/", TS * (16), TS * (6));
		g.drawString("NICKNAME", TS * (18), TS * (6));
		for (int id = 0; id < 4; ++id) {
			if (ctx.ghostInfo[id].pictureVisible) {
				int tileY = 7 + 3 * id;
				gss.drawSpriteCenteredOverBox(g, gss.getGhostSprite(id, Direction.RIGHT), TS * (3), TS * (tileY));
				if (ctx.ghostInfo[id].characterVisible) {
					g.setColor(gss.getGhostColor(id));
					g.drawString("-" + ctx.ghostInfo[id].character, TS * (6), TS * (tileY + 1));
				}
				if (ctx.ghostInfo[id].nicknameVisible) {
					g.setColor(gss.getGhostColor(id));
					g.drawString("\"" + ctx.ghostInfo[id].ghost.name() + "\"", TS * (17), TS * (tileY + 1));
				}
			}
		}
	}

	private void drawPoints(Graphics2D g, int tileX, int tileY) {
		g.setColor(gss.getFoodColor(1));
		g.fillRect(TS * (tileX) + 6, TS * (tileY - 1) + 2, 2, 2);
		if (Boolean.TRUE.equals(ctx.blinking.frame())) {
			g.fillOval(TS * (tileX), TS * (tileY + 1) - 2, 10, 10);
		}
		g.setColor(new Color(222, 222, 255));
		g.setFont(gss.getArcadeFont());
		g.drawString("10", TS * (tileX + 2), TS * (tileY));
		g.drawString("50", TS * (tileX + 2), TS * (tileY + 2));
		g.setFont(gss.getArcadeFont().deriveFont(6f));
		g.drawString("PTS", TS * (tileX + 5), TS * (tileY));
		g.drawString("PTS", TS * (tileX + 5), TS * (tileY + 2));
	}

	private void drawEnergizer(Graphics2D g) {
		g.setColor(gss.getFoodColor(1));
		g.fillOval(TS * (3), TS * (20), TS, TS);
	}
}