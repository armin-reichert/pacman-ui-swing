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
package de.amr.games.pacman.ui.swing.scenes.common;

import static de.amr.games.pacman.lib.Globals.TS;

import java.awt.Graphics2D;
import java.awt.Image;

import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.model.world.World;
import de.amr.games.pacman.ui.swing.rendering.common.DebugDraw;
import de.amr.games.pacman.ui.swing.shell.Keyboard;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	@Override
	public void update() {
		if (Keyboard.keyPressed("5")) {
			gameController.addCredit();
		}
	}

	@Override
	public void render(Graphics2D g) {
		game.level().ifPresent(level -> {
			drawMaze(g, level.world(), gss.mazeNumber(level.number()));
			level.bonusManagement().getBonus().ifPresent(bonus -> gss.drawBonus(g, bonus));
			gss.drawGameState(g, game, game.hasCredit() ? gameController.state() : GameState.GAME_OVER);
			gss.drawPac(g, level.pac());
			gss.drawGhost(g, level.ghost(GameModel.ORANGE_GHOST));
			gss.drawGhost(g, level.ghost(GameModel.CYAN_GHOST));
			gss.drawGhost(g, level.ghost(GameModel.PINK_GHOST));
			gss.drawGhost(g, level.ghost(GameModel.RED_GHOST));
			if (PacManGameUI.isDebugDraw()) {
				DebugDraw.drawPlaySceneDebugInfo(g, gameController);
			}
		});
		boolean highScoreOnly = !game.isPlaying() && gameController.state() != GameState.READY
				&& gameController.state() != GameState.GAME_OVER;
		gss.drawScores(g, game, highScoreOnly);
		if (game.hasCredit()) {
			gss.drawLivesCounter(g, game);
		} else {
			gss.drawCredit(g, game.credit());
		}
		gss.drawLevelCounter(g, game.levelCounter());
	}

	private void drawMaze(Graphics2D g, World world, int mazeNumber) {
		var flashing = world.animation(GameModel.AK_MAZE_FLASHING);
		if (flashing.isPresent() && flashing.get().isRunning()) {
			g.drawImage((Image) flashing.get().frame(), 0, TS * (3), null);
		} else {
			gss.drawFullMaze(g, mazeNumber, 0, TS * (3));
			var energizerPulse = world.animation(GameModel.AK_MAZE_ENERGIZER_BLINKING);
			if (energizerPulse.isPresent()) {
				boolean dark = !(boolean) energizerPulse.get().frame();
				gss.drawDarkTiles(g, world.tiles(),
						tile -> world.containsEatenFood(tile) || world.isEnergizerTile(tile) && dark);
			} else {
				gss.drawDarkTiles(g, world.tiles(), world::containsEatenFood);
			}
		}
		if (PacManGameUI.isDebugDraw()) {
			DebugDraw.drawMazeStructure(g, world);
		}
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		if (e.newGameState == GameState.CHANGING_TO_NEXT_LEVEL) {
			gameController.terminateCurrentState(); // TODO check if needed
		}
	}
}