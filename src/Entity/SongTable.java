package Entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Objects;

public class SongTable {
    private String like;
    private BooleanProperty likeProperty = new SimpleBooleanProperty(false);
    private String songTitle;
    private String songSinger;
    private String albumName;
    private String songTime;
    private String songSize;
    private String path;
    private String remarks;
    public SongTable(){
        this.songTitle = "未知音乐名";
        this.songSinger = "未知歌手";
        this.albumName = "未知专辑";
        this.songTime = "未知时间";
        this.songSize = "未知大小";
        this.path = "未知位置";
        this.remarks = "";

    }

    public SongTable(String like, String songTitle, String songSinger, String albumName, String songTime, String songSize, String path) {
        this.like = like;
        setLike(like);
        this.songTitle = songTitle;
        this.songSinger = songSinger;
        this.albumName = albumName;
        this.songTime = songTime;
        this.songSize = songSize;
        this.path = path;
    }

    public void setLike(String like){
        this.like = like;
        if(like.equals("YES")){
            likeProperty.setValue(true);
        }else{
            likeProperty.setValue(false);
        }

    }

    public String getLike(){
      return like;
   }
    public boolean isLikeProperty() {
        return likeProperty.get();
    }

    public BooleanProperty likePropertyProperty() {
        return likeProperty;
    }

    public void setLikeProperty(boolean likeProperty) {
        this.likeProperty.set(likeProperty);
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongSinger() {
        return songSinger;
    }

    public void setSongSinger(String songSinger) {
        this.songSinger = songSinger;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSongTime() {
        return songTime;
    }

    public void setSongTime(String songTime) {
        this.songTime = songTime;
    }

    public String getSongSize() {
        return songSize;
    }

    public void setSongSize(String songSize) {
        this.songSize = songSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongTable songTable = (SongTable) o;
        return Objects.equals(like, songTable.like) &&
                Objects.equals(songTitle, songTable.songTitle) &&
                Objects.equals(songSinger, songTable.songSinger) &&
                Objects.equals(albumName, songTable.albumName) &&
                Objects.equals(songTime, songTable.songTime) &&
                Objects.equals(songSize, songTable.songSize) &&
                Objects.equals(path, songTable.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(like, songTitle, songSinger, albumName, songTime, songSize, path);
    }

    @Override
    public String toString() {
        return "SongTable{" +
                "like='" + like + '\'' +
                ", likeProperty=" + likeProperty +
                ", songTitle='" + songTitle + '\'' +
                ", songSinger='" + songSinger + '\'' +
                ", albumName='" + albumName + '\'' +
                ", songTime='" + songTime + '\'' +
                ", songSize='" + songSize + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
