/*
 * Copyright (c) 2008, 2009, 2010, 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tulskiy.musique.audio.player;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.tulskiy.musique.audio.player.Actor.Message;
import com.tulskiy.musique.audio.player.io.AudioOutput;
import com.tulskiy.musique.audio.player.io.Buffer;
import com.tulskiy.musique.model.Track;

/**
 * Author: Denis Tulskiy Date: Jan 21, 2010
 */
public class Player {
	public final Logger logger = Logger.getLogger(getClass().getName());
	private static final int BUFFER_SIZE = (int) Math.pow(2, 18);

	private PlayingThread playingThread;
	private BufferingThread bufferingThread;
	private ArrayList<PlayerListener> listeners = new ArrayList<PlayerListener>();

	public Player() {
		Buffer buffer = new Buffer(BUFFER_SIZE);
		playingThread = new PlayingThread(this, buffer);
		Thread t1 = new Thread(playingThread, "Playing Thread");
		t1.setPriority(Thread.MAX_PRIORITY);
		t1.start();
		bufferingThread = new BufferingThread(buffer, playingThread);
		new Thread(bufferingThread, "Buffer Thread").start();
	}

	public void open(Track track) {
		bufferingThread.send(Message.OPEN, track);
	}

	public void play() {
		if (!isPaused()) {
			Track track = getTrack();
			if (track == null) {
				stop();
			}
		}
	}

	public void pause() {
		playingThread.send(Message.PAUSE);
	}

	public void seek(long sample) {
		bufferingThread.send(Message.SEEK, sample);
	}

	public void stop() {
		bufferingThread.send(Message.STOP);
	}

	public AudioOutput getAudioOutput() {
		return playingThread.getOutput();
	}

	public void addListener(PlayerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(PlayerListener listener) {
		listeners.remove(listener);
	}

	public long getCurrentSample() {
		return playingThread.getCurrentSample();
	}

	public double getCurrentMillis() {
		return this.playingThread.getCurrentMillis();
	}

	public Track getTrack() {
		return playingThread.getCurrentTrack();
	}

	public double getPlaybackTime() {
		return playingThread.getPlaybackTime();
	}

	public boolean isPlaying() {
		return playingThread.isActive() && getTrack() != null;
	}

	public boolean isPaused() {
		return !isPlaying() && !isStopped();
	}

	public boolean isStopped() {
		return !bufferingThread.isActive();
	}

	public void setStopAfterCurrent(boolean stopAfterCurrent) {
		bufferingThread.setStopAfterCurrent(stopAfterCurrent);
	}

	synchronized void fireEvent(PlayerEvent.PlayerEventCode event) {
		logger.fine("Player Event: " + event);
		PlayerEvent e = new PlayerEvent(event);
		for (PlayerListener listener : listeners) {
			listener.onEvent(e);
		}
	}
}
