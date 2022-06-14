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

package de.amr.games.pacman.ui.swing.sound;

import static de.amr.games.pacman.lib.Logging.log;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import de.amr.games.pacman.model.common.GameSound;
import de.amr.games.pacman.model.common.GameSounds;

/**
 * @author Armin Reichert
 *
 */
public class AbstractGameSounds implements GameSounds {

	protected final Map<GameSound, Clip> clips = new EnumMap<>(GameSound.class);
	protected boolean silent;
	protected boolean muted;

	@Override
	public void setSilent(boolean silent) {
		this.silent = silent;
		if (silent) {
			stopAll();
		}
	}

	@Override
	public boolean isMuted() {
		return muted;
	}

	@Override
	public void setMuted(boolean muted) {
		this.muted = muted;
		if (muted) {
			stopAll();
		}
	}

	private Clip createAndOpenClip(URL url) {
		try (AudioInputStream as = AudioSystem.getAudioInputStream(url)) {
			Clip clip = AudioSystem.getClip();
			clip.open(as);
			return clip;
		} catch (Exception x) {
			throw new RuntimeException("Error opening audio clip from URL " + url, x);
		}
	}

	protected void put(Map<GameSound, Clip> map, GameSound sound, String path) {
		URL url = getClass().getResource(path);
		if (url == null) {
			throw new RuntimeException("Sound resource does not exist: " + path);
		}
		Clip clip = createAndOpenClip(url);
		map.put(sound, clip);
	}

	protected void playClip(Clip clip) {
		if (!silent && !muted) {
			clip.start();
		}
	}

	protected void playClip(Clip clip, int repetitions) {
		if (!silent && !muted) {
			clip.loop(repetitions);
		}
	}

	protected Clip getClip(GameSound sound) {
		if (!clips.containsKey(sound)) {
			throw new RuntimeException("No clip found for " + sound);
		}
		return clips.get(sound);
	}

	public boolean isPlaying(GameSound sound) {
		return getClip(sound).isRunning();
	}

	@Override
	public void ensurePlaying(GameSound sound) {
		if (!isPlaying(sound)) {
			play(sound);
		}
	}

	@Override
	public void play(GameSound sound) {
		loop(sound, 1);
	}

	@Override
	public void ensureLoop(GameSound sound, int repetitions) {
		if (!isPlaying(sound)) {
			loop(sound, repetitions);
		}
	}

	@Override
	public void loop(GameSound sound, int repetitions) {
		Clip clip = getClip(sound);
		playClip(clip, repetitions);
	}

	@Override
	public void stop(GameSound sound) {
		getClip(sound).stop();
	}

	@Override
	public void stopAll() {
		for (var clip : clips.values()) {
			clip.stop();
		}
	}

	// -----------------

	public void startSiren(int sirenIndex) {
		stopSirens();
		var siren = switch (sirenIndex) {
		case 0 -> GameSound.SIREN_1;
		case 1 -> GameSound.SIREN_2;
		case 2 -> GameSound.SIREN_3;
		case 3 -> GameSound.SIREN_4;
		default -> throw new IllegalArgumentException("Illegal siren index: " + sirenIndex);
		};
//		getClip(siren).setVolume(0.2);
		loop(siren, Clip.LOOP_CONTINUOUSLY);
		log("Siren %s started", siren);
	}

	public Stream<GameSound> sirens() {
		return Stream.of(GameSound.SIREN_1, GameSound.SIREN_2, GameSound.SIREN_3, GameSound.SIREN_4);
	}

	@Override
	public void ensureSirenStarted(int sirenIndex) {
		if (!sirens().anyMatch(this::isPlaying)) {
			startSiren(sirenIndex);
		}
	}

	@Override
	public void stopSirens() {
		sirens().forEach(siren -> {
			if (isPlaying(siren)) {
				getClip(siren).stop();
				log("Siren %s stopped", siren);
			}
		});
	}
}