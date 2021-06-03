package Util;

import Controller.RightController;
import Entity.Song;

import Entity.SongTable;
import Gui.GUI;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuSongXmlUtil {
    private static String path = XmlUtil.class.getResource("/").getPath();
    private static String fileName = "MenuList.xml";
    static {

        createSongListDoc();;
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
                Element root = document.addElement("menu-List");




                writeSongListDoc(document);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public static boolean writeSongToSongList(Song song){
//
//        //如果在本地歌单中找到了该歌曲，则写入失败
//        if(findSongFromMenuSong(song)) return false;
//        else{
//            Document songListDoc = getSongListDoc();
//            Element rootElement = songListDoc.getRootElement();
//            Element songElement = rootElement.addElement("song");
//            songElement.addElement("path").addText(song.getPath());
//            songElement.addElement("size").addText(song.getSize());
//            songElement.addElement("isLike").addText(song.getIsLike());
//            Element songTag = songElement.addElement("tag");
//            songTag.addElement("alum").addText(song.getTag().getAlbum());
//            songTag.addElement("artist").addText(song.getTag().getArtist());
//            songTag.addElement("songName").addText(song.getTag().getSongName());
//            songTag.addElement("time").addText(song.getTag().getTime());
//            writeSongListDoc(songListDoc);
//            return true;
//        }
//    }
public static ArrayList<String> getSongTableList(int songMenuId){
    Document songListDoc = getSongListDoc();
    Element rootElement = songListDoc.getRootElement();
    ArrayList<String> songPath = new ArrayList<>();
    Element songMenu = (Element) rootElement.selectSingleNode("//songMenu[@id='" + songMenuId + "']");
    if(songMenu == null) return songPath;
    List<Element> songElement = songMenu.element("song-List").elements("song");

    for(Element el : songElement ){
        String path = el.attributeValue("path");
        songPath.add(path);

    }
    return songPath;
}
//写入一堆歌曲进入歌单中
    public static void writeSongsToSongList(ArrayList<SongTable>songTables, int songMenuId){
        //写入本地音频文件库中
        LocalSongXmlUtil.writeSongsToSongList(songTables,songMenuId);
        RightController rightController = GUI.getRightController();
        if(songMenuId!=0){
            Element songMenu = getSongMenu(songMenuId);
            if(songMenu == null) {
                Document songListDoc = getSongListDoc();
                Element rootElement = songListDoc.getRootElement();
                 songMenu = rootElement.addElement("songMenu").addAttribute("id", String.valueOf(songMenuId));

            }
            Element songList = songMenu.element("song-List");
            if(songList == null)  songList = songMenu.addElement("song-List");
            for(SongTable song : songTables){
                if(!findSongFromMenuSong(song,songMenuId)){
                    songList.addElement("song").addAttribute("path",song.getPath());
                    rightController.addMusicToMenuTable(song);
                }
            }
            writeSongListDoc(songMenu.getDocument());
        }
    }
    //歌单中转移的，不需要写入本地音频
    public static void writeSongToSongList(SongTable song, int songMenuId){



        if(songMenuId!=0){
            Element songMenu = getSongMenu(songMenuId);
            if(songMenu == null) {
                Document songListDoc = getSongListDoc();
                Element rootElement = songListDoc.getRootElement();
                songMenu = rootElement.addElement("songMenu").addAttribute("id", String.valueOf(songMenuId));

            }
            Element songList = songMenu.element("song-List");
            if(songList == null)  songList = songMenu.addElement("song-List");

                if(!findSongFromMenuSong(song,songMenuId)){
                    songList.addElement("song").addAttribute("path",song.getPath());
                    if(songMenuId == 1){
                        LocalSongXmlUtil.setSongIsLike(song,true);
                       GUI.getRightController().setLocalListLike(song);
                    }

                }

            writeSongListDoc(songMenu.getDocument());
        }
    }
    public static Element getSongMenu(int songMenuId){
        Document doc = getSongListDoc();
        Element element = (Element) doc.selectSingleNode("//songMenu[@id='"+songMenuId+"']");

        return element;
    }
    public static void createSongMenu(int songMenuId){
        Document doc = getSongListDoc();
        Element rootElement = doc.getRootElement();
        rootElement.addElement("songMenu").addAttribute("id", String.valueOf(songMenuId));
        writeSongListDoc(doc);
    }
    public static boolean findSongMenu(int songMenuId){
        Document doc = getSongListDoc();
        Node node = doc.selectSingleNode("//songMenu[@id='"+songMenuId+"']");
        return node !=null;
    }
    public static boolean  findSongFromMenuSong(SongTable song,int songMenuId){
        String path = song.getPath();
        Document songListDoc = getSongListDoc();
        Node node = songListDoc.selectSingleNode("//songMenu[@id='"+songMenuId+"']/song-List/song[@path='" + path + "']");
        return  node != null;

    }
    //设置喜欢
    public static void setSongLike(SongTable song) {
        //如果不在我的喜欢列表里面，添加进去
        if (!findSongFromMenuSong(song, 1)) {
            Element songMenu = getSongMenu(1);
            if (songMenu == null) {
                Document songListDoc = getSongListDoc();
                Element rootElement = songListDoc.getRootElement();
                songMenu = rootElement.addElement("songMenu").addAttribute("id", String.valueOf(1));

            }
            Element songList = songMenu.element("song-List");
            if (songList == null) songList = songMenu.addElement("song-List");
            songList.addElement("song").addAttribute("path", song.getPath());
            writeSongListDoc(songMenu.getDocument());
            LocalSongXmlUtil.setSongIsLike(song,true);
        }

    }
    //取消喜欢
    public static  void setSongNotLike(SongTable song){
        if (findSongFromMenuSong(song, 1)) {
            Element songMenu = getSongMenu(1);
            if (songMenu != null) {
                Element songList = songMenu.element("song-List");
                Node node = songList.selectSingleNode("//song[@path='" + song.getPath() + "']");
                songList.remove(node);
                writeSongListDoc(songList.getDocument());
            }

            LocalSongXmlUtil.setSongIsLike(song,false);
        }

    }
    //删除歌曲
    public static  void removeSongFromMenu(SongTable song,int menuId){
         //如果歌单不是本地歌曲库
        if (menuId !=0 && findSongFromMenuSong(song, menuId)) {
            Document songListDoc = getSongListDoc();
            Element rootElement = songListDoc.getRootElement();
            Node node = rootElement.selectSingleNode("//songMenu[@id='" + menuId + "']/song-List/song[@path='" + song.getPath() + "']");
            Element parent = node.getParent();
            parent.remove(node);
            writeSongListDoc(parent.getDocument());
            //如果该歌单是喜欢的歌单
            if(menuId == 1)
            LocalSongXmlUtil.setSongIsLike(song,false);
        }
        if(menuId == 0 ){
          LocalSongXmlUtil.removeSong(song);
          removeSongFromAllMenu(song);
          //删除歌词索引
          LrcUtil.deleteLrc(song);
          //删除旗下的所有子音频
          CutPartUtil.deleteCutPart(song);
        }

    }
    //删除包含该音乐的所有歌单里面的音乐
    public static  void removeSongFromAllMenu(SongTable song){
        //如果歌单不是本地歌曲库
        Document songListDoc = getSongListDoc();
        Element rootElement = songListDoc.getRootElement();
        List<Element> list = rootElement.selectNodes("//songMenu/song-List/song[@path='" + song.getPath() + "']");
        for(Element element : list){
            System.out.println(element);
              element.getParent().remove(element);
        }
        writeSongListDoc(rootElement.getDocument());

    }
    //删除该歌单和歌单中的信息
    public static  void removeSongMenu(int menuId){
        //如果歌单不是本地歌曲库
        Document songListDoc = getSongListDoc();
        Element rootElement = songListDoc.getRootElement();
        Node node = rootElement.selectSingleNode("//songMenu[@id='" + menuId + "']");
        if(node != null){
            rootElement.remove(node);
            writeSongListDoc(rootElement.getDocument());

        }

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

}
