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

package com.tulskiy.musique.audio.formats.mp4;

import javax.sound.sampled.AudioFormat;

import com.tulskiy.musique.audio.Decoder;
import com.tulskiy.musique.model.Track;

/**
 * Author: Denis Tulskiy
 * Date: 4/2/11
 */
public class MP4Demuxer implements Decoder {
    private final Decoder alacDecoder = new ALACDecoder();
    private final Decoder aacDecoder = new MP4Decoder();

    private Decoder decoder;

    public boolean open(Track track) {
        String codec = track.getTrackData().getCodec();
        if ("AAC".equals(codec)) {
            decoder = aacDecoder;
        } else if ("Apple Lossless".equals(codec)) {
            decoder = alacDecoder;
        } else {
            return false;
        }
        return decoder.open(track);
    }

    public AudioFormat getAudioFormat() {
        return decoder.getAudioFormat();
    }

    public void seekSample(long sample) {
        decoder.seekSample(sample);
    }

    public int decode(byte[] buf) {
        return decoder.decode(buf);
    }

    public void close() {
        decoder.close();
    }
}
