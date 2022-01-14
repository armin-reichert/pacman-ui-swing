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
package de.amr.games.pacman.ui.swing.shell;

import de.amr.games.pacman.controller.PlayerControl;
import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.model.common.Pac;

/**
 * Controls the player using the keyboard.
 * 
 * @author Armin Reichert
 */
public class ManualPlayerControl implements PlayerControl {

	private final Keyboard keyboard;
	private final String upKey, downKey, leftKey, rightKey;

	public ManualPlayerControl(Keyboard keyboard, String upKey, String downKey, String leftKey, String rightKey) {
		this.keyboard = keyboard;
		this.upKey = upKey;
		this.downKey = downKey;
		this.leftKey = leftKey;
		this.rightKey = rightKey;
	}

	@Override
	public void steer(Pac player) {
		if (keyboard.keyPressed(upKey)) {
			player.setWishDir(Direction.UP);
		}
		if (keyboard.keyPressed(downKey)) {
			player.setWishDir(Direction.DOWN);
		}
		if (keyboard.keyPressed(leftKey)) {
			player.setWishDir(Direction.LEFT);
		}
		if (keyboard.keyPressed(rightKey)) {
			player.setWishDir(Direction.RIGHT);
		}
	}
}