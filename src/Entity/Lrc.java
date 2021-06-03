package Entity;

public class Lrc {
    //时间
    private Long currentTime;
    //歌词
    private String currentContent;

    public Lrc(Long currentTime, String currentContent) {
        this.currentTime = currentTime;
        this.currentContent = currentContent;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(String currentContent) {
        this.currentContent = currentContent;
    }
}
