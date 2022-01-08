/*
MIT License

Copyright (c) 2021 Armin Reichert

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

import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.awt.Dimension;
import java.util.List;

import de.amr.games.pacman.ui.swing.assets.PacManGameSounds;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.mspacman.MsPacManGameRendering;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_IntermissionScene1;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_IntermissionScene2;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_IntermissionScene3;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_IntroScene;

/**
 * Ms. Pac-Man game scenes.
 * 
 * @author Armin Reichert
 */
public class ScenesMsPacMan {

	public static final Dimension UNSCALED_SIZE = new Dimension(t(28), t(36));
	public static MsPacManGameRendering RENDERING = new MsPacManGameRendering();
	public static final SoundManager SOUNDS = new SoundManager(PacManGameSounds::msPacManSoundURL);
	public static final List<GameScene> SCENES = List.of(
	//@formatter:off
		new MsPacMan_IntroScene(UNSCALED_SIZE), //
		new MsPacMan_IntermissionScene1(UNSCALED_SIZE), //
		new MsPacMan_IntermissionScene2(UNSCALED_SIZE), //
		new MsPacMan_IntermissionScene3(UNSCALED_SIZE), //
		new PlayScene(UNSCALED_SIZE, RENDERING, SOUNDS)//
		//@formatter:on
	);
}