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
package de.amr.games.pacman.ui.swing.shell;

import static de.amr.games.pacman.controller.GameState.CREDIT;
import static de.amr.games.pacman.controller.GameState.INTRO;
import static de.amr.games.pacman.lib.Globals.TS;
import static de.amr.games.pacman.model.GameVariant.MS_PACMAN;
import static de.amr.games.pacman.ui.swing.shell.Keyboard.MOD_CTRL;
import static de.amr.games.pacman.ui.swing.shell.Keyboard.MOD_SHIFT;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.tinylog.Logger;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameEventListener;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.event.SoundEvent;
import de.amr.games.pacman.lib.math.Vector2i;
import de.amr.games.pacman.model.world.World;
import de.amr.games.pacman.ui.swing.app.GameLoop;
import de.amr.games.pacman.ui.swing.lib.Ujfc;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.WorldAnimations;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.rendering.pacman.SpritesheetPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.BootScene;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.scenes.common.PlayScene;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacManCreditScene;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacManIntermissionScene1;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacManIntermissionScene2;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacManIntermissionScene3;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacManIntroScene;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacManCreditScene;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacManCutscene1;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacManCutscene2;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacManCutscene3;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacManIntroScene;

/**
 * A Swing UI for the Pac-Man / Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class PacManGameUI implements GameEventListener {

	private static boolean debugDraw = false;

	public static boolean isDebugDraw() {
		return debugDraw;
	}

	public static void toggleDebugDraw() {
		debugDraw = !debugDraw;
	}

	private final GameLoop gameLoop;
	private final GameController gameController;

	private final Vector2i unscaledSize;
	private final Vector2i scaledSize;
	private final float scaling;
	private final JFrame window;
	private final Timer titleUpdateTimer;
	private final Canvas canvas;
	private final FlashMessageDisplay flashMessageDisplay;

	private final List<GameScene> gameScenesPacMan = List.of( //
			new BootScene(), //
			new PacManIntroScene(), //
			new PacManCreditScene(), //
			new PacManCutscene1(), //
			new PacManCutscene2(), //
			new PacManCutscene3(), //
			new PlayScene() //
	);

	private final List<GameScene> gameScenesMsPacMan = List.of( //
			new BootScene(), //
			new MsPacManIntroScene(), //
			new MsPacManCreditScene(), //
			new MsPacManIntermissionScene1(), //
			new MsPacManIntermissionScene2(), //
			new MsPacManIntermissionScene3(), //
			new PlayScene()//
	);

	private GameScene currentGameScene;

	public PacManGameUI(GameLoop gameLoop, GameController controller, float height) {
		this.gameLoop = gameLoop;
		this.gameController = controller;
		this.unscaledSize = new Vector2i(World.TILES_X * TS, World.TILES_Y * TS);
		this.scaling = height / unscaledSize.y();
		this.scaledSize = new Vector2i(Math.round(scaling * unscaledSize.x()), Math.round(scaling * unscaledSize.y()));

		flashMessageDisplay = new FlashMessageDisplay(unscaledSize);

		canvas = new Canvas();
		canvas.setBackground(Color.BLACK);
		canvas.setSize(scaledSize.x(), scaledSize.y());
		canvas.setFocusable(false);

		window = new JFrame();
		window.setTitle("Swing: Pac-Man");
		window.setBackground(Color.BLACK);
		window.setResizable(false);
		window.setFocusable(true);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setIconImage(Ujfc.image("/pacman/graphics/pacman.png"));
		window.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				titleUpdateTimer.stop();
				gameLoop.end();
			}
		});
		window.getContentPane().add(canvas);
		window.addKeyListener(Keyboard.theKeyboard.handler);

		titleUpdateTimer = new Timer(1000, e -> window.setTitle(String.format("%s (%d fps, JFC Swing)",
				gameController.game().variant() == MS_PACMAN ? "Ms. Pac-Man" : "Pac-Man", gameLoop.clock.getLastFPS())));

		gameController.restart(GameState.BOOT);
	}

	public void show() {
		updateGameScene(gameController.state(), true);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		moveMousePointerOutOfSight();
		titleUpdateTimer.start();
	}

	@Override
	public void onGameEvent(GameEvent event) {
		GameEventListener.super.onGameEvent(event);
		currentGameScene.onGameEvent(event);
	}

	@Override
	public void onUnspecifiedChange(GameEvent e) {
		updateGameScene(gameController.state(), true);
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		updateGameScene(e.newGameState, false);
	}

	// this is dubious but we need some point in time where the animations are created
	@Override
	public void onLevelStarting(GameEvent e) {
		gameController.game().level().ifPresent(level -> {
			var gss = switch (gameController.game().variant()) {
			case MS_PACMAN -> SpritesheetMsPacMan.get();
			case PACMAN -> SpritesheetPacMan.get();
			};
			level.world().setAnimations(new WorldAnimations(gss, gss.mazeNumber(level.number())));
			level.pac().setAnimations(new PacAnimations(level.pac(), gss));
			level.ghosts().forEach(ghost -> ghost.setAnimations(new GhostAnimations(ghost, gss)));
		});
	}

	@Override
	public void onSoundEvent(SoundEvent e) {
		Logger.info("Received {}", e);
	}

	private void updateGameScene(GameState gameState, boolean forced) {
		var newGameScene = getSceneForGameState(gameState);
		if (newGameScene == null) {
			throw new IllegalStateException("No scene found for game state " + gameState);
		}
		if (currentGameScene != newGameScene || forced) {
			if (currentGameScene != null) {
				currentGameScene.end();
			}
			newGameScene.setContext(gameController);
			newGameScene.init();
			Logger.info("Current scene changed from {} to {}", currentGameScene, newGameScene);
		}
		currentGameScene = newGameScene;
	}

	private GameScene getSceneForGameState(GameState state) {
		var game = gameController.game();
		var scenes = game.variant() == MS_PACMAN ? gameScenesMsPacMan : gameScenesPacMan;
		return switch (state) {
		case BOOT -> scenes.get(0);
		case INTRO -> scenes.get(1);
		case CREDIT -> scenes.get(2);
		case INTERMISSION -> {
			var level = game.level();
			if (level.isPresent()) {
				yield scenes.get(2 + level.get().intermissionNumber);
			}
			throw new IllegalStateException("No game level is present");
		}
		case INTERMISSION_TEST -> scenes.get(2 + game.intermissionTestNumber);
		default -> scenes.get(6);
		};
	}

	public void update() {
		handleNonPlayerKeys();
		if (currentGameScene != null) {
			currentGameScene.update();
		}
		flashMessageDisplay.update();
		EventQueue.invokeLater(this::renderScreen);
	}

	private void renderScreen() {
		if (currentGameScene == null) {
			return;
		}
		if (canvas.getBufferStrategy() == null) {
			canvas.createBufferStrategy(2);
			return;
		}
		do {
			do {
				Graphics2D g = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.scale(scaling, scaling);
				currentGameScene.render(g);
				flashMessageDisplay.render(g);
				g.dispose();
			} while (canvas.getBufferStrategy().contentsRestored());
			canvas.getBufferStrategy().show();
		} while (canvas.getBufferStrategy().contentsLost());
	}

	public void showFlashMessage(double seconds, String message, Object... args) {
		flashMessageDisplay.addMessage(seconds, message, args);
	}

	private void handleNonPlayerKeys() {
		var game = gameController.game();

		if (Keyboard.keyPressed("A")) {
			gameController.toggleAutoControlled();
			showFlashMessage(1, "Autopilot %s", gameController.isAutoControlled() ? "on" : "off");
		}

		else if (Keyboard.keyPressed(MOD_CTRL, "D")) {
			toggleDebugDraw();
			Logger.info("UI debug mode is {}", debugDraw ? "on" : "off");
		}

		else if (Keyboard.keyPressed("E")) {
			gameController.cheatEatAllPellets();
		}

		else if (Keyboard.keyPressed("I")) {
			game.setImmune(!game.isImmune());
			showFlashMessage(1, "Player is %s", game.isImmune() ? "immune" : "vulnerable");
		}

		else if (Keyboard.keyPressed("L")) {
			if (game.isPlaying()) {
				game.setLives(game.lives() + 3);
			}
		}

		else if (Keyboard.keyPressed("N")) {
			if (game.isPlaying()) {
				gameController.changeState(GameState.LEVEL_COMPLETE);
			}
		}

		else if (Keyboard.keyPressed("Q")) {
			restartIntro();
		}

		else if (Keyboard.keyPressed(MOD_CTRL, "S")) {
			int fps = gameLoop.clock.getTargetFPS() + 10;
			gameLoop.clock.setTargetFPS(fps);
			showFlashMessage(2, "Target FPS set to %s Hz", fps);
			Logger.info("Clock frequency changed to {} Hz", gameLoop.clock.getTargetFPS());
		}

		else if (Keyboard.keyPressed(MOD_CTRL | MOD_SHIFT, "S")) {
			int fps = gameLoop.clock.getTargetFPS() - 10;
			fps = Math.max(10, fps);
			gameLoop.clock.setTargetFPS(fps);
			showFlashMessage(2, "Target FPS set to %s Hz", fps);
			Logger.info("Clock frequency changed to {} Hz", gameLoop.clock.getTargetFPS());
		}

		else if (Keyboard.keyPressed("V")) {
			gameController.selectGameVariant(game.variant().next());
		}

		else if (Keyboard.keyPressed("X")) {
			gameController.cheatKillAllEatableGhosts();
		}

		else if (Keyboard.keyPressed("Z")) {
			gameController.startCutscenesTest();
		}
	}

	private void restartIntro() {
		currentGameScene.end();
		// TODO check this
		if (gameController.state() != CREDIT && gameController.state() != INTRO) {
			gameController.game().changeCredit(-1);
		}
		gameController.restart(INTRO);
	}

	private void moveMousePointerOutOfSight() {
		try {
			Robot robot = new Robot();
			robot.mouseMove(window.getX() + 10, window.getY());
		} catch (AWTException x) {
			x.printStackTrace();
		}
	}
}