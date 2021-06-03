package Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LrcInfo {
	private String title;//歌曲名
	private String singer;//演唱者
	private String album;//专辑	
	private ArrayList<Lrc> infos;//保存歌词信息和时间点

	//以下为getter()  setter()

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public ArrayList<Lrc> getInfos() {
		return infos;
	}

	public void setInfos(ArrayList<Lrc> infos) {
		this.infos = infos;
	}
}