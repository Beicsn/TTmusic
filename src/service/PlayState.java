package service;

import Entity.SongTable;
import javafx.collections.ObservableList;

import java.util.UUID;

public class PlayState {
     public static  PlayState playState;
    //播放状态,默认暂停
    private int playStatus = GlobalVariable.playPause;
    //当前播放时间
    private long currentProgress;
    //当前播放的音乐
    private SongTable currentSong;
    //当前播放的音乐歌单
    private ObservableList<SongTable> songTableList;
    //音乐的下标索引
    private int index;
    //滑动条是否在移动
    private boolean sliderIsMoved = false;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    //播放顺序,默认顺序播放
    private int order = GlobalVariable.orderBroadcast;
    public boolean isSliderIsMoved() {
        return sliderIsMoved;
    }

    public void setSliderIsMoved(boolean sliderIsMoved) {
        this.sliderIsMoved = sliderIsMoved;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ObservableList<SongTable> getSongTableList() {
        return songTableList;
    }

    public void setSongTableList(ObservableList<SongTable> songTableList) {
        this.songTableList = songTableList;
    }

    public long getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(long currentProgress) {
        this.currentProgress = currentProgress;
    }

    public SongTable getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(SongTable currentSong) {
        this.currentSong = currentSong;
    }



    public static PlayState getPlayState() {
        return playState;
    }



    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    static {
            playState = new PlayState();


    }


}
