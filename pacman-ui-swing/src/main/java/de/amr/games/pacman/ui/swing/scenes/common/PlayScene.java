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
package de.amr.games.pacman.ui.swing.scenes.common;

import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import java.util.stream.Collectors;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.controller.PacManGameState;
import de.amr.games.pacman.controller.event.PacManGameEvent;
import de.amr.games.pacman.controller.event.PacManGameStateChangeEvent;
import de.amr.games.pacman.controller.event.ScatterPhaseStartedEvent;
import de.amr.games.pacman.lib.TickTimerEvent;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.model.common.GhostState;
import de.amr.games.pacman.ui.PacManGameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.common.AbstractPacManGameRendering;
import de.amr.games.pacman.ui.swing.rendering.common.Bonus2D;
import de.amr.games.pacman.ui.swing.rendering.common.Energizer2D;
import de.amr.games.pacman.ui.swing.rendering.common.Ghost2D;
import de.amr.games.pacman.ui.swing.rendering.common.Player2D;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	private Player2D player2D;
	private List<Ghost2D> ghosts2D;
	private List<Energizer2D> energizers2D;
	private Bonus2D bonus2D;

	private TimedSequence<?> mazeFlashing;

	public PlayScene(PacManGameController controller, Dimension size, AbstractPacManGameRendering rendering,
			SoundManager sounds) {
		super(controller, size, rendering, sounds);
	}

	@Override
	public void init() {
		player2D = new Player2D(game().player());
		player2D.setRendering(rendering);

		ghosts2D = game().ghosts().map(Ghost2D::new).collect(Collectors.toList());
		ghosts2D.forEach(ghost2D -> ghost2D.setRendering(rendering));

		energizers2D = game().level().world.energizerTiles().map(Energizer2D::new).collect(Collectors.toList());

		bonus2D = new Bonus2D();
		bonus2D.setRendering(rendering);

		mazeFlashing = rendering.mazeFlashing(game().level().mazeNumber).repetitions(game().level().numFlashes);
		mazeFlashing.reset();

		game().player().powerTimer.addEventListener(this::handleGhostsFlashing);
	}

	@Override
	public void update() {
		if (gameController.currentStateID == PacManGameState.LEVEL_COMPLETE) {
			playLevelCompleteAnimation(gameController.currentStateID);
		} else if (gameController.currentStateID == PacManGameState.LEVEL_STARTING) {
			gameController.stateTimer().expire();
		}
	}

	@Override
	public void end() {
		game().player().powerTimer.removeEventListener(this::handleGhostsFlashing);
	}

	@Override
	public void onPacManGameStateChange(PacManGameStateChangeEvent e) {
		sounds.setMuted(gameController.isAttractMode());

		// enter READY
		if (e.newGameState == PacManGameState.READY) {
			energizers2D.forEach(energizer2D -> energizer2D.getBlinkingAnimation().reset());
			rendering.mazeFlashing(game().level().mazeNumber).reset();
			if (!gameController.isAttractMode() && !gameController.isGameRunning()) {
				sounds.play(PacManGameSound.GAME_READY);
			}
		}

		// enter HUNTING
		if (e.newGameState == PacManGameState.HUNTING) {
			energizers2D.forEach(energizer2D -> energizer2D.getBlinkingAnimation().restart());
			player2D.getMunchingAnimations().values().forEach(TimedSequence::restart);
			ghosts2D.forEach(ghost2D -> {
				ghost2D.getKickingAnimations().values().forEach(TimedSequence::restart);
			});
		}

		// enter PACMAN_DYING
		if (e.newGameState == PacManGameState.PACMAN_DYING) {
			gameController.stateTimer().resetSeconds(5);
			gameController.stateTimer().start();
			sounds.stopAll();
			playAnimationPlayerDying();
		}

		// enter GHOST_DYING
		if (e.newGameState == PacManGameState.GHOST_DYING) {
			sounds.play(PacManGameSound.GHOST_EATEN);
			energizers2D.forEach(energizer2D -> energizer2D.getBlinkingAnimation().restart());
		}

		// exit GHOST_DYING
		if (e.oldGameState == PacManGameState.GHOST_DYING) {
			// the dead ghost(s) will return home now
			if (game().ghosts(GhostState.DEAD).count() > 0) {
				sounds.loop(PacManGameSound.GHOST_RETURNING_HOME, Integer.MAX_VALUE);
			}
		}

		// enter LEVEL_COMPLETE
		if (e.newGameState == PacManGameState.LEVEL_COMPLETE) {
			mazeFlashing = rendering.mazeFlashing(game().level().mazeNumber);
			sounds.stopAll();
		}

		// enter GAME_OVER
		if (e.newGameState == PacManGameState.GAME_OVER) {
			ghosts2D.forEach(ghost2D -> {
				ghost2D.getKickingAnimations().values().forEach(TimedSequence::reset);
			});
		}
	}

	@Override
	public void onGameEvent(PacManGameEvent gameEvent) {
		sounds.setMuted(gameController.isAttractMode());
		super.onGameEvent(gameEvent);
	}

	@Override
	public void onScatterPhaseStarted(ScatterPhaseStartedEvent e) {
		if (e.scatterPhase > 0) {
			sounds.stop(PacManGameSound.SIRENS.get(e.scatterPhase - 1));
		}
		sounds.loop(PacManGameSound.SIRENS.get(e.scatterPhase), Integer.MAX_VALUE);
	}

	@Override
	public void onPlayerLostPower(PacManGameEvent e) {
		sounds.stop(PacManGameSound.PACMAN_POWER);
	}

	@Override
	public void onPlayerFoundFood(PacManGameEvent e) {
		sounds.play(PacManGameSound.PACMAN_MUNCH);
	}

	@Override
	public void onPlayerGainsPower(PacManGameEvent e) {
		sounds.loop(PacManGameSound.PACMAN_POWER, Integer.MAX_VALUE);
		ghosts2D.stream().filter(ghost2D -> ghost2D.ghost.is(GhostState.FRIGHTENED)).forEach(ghost2D -> {
			ghost2D.getFlashingAnimation().reset();
			ghost2D.getFrightenedAnimation().restart();
		});
	}

	@Override
	public void onBonusActivated(PacManGameEvent e) {
		bonus2D.setBonus(gameController.game().bonus());
		if (bonus2D.getJumpAnimation() != null) {
			bonus2D.getJumpAnimation().restart();
		}
	}

	@Override
	public void onBonusEaten(PacManGameEvent e) {
		sounds.play(PacManGameSound.BONUS_EATEN);
		if (bonus2D.getJumpAnimation() != null) {
			bonus2D.getJumpAnimation().reset();
		}
	}

	@Override
	public void onExtraLife(PacManGameEvent e) {
		sounds.play(PacManGameSound.EXTRA_LIFE);
		gameController.getUI().showFlashMessage(1, "Extra life!");
	}

	@Override
	public void onGhostReturnsHome(PacManGameEvent e) {
		sounds.play(PacManGameSound.GHOST_RETURNING_HOME);
	}

	@Override
	public void onGhostEntersHouse(PacManGameEvent e) {
		if (game().ghosts(GhostState.DEAD).count() == 0) {
			sounds.stop(PacManGameSound.GHOST_RETURNING_HOME);
		}
	};

	@Override
	public void render(Graphics2D g) {
		rendering.drawMaze(g, game().level().mazeNumber, 0, t(3), mazeFlashing.isRunning());
		if (!mazeFlashing.isRunning()) {
			rendering.hideEatenFood(g, game().level().world.tiles(), game().level()::isFoodRemoved);
			energizers2D.forEach(energizer2D -> energizer2D.render(g));
		}
		if (gameController.isAttractMode()) {
			rendering.drawGameState(g, game(), PacManGameState.GAME_OVER);
		} else {
			rendering.drawGameState(g, game(), gameController.currentStateID);
		}
		bonus2D.render(g);
		player2D.render(g);
		ghosts2D.forEach(ghost2D -> {
			ghost2D.setDisplayFrightened(game().player().powerTimer.isRunning());
			ghost2D.render(g);
		});
		if (gameController.isGameRunning()) {
			rendering.drawScore(g, game(), false);
			rendering.drawLivesCounter(g, game(), t(2), t(34));
		} else {
			rendering.drawScore(g, game(), true);
		}
		rendering.drawLevelCounter(g, game(), t(25), t(34));
	}

	private void handleGhostsFlashing(TickTimerEvent e) {
		if (e.type == TickTimerEvent.Type.HALF_EXPIRED) {
			ghosts2D.stream().filter(ghost2D -> ghost2D.ghost.is(GhostState.FRIGHTENED)).forEach(ghost2D -> {
				TimedSequence<?> flashing = ghost2D.getFlashingAnimation();
				long frameTime = e.ticks / (game().level().numFlashes * flashing.numFrames());
				flashing.frameDuration(frameTime).repetitions(game().level().numFlashes).restart();
			});
		}
	}

	private void playAnimationPlayerDying() {
		ghosts2D.forEach(ghost2D -> {
			ghost2D.getKickingAnimations().values().forEach(TimedSequence::reset);
		});
		player2D.getDyingAnimation().delay(120).onStart(() -> {
			game().ghosts().forEach(ghost -> ghost.setVisible(false));
			if (gameController.isGameRunning()) {
				sounds.play(PacManGameSound.PACMAN_DEATH);
			}
		}).restart();
	}

	private void playLevelCompleteAnimation(PacManGameState state) {
		if (gameController.stateTimer().isRunningSeconds(2)) {
			game().ghosts().forEach(ghost -> ghost.setVisible(false));
		}
		if (gameController.stateTimer().isRunningSeconds(3)) {
			mazeFlashing.restart();
		}
		mazeFlashing.animate();
		if (mazeFlashing.isComplete()) {
			gameController.stateTimer().expire();
		}
	}
}