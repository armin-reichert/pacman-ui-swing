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

package de.amr.games.pacman.ui.swing.scenes.mspacman;

import static de.amr.games.pacman.lib.Globals.HTS;
import static de.amr.games.pacman.lib.Globals.TS;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * @author Armin Reichert
 */
public class MsPacManCreditScene extends GameScene {

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

		g.setFont(gss.getArcadeFont());
		g.setColor(gss.getGhostColor(GameModel.ORANGE_GHOST));
		g.drawString("PUSH START BUTTON", TS * (6), TS * (16));
		g.drawString("1 PLAYER ONLY", TS * (8), TS * (18));
		g.drawString("ADDITIONAL    AT 10000", TS * (2), TS * (25));
		BufferedImage msPacMan = SpritesheetMsPacMan.get().rhs(1, 0);
		gss.drawSpriteCenteredOverBox(g, msPacMan, TS * (13) + HTS, TS * (24) - 2);
		g.setFont(gss.getArcadeFont());
		g.setFont(gss.getArcadeFont().deriveFont(6.0f));
		g.drawString("PTS", TS * (25), TS * (25));

		gss.drawCopyright(g, TS * (6), TS * (28));
		gss.drawCredit(g, game.credit());
	}

}