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
package de.amr.games.pacman.ui.swing.entity.common;

import java.awt.Graphics2D;

import de.amr.games.pacman.lib.animation.SimpleThingAnimation;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Bonus symbol. In Pac-Man game, it resides at a fixed position. In Ms. Pac-Man, it appears at some portal and wanders
 * the maze before it exits the maze through some portal on the other side. Changes to bonus value when consumed.
 * 
 * @author Armin Reichert
 */
public class Bonus2D extends GameEntity2D {

	private SimpleThingAnimation<Integer> jumpAnimation;

	public Bonus2D(GameModel game, boolean jumping) {
		super(game);
		if (jumping) {
			jumpAnimation = new SimpleThingAnimation<>(-2, 2);
			jumpAnimation.frameDuration(10);
			jumpAnimation.repeatForever();
		}
	}

	public void startJumping() {
		if (jumpAnimation != null) {
			jumpAnimation.restart();
		}
	}

	public void stopJumping() {
		if (jumpAnimation != null) {
			jumpAnimation.stop();
		}
	}

	@Override
	public void render(Graphics2D g, Rendering2D r2D) {
		var bonus = game.bonus();
		var sprite = r2D.getBonusSymbolSprite(bonus.symbol());
		int dy = jumpAnimation != null && jumpAnimation.isRunning() ? jumpAnimation.animate() : 0;
		g.translate(0, dy);
		r2D.drawSpriteCenteredOverBox(g, sprite, bonus.entity().position.x, bonus.entity().position.y);
		g.translate(0, -dy);
	}
}