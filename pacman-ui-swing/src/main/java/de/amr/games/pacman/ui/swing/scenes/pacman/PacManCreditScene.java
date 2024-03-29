/*
MIT License

Copyright (c) 2021-2023 Armin Reichert

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

import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * @author Armin Reichert
 */
public class PacManCreditScene extends GameScene {

	@Override
	public void update() {
		if (Keyboard.keyPressed("5")) {
			gameController.addCredit();
		} else if (Keyboard.keyPressed("1")) {
			gameController.startPlaying();
		}
	}

	@Override
	public void render(Graphics2D g) {
		gss.drawScores(g, game, true);
		var arcade8 = gss.getArcadeFont();
		var arcade6 = arcade8.deriveFont(6f);
		gss.drawText(g, "PUSH START BUTTON", gss.getGhostColor(GameModel.ORANGE_GHOST), arcade8, TS * (6), TS * (17));
		gss.drawText(g, "1 PLAYER ONLY", gss.getGhostColor(GameModel.CYAN_GHOST), arcade8, TS * (8), TS * (21));
		gss.drawText(g, "1 PLAYER ONLY", gss.getGhostColor(GameModel.CYAN_GHOST), arcade8, TS * (8), TS * (21));
		gss.drawText(g, "BONUS PAC-MAN FOR 10000", new Color(255, 184, 174), arcade8, TS * (1), TS * (25));
		gss.drawText(g, "PTS", new Color(255, 184, 174), arcade6, TS * (25), TS * (25));
		gss.drawCopyright(g, TS * (4), TS * (29));
		gss.drawCredit(g, game.credit());
		gss.drawLevelCounter(g, game.levelCounter());
	}
}