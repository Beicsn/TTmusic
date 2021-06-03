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

package com.tulskiy.musique.audio.formats.tta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import javax.sound.sampled.AudioFormat;

import com.tulskiy.musique.audio.Decoder;
import com.tulskiy.musique.model.Track;
import com.tulskiy.tta.TTA_Decoder;
import com.tulskiy.tta.TTA_info;

/**
 * Author: Denis Tulskiy
 * Date: 6/4/11
 */
public class TTADecoder implements Decoder {
    private TTA_Decoder decoder;
    private AudioFormat fmt;
    private TTA_info info;
    private Track track;

    public boolean open(Track track) {
        this.track = track;
        try {
            decoder = new TTA_Decoder(new FileInputStream(track.getTrackData().getFile()));

            info = decoder.init_get_info(0);
            fmt = new AudioFormat(info.sps, info.bps, info.nch, true, false);

            return true;
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "File not found", e);
        }
        return false;
    }

    public AudioFormat getAudioFormat() {
        return fmt;
    }

    public void seekSample(long sample) {
        decoder.set_position((int) sample);
    }

    public int decode(byte[] buf) {
        try {
            track.getTrackData().setBitrate(decoder.get_current_bitrate());
            return decoder.process_stream(buf);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Decoder Error", e);
            return -1;
        }
    }

    public void close() {
        track.getTrackData().setBitrate(info.bitrate);
        if (decoder != null)
            try {
                decoder.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not close decoder", e);
            }
        decoder = null;
        info = null;
    }
}
