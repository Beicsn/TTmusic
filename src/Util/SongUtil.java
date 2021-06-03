package Util;


import Entity.Song;
import Entity.SongTable;
import Entity.Tag;
import org.dom4j.Element;

import java.text.DecimalFormat;

/** 
* @date 2017年3月19日 上午1:10:26 
* @version 1.0 
* @Description:   Song工具类
*/
public class SongUtil {
	public static String getSizeToMb(long size) {

	    DecimalFormat df = new DecimalFormat("#.00");
	    String fileSizeString = "";
	    if (size < 1024) {
	        fileSizeString = df.format((double) size) + "B";
	    } else if (size < 1048576) {
	        fileSizeString = df.format((double) size / 1024) + "K";
	    } else if (size < 1073741824) {
	        fileSizeString = df.format((double) size / 1048576) + "M";
	    }
	        return fileSizeString;
	    }
	
	public static Song eleToSong(Element element) {
        Song song=new Song();
        song.setPath(element.elementText("path"));
        song.setSize(element.elementText("size"));
        song.setIsLike(element.elementText("isLike"));
        Element tagElement = element.element("tag");
        Tag tag=new Tag();
        tag.setAlbum(tagElement.elementText("album"));
        tag.setSongName(tagElement.elementText("songName"));
        tag.setTime(tagElement.elementText("time"));
        tag.setArtist(tagElement.elementText("artist"));
        song.setTag(tag);
        return song;
	}
    public static SongTable	eleToSongTable(Element element){
		SongTable songTable = new SongTable();
		songTable.setPath(element.elementText("path"));
		songTable.setSongSize (element.elementText("size"));
		songTable.setLike(element.elementText("isLike"));
		Element tagElement = element.element("tag");

		songTable.setAlbumName(tagElement.elementText("album"));
		songTable.setSongTitle(tagElement.elementText("songName"));
		songTable.setSongTime(tagElement.elementText("time"));
		songTable.setSongSinger(tagElement.elementText("artist"));

		return songTable;
	}
	
	public static void songToEle(Song song, Element parent) {
		Element node=parent.addElement("song");
		node.addElement("path").setText(song.getPath());
		node.addElement("Size").setText(song.getSize());
		node.addElement("isLike").setText(song.getIsLike());
		Element tagElement = node.addElement("tag");
		tagElement.addElement("album").setText(song.getTag().getAlbum().trim());
		tagElement.addElement("artist").setText(song.getTag().getArtist().trim());
		tagElement.addElement("songName").setText(song.getTag().getSongName().trim());
		tagElement.addElement("Size").setText(song.getTag().getTime().trim());
	}
	public static SongTable songToSongTable(Song song){
		SongTable songTable = new SongTable();

		songTable.setLike(song.getIsLike());
		songTable.setLikeProperty(song.getLike());
		songTable.setSongTitle(song.getTag().getSongName());
		songTable.setSongSinger(song.getTag().getArtist());
		songTable.setAlbumName(song.getTag().getAlbum());
		songTable.setSongTime(song.getTag().getTime());
		songTable.setSongSize(song.getSize());
		songTable.setPath(song.getPath());
		return songTable;

	}

	
}
 