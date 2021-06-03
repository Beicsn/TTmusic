package Entity;
import javafx.application.Platform;

import java.util.ArrayList;

public class SongMenu {
    private String songMenuName;
    private String createTime;
    private  int  parentId;
    private  int id;

    public SongMenu(){

    }
    public SongMenu(String songMenuName, String createTime,int parentId, int id) {
        this.songMenuName = songMenuName;
        this.createTime = createTime;
        this.parentId = parentId;
        this.id = id;

    }

    public String getSongMenuName() {
        return songMenuName;
    }

    public void setSongMenuName(String songMenuName) {
        this.songMenuName = songMenuName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}