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
package de.amr.games.pacman.ui.swing.app;

import static java.awt.EventQueue.invokeLater;

import java.util.Arrays;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.event.GameEvents;
import de.amr.games.pacman.ui.swing.shell.PacController;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI;

/**
 * The Pac-Man application.
 * 
 * Command-line arguments:
 * <ul>
 * <li><code>-height</code> &lt;pixels&gt;: Height of UI in pixels (default: 576)</li>
 * <li><code>-pacman</code>: Starts the game in Pac-Man mode</li>
 * <li><code>-mspacman</code>: Starts game in Ms. Pac-Man mode</li>
 * </ul>
 * 
 * @author Armin Reichert
 */
public class PacManGameAppSwing {

	public static void main(String[] args) {
		PacManGameAppSwing app = new PacManGameAppSwing(new Options(Arrays.asList(args)));
		invokeLater(app::createAndShowUI);
	}

	private Options options;
	private GameController gameController;
	private GameLoop gameLoop = new GameLoop();

	public PacManGameAppSwing(Options options) {
		this.options = options;
		gameController = new GameController();
		gameController.selectGame(options.gameVariant);
	}

	private void createAndShowUI() {
		var ui = new PacManGameUI(gameLoop, gameController, options.height);
		ui.show();
		GameEvents.addEventListener(ui);
		gameController.setPacSteering(new PacController("Up", "Down", "Left", "Right"));
		gameLoop.action = () -> {
			gameLoop.clock.frame(gameController::update);
			ui.update();
		};
		gameLoop.start();
	}
}