package service;

import Entity.Song;
import Entity.SongTable;
import Entity.Tag;
import Util.SongUtil;
import Util.TagInfoUtil;
import myorg.jaudiotagger.audio.exceptions.CannotReadException;
import myorg.jaudiotagger.audio.exceptions.CannotWriteException;
import myorg.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import myorg.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import myorg.jaudiotagger.audio.mp3.MP3AudioHeader;
import myorg.jaudiotagger.audio.mp3.MP3File;
import myorg.jaudiotagger.tag.TagException;
import myorg.jaudiotagger.tag.TagField;
import myorg.jaudiotagger.tag.id3.AbstractID3v2Tag;
import myorg.jaudiotagger.tag.id3.ID3v1Tag;

import java.io.*;

import static myorg.jaudiotagger.tag.FieldKey.TITLE;

public class SongOperate {
    //从本地磁盘中删除音乐
    public static void deleteSongForever(SongTable songTable){
        String path = songTable.getPath();
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }

    // 添加一个song，并返回实体
    public static Song addSong(String path, int songMenuId) throws RuntimeException {
        Song song = new Song();
        //歌单ID 为1，即是我喜欢的音乐
        song.setPath(path);
        if(songMenuId == 1){
            song.setYesLike();
        }else{
             song.setNoLike();
        }
        File file = new File(path);

        song.setSize(SongUtil.getSizeToMb(file.length()));
        Tag tag;
        //按照不同格式的音频文件获取音频信息
        if(path.endsWith(".mp3")){
            tag = TagInfoUtil.Mp3InfoRead(path);

        }else if(path.endsWith(".flac")){
            tag = TagInfoUtil.FlacInfoRead(path);
        }else{
            tag = TagInfoUtil.getOtherTag(path);
        }
         song.setTag(tag);
        return song;
    }
    public static void cutSong(File oldSong,double startTime,double endTime,File newSong) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        if(!newSong.exists()){
            newSong.createNewFile();
        }

        MP3File mp3File = new MP3File(oldSong);//封装好的类



        MP3AudioHeader header = mp3File.getMP3AudioHeader();



        int bitRate = Integer.parseInt(header.getBitRate());
        System.out.println("彼特率是+"+bitRate);
        BufferedInputStream bis1 = null;

        BufferedOutputStream bos = null;

        //第一首歌剪切部分起始字节
       long start1= bitRate * Math.round(startTime/1000) *1024 /8  ;;//320kbps（比特率）*58s*1024/8=2375680 比特率可以查看音频属性获知
        long end1=bitRate *  Math.round(endTime/1000 ) * 1024 /8 ;//320kbps*120s*1024/8=4915200

        AbstractID3v2Tag  id3v2Tag  = mp3File.getID3v2Tag();
        ID3v1Tag id3v1Tag = mp3File.getID3v1Tag();
        System.out.println(mp3File.getID3v2Tag());
        int size = mp3File.getID3v2Tag().getSize();
        int tatol1 = 0;
        System.out.println(size);
        try {
            //两个输入流
            bis1 = new BufferedInputStream(new FileInputStream(oldSong));

            //缓冲字节输出流（true表示可以在流的后面追加数据，而不是覆盖！！//false为覆盖）
            bos = new BufferedOutputStream(new FileOutputStream(newSong,false));

            //第一首歌剪切、写入
            byte[] b1= new byte[512];
            int len1 = 0;
            while((len1 = bis1.read(b1))!=-1){
                tatol1+=len1;   //累积tatol
                if(tatol1>size&&tatol1<start1 ){
                    continue;
                }
                if(tatol1 >end1 ){
                    continue;
                }
                bos.write(b1);


            }
                 bos.flush();
            System.out.println("歌曲剪切完成！");
            MP3File newSongFile = new MP3File(newSong);//封装好的类



            if(id3v2Tag!=null)
            newSongFile.setID3v2Tag(id3v2Tag);
//            if(id3v1Tag!=null)
//            newSongFile.setID3v1Tag(id3v1Tag);
            newSongFile.commit();
            System.out.println(newSongFile.getTag());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        } finally{
            try {//切记要关闭流！！
                if(bis1!=null) bis1.close();
                if(bos!=null) bos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
