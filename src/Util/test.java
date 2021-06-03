package Util;

import Entity.SongTable;
import com.tulskiy.musique.audio.player.Player;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import myorg.jaudiotagger.audio.exceptions.CannotReadException;
import myorg.jaudiotagger.audio.exceptions.CannotWriteException;
import myorg.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import myorg.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import myorg.jaudiotagger.audio.mp3.MP3AudioHeader;
import myorg.jaudiotagger.audio.mp3.MP3File;
import myorg.jaudiotagger.tag.FieldKey;
import myorg.jaudiotagger.tag.Tag;
import myorg.jaudiotagger.tag.TagException;
import myorg.jaudiotagger.tag.TagField;
import myorg.jaudiotagger.tag.id3.AbstractID3v2Tag;
import myorg.jaudiotagger.tag.id3.ID3v1Tag;
import myorg.jaudiotagger.tag.id3.ID3v24Tag;
import sun.nio.ch.FileKey;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static myorg.jaudiotagger.tag.FieldKey.*;

public class test {

    public static void main(String[] args){
        ArrayList<SongTable> tableList = LocalSongXmlUtil.getSongTableList();

        LrcUtil.writeLrc(tableList.get(0),"E:\\test24");



    }



}
