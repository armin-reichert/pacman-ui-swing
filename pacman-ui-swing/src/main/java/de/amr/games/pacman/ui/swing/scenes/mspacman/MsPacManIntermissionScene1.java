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
package de.amr.games.pacman.ui.swing.scenes.mspacman;

import java.awt.Graphics2D;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.MsPacManIntermission1;
import de.amr.games.pacman.lib.anim.AnimationMap;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.ui.swing.entity.mspacman.Heart2D;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intermission scene 1: "They meet".
 * <p>
 * Pac-Man leads Inky and Ms. Pac-Man leads Pinky. Soon, the two Pac-Men are about to collide, they quickly move
 * upwards, causing Inky and Pinky to collide and vanish. Finally, Pac-Man and Ms. Pac-Man face each other at the top of
 * the screen and a big pink heart appears above them. (Played after round 2)
 * 
 * @author Armin Reichert
 */
public class MsPacManIntermissionScene1 extends GameScene {

	private MsPacManIntermission1 sceneController;
	private MsPacManIntermission1.Context ctx;
	private Heart2D heart2D;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new MsPacManIntermission1(gameController);
		ctx = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.restart(MsPacManIntermission1.State.FLAP);
		ctx.clapperboard.setAnimation(SpritesheetMsPacMan.get().createClapperboardAnimation());
		ctx.msPac.setAnimations(new PacAnimations(ctx.msPac, gss));
		ctx.msPac.animations().ifPresent(AnimationMap::ensureRunning);
		ctx.pacMan.setAnimations(new PacAnimations(ctx.pacMan, gss));
		var husbandMunching = SpritesheetMsPacMan.get().createHusbandMunchingAnimations(ctx.pacMan);
		ctx.pacMan.animations().ifPresent(anims -> anims.put(GameModel.AK_PAC_MUNCHING, husbandMunching));
		ctx.pacMan.animations().ifPresent(anims -> anims.selectedAnimation().get().ensureRunning());
		ctx.inky.setAnimations(new GhostAnimations(ctx.inky, gss));
		ctx.pinky.setAnimations(new GhostAnimations(ctx.pinky, gss));
		heart2D = new Heart2D(ctx.heart);
		heart2D.setImage(SpritesheetMsPacMan.get().getHeart());
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		((SpritesheetMsPacMan) gss).drawClapperboard(g, ctx.clapperboard);
		gss.drawPac(g, ctx.msPac);
		gss.drawPac(g, ctx.pacMan);
		gss.drawGhost(g, ctx.inky);
		gss.drawGhost(g, ctx.pinky);
		heart2D.render(g);
	}
}