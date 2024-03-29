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
import de.amr.games.pacman.controller.MsPacManIntermission3;
import de.amr.games.pacman.lib.anim.AnimationMap;
import de.amr.games.pacman.model.GameModel;
import de.amr.games.pacman.ui.swing.entity.mspacman.Stork2D;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intermission scene 3: "Junior".
 * 
 * <p>
 * Pac-Man and Ms. Pac-Man gradually wait for a stork, who flies overhead with a little blue bundle. The stork drops the
 * bundle, which falls to the ground in front of Pac-Man and Ms. Pac-Man, and finally opens up to reveal a tiny Pac-Man.
 * (Played after rounds 9, 13, and 17)
 * 
 * @author Armin Reichert
 */
public class MsPacManIntermissionScene3 extends GameScene {

	private MsPacManIntermission3 sceneController;
	private MsPacManIntermission3.Context ctx;
	private Stork2D stork2D;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new MsPacManIntermission3(gameController);
		ctx = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.restart(MsPacManIntermission3.State.FLAP);
		ctx.clapperboard.setAnimation(SpritesheetMsPacMan.get().createClapperboardAnimation());
		ctx.msPacMan.setAnimations(new PacAnimations(ctx.msPacMan, gss));
		ctx.msPacMan.animations().ifPresent(AnimationMap::ensureRunning);
		ctx.pacMan.setAnimations(new PacAnimations(ctx.pacMan, gss));
		var husbandMunching = SpritesheetMsPacMan.get().createHusbandMunchingAnimations(ctx.pacMan);
		ctx.pacMan.animations().ifPresent(anims -> anims.put(GameModel.AK_PAC_MUNCHING, husbandMunching));
		ctx.pacMan.animations().ifPresent(AnimationMap::ensureRunning);
		stork2D = new Stork2D(ctx.stork, gss);
		stork2D.animation.restart();
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		var ssmp = ((SpritesheetMsPacMan) gss);
		ssmp.drawClapperboard(g, ctx.clapperboard);
		gss.drawPac(g, ctx.msPacMan);
		gss.drawPac(g, ctx.pacMan);
		stork2D.render(g);
		gss.drawEntity(g, ctx.bag, ctx.bagOpen ? ssmp.getJunior() : ssmp.getBlueBag());
	}
}