/*
MIT License

Copyright (c) 2022 Armin Reichert

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

import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.world.World;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.rendering.mspacman.Rendering2D_MsPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * @author Armin Reichert
 */
public class MsPacMan_CreditScene extends GameScene {

	public MsPacMan_CreditScene(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
	}

	@Override
	public void update() {
		if (Keyboard.keyPressed("5")) {
			SoundManager.get().play(GameSound.CREDIT);
			gameController.addCredit();
			return;
		} else if (Keyboard.keyPressed("Space") || Keyboard.keyPressed("1")) {
			gameController.requestGame();
			return;
		}
	}

	@Override
	public void render(Graphics2D g) {

		r2D.drawScore(g, game, true);

		g.setFont(r2D.getArcadeFont());
		g.setColor(r2D.getGhostColor(Ghost.ORANGE_GHOST));
		g.drawString("PUSH START BUTTON", t(6), t(16));
		g.drawString("1 PLAYER ONLY", t(8), t(18));
		g.drawString("ADDITIONAL    AT 10000", t(2), t(25));
		BufferedImage msPacMan = Rendering2D_MsPacMan.get().rhs(1, 0);
		r2D.drawSpriteCenteredOverBBox(g, msPacMan, t(13) + World.HTS, t(24) - 2);
		g.setFont(r2D.getArcadeFont());
		g.setFont(r2D.getArcadeFont().deriveFont(6.0f));
		g.drawString("PTS", t(25), t(25));

		r2D.drawCopyright(g, t(4), t(28));
		r2D.drawCredit(g, gameController.credit());
	}

}