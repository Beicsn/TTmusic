package Entity;

/**
 * @version 1.0
 * @Description:   歌曲的实体类
 */
public class Song {
    //歌曲路径
    private String path;
    //歌曲是否喜欢
    private String isLike;
    //歌曲大小
    private String size;
    //歌曲信息
    private Tag tag;


    public Boolean getLike(){
        if(isLike == "YES"){
            return true;
        }
        return false;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setYesLike(){
        this.isLike = "YES";
    }
    public void setNoLike(){
        this.isLike = "NO";
    }


    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public String toString() {
        return "Song [tag=" + tag + ", path=" + path + ", size=" + size + ", isLike=" + isLike + "]";
    }
}
