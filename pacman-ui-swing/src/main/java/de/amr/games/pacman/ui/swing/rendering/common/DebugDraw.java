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
package de.amr.games.pacman.ui.swing.rendering.common;

import static de.amr.games.pacman.lib.Globals.HTS;
import static de.amr.games.pacman.lib.Globals.TS;
import static de.amr.games.pacman.lib.timer.TickTimer.ticksToString;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.lib.math.Vector2f;
import de.amr.games.pacman.lib.math.Vector2i;
import de.amr.games.pacman.model.common.world.World;

/**
 * @author Armin Reichert
 */
public class DebugDraw {

	private DebugDraw() {
	}

	public static void drawPlaySceneDebugInfo(Graphics2D g, GameController controller) {
		final Color[] ghostColors = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };
		var game = controller.game();
		var state = controller.state();
		game.level().ifPresent(level -> {
			var huntingTimer = level.huntingTimer();
			String stateText;
			if (state == GameState.HUNTING && level.scatterPhase().isPresent()) {
				var ticks = huntingTimer.remaining();
				stateText = "Scattering phase %d Remaining: %s".formatted(level.scatterPhase().getAsInt(),
						ticksToString(ticks));
			} else if (state == GameState.HUNTING && level.chasingPhase().isPresent()) {
				var ticks = huntingTimer.remaining();
				stateText = "Chasing phase %d Remaining: %s".formatted(level.chasingPhase().getAsInt(), ticksToString(ticks));
			} else {
				var ticks = state.timer().tick();
				stateText = "State %s Running: %s".formatted(state, ticksToString(ticks));
			}
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.PLAIN, 6));
			g.drawString(stateText, TS * 1, TS * 3);
			level.ghosts().forEach(ghost -> {
				g.setColor(Color.WHITE);
				g.drawRect((int) ghost.position().x(), (int) ghost.position().y(), TS, TS);
				ghost.targetTile().ifPresent(targetTile -> {
					Color c = ghostColors[ghost.id()];
					g.setColor(c);
					g.fillRect(TS * (targetTile.x()) + HTS / 2, TS * (targetTile.y()) + HTS / 2, HTS, HTS);
					g.setStroke(new BasicStroke(0.5f));
					Vector2f targetPosition = targetTile.scaled(TS).plus(HTS, HTS).toFloatVec();
					g.drawLine((int) ghost.position().x(), (int) ghost.position().y(), (int) targetPosition.x(),
							(int) targetPosition.y());
				});
			});
			level.pac().targetTile().ifPresent(targetTile -> {
				g.setColor(new Color(255, 255, 0, 200));
				g.fillRect(TS * (targetTile.x()), TS * (targetTile.y()), TS, TS);
			});
		});
	}

	public static void drawMazeStructure(Graphics2D g, World world) {
		Color dark = new Color(80, 80, 80);
		for (int x = 0; x < world.numCols(); ++x) {
			for (int y = 0; y < world.numRows(); ++y) {
				Vector2i tile = new Vector2i(x, y);
				if (world.isIntersection(tile)) {
					g.setColor(dark);
					g.drawOval(TS * (x), TS * (y), TS, TS);
				}
				if (world.house().contains(tile)) {
					g.setColor(new Color(100, 100, 100, 100));
					g.fillRect(TS * (x) + 1, TS * (y) + 1, TS - 2, TS - 2);
				}
			}
		}
	}
}