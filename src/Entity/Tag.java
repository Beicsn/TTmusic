package Entity;

/** @date 2
 ** @version 1.0
 * * @Description:   Tag信息实体类
 */
public class Tag {
    //音乐名称
    private String songName;
    //歌手
    private String artist;
    //专辑
    private String Album;
    //歌曲时长
    private String time;

    public Tag(String songName, String artist, String album, String time) {
        this.songName = songName;
        this.artist = artist;
        Album = album;
        this.time = time;
    }
    public Tag() {
        songName = "";
        artist = "";
        Album = "";
        time ="";
    }
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
