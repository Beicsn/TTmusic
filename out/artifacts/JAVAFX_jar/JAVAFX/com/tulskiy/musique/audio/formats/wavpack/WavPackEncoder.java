/*
 * Copyright (c) 2008, 2009, 2010 Denis Tulskiy
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

package com.tulskiy.musique.audio.formats.wavpack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.security.auth.login.Configuration;
import javax.sound.sampled.AudioFormat;

import com.tulskiy.musique.audio.Encoder;
import com.tulskiy.musique.util.Util;
import com.wavpack.encoder.Defines;
import com.wavpack.encoder.WavPackUtils;
import com.wavpack.encoder.WavpackConfig;
import com.wavpack.encoder.WavpackContext;

/**
 * Author: Denis Tulskiy
 * Date: Jul 25, 2010
 */
public class WavPackEncoder implements Encoder {
    private WavpackContext wpc;
    private long[] sample_buffer;
    private int totalSamples;

    public boolean open(File outputFile, AudioFormat fmt) {
        wpc = new WavpackContext();
        WavpackConfig config = wpc.config;
        try {
            wpc.outfile = new RandomAccessFile(outputFile, "rw");
            wpc.outfile.setLength(0);

            config.bytes_per_sample = fmt.getSampleSizeInBits() / 8;
            config.bits_per_sample = fmt.getSampleSizeInBits();
            config.num_channels = fmt.getChannels();
            config.sample_rate = (long) fmt.getSampleRate();

            WavPackUtils.WavpackSetConfiguration(wpc, config, -1);
            WavPackUtils.WavpackPackInit(wpc);
            sample_buffer = new long[(Defines.INPUT_SAMPLES * 4 * fmt.getChannels())];

            totalSamples = 0;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void encode(byte[] buf, int len) {
        int loopBps = WavPackUtils.WavpackGetBytesPerSample(wpc);
        int sample_count = len / loopBps / 2;
        totalSamples += sample_count;
        int cnt = sample_count * WavPackUtils.WavpackGetNumChannels(wpc);

        if (loopBps == 1) {
            int intermalCount = 0;

            while (cnt > 0) {
                sample_buffer[intermalCount] = (buf[intermalCount] & 0xff) - 128;
                intermalCount++;
                cnt--;
            }
        } else if (loopBps == 2) {
            int dcounter = 0;
            int scounter = 0;

            while (cnt > 0) {
                sample_buffer[dcounter] = (buf[scounter] & 0xff) | (buf[scounter + 1] << 8);
                scounter = scounter + 2;
                dcounter++;
                cnt--;
            }
        } else if (loopBps == 3) {
            int dcounter = 0;
            int scounter = 0;

            while (cnt > 0) {
                sample_buffer[dcounter] = (buf[scounter] & 0xff) |
                                          ((buf[scounter + 1] & 0xff) << 8) | (buf[scounter + 2] << 16);
                scounter = scounter + 3;
                dcounter++;
                cnt--;
            }
        }
        wpc.byte_idx = 0;
        WavPackUtils.WavpackPackSamples(wpc, sample_buffer, sample_count);
    }

    public void close() {
        try {
            WavPackUtils.WavpackFlushSamples(wpc);
            fixLength(wpc.outfile);
            wpc.outfile.close();

            if (wpc.correction_outfile != null) {
                fixLength(wpc.correction_outfile);
                wpc.correction_outfile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fixLength(RandomAccessFile file) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(totalSamples);
        file.seek(12);
        file.write(buf.array());
    }

}
