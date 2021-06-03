package Util;



import Entity.SongTable;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import Entity.Song;
public class LocalSongXmlUtil {
    private static String path = XmlUtil.class.getResource("/").getPath();
    private static String fileName = "LocalSong.xml";
    //初始化数据文件
    static {

        createSongListDoc();;
    }
    public static ArrayList<SongTable> getSongTableList(){
        Document songListDoc = getSongListDoc();
        Element rootElement = songListDoc.getRootElement();
        ArrayList<SongTable> songTables = new ArrayList<>();
        List<Element> songs = rootElement.elements("song");
         for(Element el : songs){
             String isLike = el.elementText("isLike");
             String path = el.elementText("path");
             String size = el.elementText("size");
             Element tag = el.element("tag");
             String album = tag.elementText("album");
             String artist = tag.elementText("artist");
             String songName = tag.elementText("songName");
             String time = tag.elementText("time");
             SongTable songTable = new SongTable(isLike, songName, artist, album, time, size, path);
                songTables.add(songTable);
         }
         return songTables;
    }
    public static ArrayList<SongTable> getSongTableList(HashMap<String,Integer> map){
        Document songListDoc = getSongListDoc();
        Element rootElement = songListDoc.getRootElement();
        ArrayList<SongTable> songTables = new ArrayList<>();
        List<Element> songs = rootElement.elements("song");
        int i =0;
        for(Element el : songs){
            String isLike = el.elementText("isLike");
            String path = el.elementText("path");
            String size = el.elementText("size");
            Element tag = el.element("tag");
            String album = tag.elementText("album");
            String artist = tag.elementText("artist");
            String songName = tag.elementText("songName");
            String time = tag.elementText("time");
            SongTable songTable = new SongTable(isLike, songName, artist, album, time, size, path);
            map.put(path,i++);
            songTables.add(songTable);
        }
        return songTables;
    }
    public static Document getSongListDoc(){

        File songListXml = new File(path + fileName);
        if(!songListXml.exists()){
            createSongListDoc();
        }
        Document read = null;
        try {
            read  = new SAXReader().read(songListXml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return  read;

    }
    public static  void createSongListDoc(){
        //获取程序当前路径


        File file = new File(path + fileName);
        if (!file.exists()) {

            try {
                file.createNewFile();
                //创建document对象
                Document document = DocumentHelper.createDocument();
                Element root = document.addElement("localMusic");




                writeSongListDoc(document);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public static boolean writeSongToSongList(Song song){
//        //如果在本地歌单中找到了该歌曲，则写入失败
//            if(findSongFromLocalSong(song)) return false;
//            else{
//                Document songListDoc = getSongListDoc();
//                Element rootElement = songListDoc.getRootElement();
//                Element songElement = rootElement.addElement("song");
//                songElement.addElement("path").addText(song.getPath());
//                songElement.addElement("length").addText(song.getLength());
//                songElement.addElement("isLike").addText(song.getIsLike());
//                Element songTag = songElement.addElement("tag");
//                songTag.addElement("alum").addText(song.getTag().getAlbum());
//                songTag.addElement("artist").addText(song.getTag().getArtist());
//                songTag.addElement("songName").addText(song.getTag().getSongName());
//                songTag.addElement("time").addText(song.getTag().getTime());
//                writeSongListDoc(songListDoc);
//                return true;
//            }
//    }
//    public static int writeSongsToSongList(ArrayList)
//    public static void writeSongListBySong(Song song){
//        Document songListDoc = getSongListDoc();
//        XMLWriter xmlWriter =null;
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(new File(path+fileName));
//            //使用漂亮格式
//            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
//            xmlWriter = new XMLWriter(fileOutputStream,outputFormat);
//            xmlWriter.write(document);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                xmlWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
    public static void  writeSongsToSongList(ArrayList<SongTable>songTables,int songMenuId){
        Document doc = getSongListDoc();
        Element rootElement = doc.getRootElement();
        for(SongTable song : songTables){
         if (!findSongFromLocalSong(song)){
             Element songElement = rootElement.addElement("song");
             songElement.addElement("path").addText(song.getPath());
             songElement.addElement("size").addText(song.getSongSize());
             songElement.addElement("isLike").addText(song.getLike());
             Element songTag = songElement.addElement("tag");
             songTag.addElement("album").addText(song.getAlbumName());
             songTag.addElement("artist").addText(song.getSongSinger());
             songTag.addElement("songName").addText(song.getSongTitle());
             songTag.addElement("time").addText(song.getSongTime());
         }
        }
        writeSongListDoc(doc);
        if(songMenuId == 1){
            for(SongTable song : songTables)
            if(findSongFromLocalSong(song))
                setSongIsLike(song,true);
        }
    }
    public static boolean  findSongFromLocalSong(SongTable song){

        Document songListDoc = getSongListDoc();
        Node node = songListDoc.selectSingleNode("//song/path[text()='" +song.getPath() + "']");
        System.out.println(node);
        return  node != null;

    }
    public static  void setSongIsLike(SongTable song,Boolean judge) {

        Document songListDoc = getSongListDoc();
        Node node = songListDoc.selectSingleNode("//song/path[text()='" + song.getPath() + "']");
        Element parent = node.getParent();
        Element isLike = parent.element("isLike");
        if (judge) {
            if (isLike.getText().equals("NO")) {
                isLike.setText("YES");
                writeSongListDoc(songListDoc);
            }
        }else{
            if (isLike.getText().equals("YES")) {
                isLike.setText("NO");
                writeSongListDoc(songListDoc);
            }
        }

    }
    public static  void removeSong(SongTable song){
        Document songList = LocalSongXmlUtil.getSongListDoc();
        Node node = songList.selectSingleNode("//song/path[text()='" + song.getPath() + "']");
        Element parent = node.getParent();
        Element rootElement = songList.getRootElement();
        rootElement.remove(parent);
        writeSongListDoc(rootElement.getDocument());
    }
    public static void writeSongListDoc(Document document){
        XMLWriter xmlWriter =null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path+fileName));
            //使用漂亮格式
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            xmlWriter = new XMLWriter(fileOutputStream,outputFormat);
            xmlWriter.write(document);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                xmlWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //从本地音乐中找
    public static ArrayList<SongTable> getSongTables(String key){
        Document document= getSongListDoc();
        List<Element> selectNodes1=document.selectNodes("//song/tag[contains(songName,'"+key+"')]");;
        Iterator<Element> iterator1 = selectNodes1.iterator();
        ArrayList<SongTable> songList=new ArrayList<SongTable>();
        while(iterator1.hasNext()){
            Element parent = iterator1.next().getParent();
            SongTable song=SongUtil.eleToSongTable(parent);
            if(!songList.contains(song)) {
                songList.add(song);
                System.out.println(song);
            }
        }
        List<Element> selectNodes2=document.selectNodes("//song/tag[contains(album,'"+key+"')]");;
        Iterator<Element> iterator2 = selectNodes2.iterator();
        while(iterator2.hasNext()){
            Element parent = iterator2.next().getParent();
            SongTable song=SongUtil.eleToSongTable(parent);
            if(!songList.contains(song)){
                songList.add(song);
                System.out.println(song);
            }

        }
        List<Element> selectNodes3=document.selectNodes("//song/tag[contains(artist,'"+key+"')]");;
        Iterator<Element> iterator3 = selectNodes3.iterator();
        while(iterator3.hasNext()){
            Element parent = iterator3.next().getParent();
            SongTable song=SongUtil.eleToSongTable(parent);
            if(!songList.contains(song))
                songList.add(song);
        }

        return songList;
    }
    public static void alterSongTitle(SongTable song,String songTitle){
        Document songListDoc = getSongListDoc();
        Element element = (Element) songListDoc.selectSingleNode("//song/path[text()='" +song.getPath() + "']").getParent();
        System.out.println(element);
        element.element("tag").element("songName").setText(songTitle);
        writeSongListDoc(element.getDocument());

    }
    public static void alterSinger(SongTable song,String singer){
        Document songListDoc = getSongListDoc();
        Element element = (Element) songListDoc.selectSingleNode("//song/path[text()='" +song.getPath() + "']").getParent();
        System.out.println(element);
        element.element("tag").element("artist").setText(singer);
        writeSongListDoc(element.getDocument());

    }
    public static void alterAlbum(SongTable song,String album){
        Document songListDoc = getSongListDoc();
        Element element = (Element) songListDoc.selectSingleNode("//song/path[text()='" +song.getPath() + "']").getParent();
        System.out.println(element);
        element.element("tag").element("album").setText(album);
        writeSongListDoc(element.getDocument());

    }

}
