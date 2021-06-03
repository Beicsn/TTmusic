package service;


import Controller.BottomController;
import Controller.LeftController;
import Entity.SongTable;
import Gui.GUI;
import Gui.Popup;
import Util.PropertiesUtil;
import com.tulskiy.musique.audio.AudioFileReader;
import com.tulskiy.musique.audio.player.Player;
import com.tulskiy.musique.model.Track;
import com.tulskiy.musique.system.TrackIO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import java.io.File;
import java.util.Optional;
import java.util.Random;
import static service.GlobalVariable.*;

public class PlayOperate {

	public static Player player;

	public PlayOperate() {
		initPlayer();
	}

	/**
	 * 初始化音乐播放器
	 */
	public void initPlayer() {
		if (player != null) {
			player.pause();
			player = null;
		}
	}

	/**
	 * 播放音乐
	 * @param songTable
	 */
	static {


		new Thread(
				new Task<Double>() {

					@Override
					protected Double call() {

						while (true) {


							if (Math.round(GUI.getBottomController().getBroadcast_progress().getValue()) == 100 && PlayState.getPlayState().getPlayStatus() == 1) {
								PlayState.getPlayState().setPlayStatus(playPause);
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										broadcastNext();
									}
								});
							} else if (player != null && player.getTrack() != null && PlayState.getPlayState().getPlayStatus() == playContinue) {
//
								long totalSamples = player.getTrack().getTrackData().getTotalSamples();
								long currentSample = player.getCurrentSample();

								double cur_progress = (double) currentSample * 100/  (double)totalSamples ;


								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										//如果没有人为的设置移动条，则slider自动移动
										if (!PlayState.getPlayState().isSliderIsMoved()&&PlayState.getPlayState().getPlayStatus() == playContinue) {
									    		if(GUI.getBottomController().getBroadcast_progress().getValue()== cur_progress){
									    			broadcastNext();
									    			return;
												}

											GUI.getBottomController().getBroadcast_progressBar().setProgress(cur_progress/100);
											GUI.getBottomController().getBroadcast_progress().setValue(cur_progress);
											System.out.println(cur_progress);

//											String text = GUI.getBottomController().getStartTime().getText();
//											System.out.println(text);
//											String[] split = text.split(":");
//
//											int minutes = Integer.parseInt(split[0]) * 60;
//											int seconds = Integer.parseInt(split[1]);
//											int totalSeconds = minutes + seconds + 1;
											double currentMillis = player.getCurrentMillis();
											String startTime = String.format("%02d:%02d", (int)currentMillis/1000 / 60,(int) currentMillis /1000 % 60);
											GUI.getBottomController().setStartTime(startTime);
										}
									}
								});
//


							}
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}


					}
				}


		).start();
	}

	public static void setVolume(Double volume) {
		if (player == null) player = new Player();
		double v = volume / 100 * 1;
		PropertiesUtil.setProperties("volume", String.valueOf(v));
		player.getAudioOutput().setVolume((float) v);


	}

	public static void playSong(SongTable songTable) {
		if (player == null) {
			player = new Player();
			setVolume(GUI.getBottomController().getVolume_size().getValue());
		}
//		if(PlayState.getPlayState().getCurrentSong()!=null)
		//	System.out.println("上一首歌:"+PlayState.getPlayState().getCurrentSong().getSongTitle()+",这一首歌:"+songTable.getSongTitle());
		//	System.out.println(songTable);

		File songFile = new File(songTable.getPath());
		if(!songFile.exists()){

			Alert _alert = new Alert(Alert.AlertType.CONFIRMATION);
			_alert.setTitle("警告");
			_alert.setHeaderText(songTable.getSongTitle()+"歌曲不存在,是否需要替你从歌单移除该歌曲");
			_alert.setContentText("[" + songFile.getAbsolutePath() + "]"+"文件不存在");
			Optional<ButtonType> result = _alert.showAndWait();
			if (result.get() == ButtonType.OK){
				GUI.getRightController().deleteSong(songTable);
			}
				broadcastNext();
				return;
		}
		AudioFileReader audioFileReader = TrackIO.getAudioFileReader(songFile.getName());
		Track read = audioFileReader.read(songFile);
		PlayState.getPlayState().setCurrentSong(songTable);
		player.open(read);
		GUI.getBottomController().getBroadcast_progress().setValue(0);
		PlayState.getPlayState().setPlayStatus(playContinue);
		System.out.println(songTable.getSongSinger());
		GUI.getBottomController().setLabel_singer(songTable.getSongSinger());
		GUI.getBottomController().setLabel_songName(songTable.getSongTitle());
		GUI.getBottomController().setSlider("00:00", songTable.getSongTime());





	changeButton();

}
	public static void  playSongByProgress(Double progress){
		long longProgress = progress.longValue();
		long totalSamples = player.getTrack().getTrackData().getTotalSamples();
		long tmp = (long) (longProgress  * totalSamples / 100);
		PlayState.getPlayState().setCurrentProgress(tmp);
		player.seek(tmp);
	}
//	public static void playSongByTime(String time){
//		long totalSamples = player.getTrack().getTrackData().getTotalSamples();
//		long tmp = (long) (progress / 100 * totalSamples);
//		PlayState.getPlayState().setCurrentProgress(tmp);
//		player.seek(tmp);
//	}

	//暂停音乐
	public static void pauseSong(){
		if(player != null){
			PlayState.getPlayState().setPlayStatus(playPause);
			player.pause();


//			System.out.println(player.getTrack().getTrackData().getTotalSamples());
//			System.out.println(player.getCurrentSample());
			PlayState.getPlayState().setCurrentProgress(player.getCurrentSample());
			changeButton();
		}
	}
	//继续音乐
	public static void continueSong(){
		if(player!=null&&player.getAudioOutput() != null) {


			PlayState.getPlayState().setPlayStatus(playContinue);
			long currentSample = player.getCurrentSample();
			player.open(player.getTrack());
			//继续播放当前进度的音乐
			player.seek(PlayState.getPlayState().getCurrentProgress());


		}else{
			Popup.showTimedDialog(1000,"请选中歌曲后进行播放");
		}
		changeButton();
	}
	//上一首播放
	public static void broadcastPrev(){
		int index = PlayState.getPlayState().getIndex();
		index--;
		if(index < 0) index = PlayState.getPlayState().getSongTableList().size()-1;
		SongTable currentSong = PlayState.getPlayState().getSongTableList().get(index);
		PlayState.getPlayState().setIndex(index);

		playSong(currentSong);
		GUI.getBottomController().initCutProgress();
		BottomController.readMoreController.init(currentSong, LeftController.currentMenu.getValue().getSongMenuName());

	}
	//下一首播放
	public static void broadcastNext(){
		int order = PlayState.getPlayState().getOrder();
		int nowIndex = PlayState.getPlayState().getIndex();
		if(order == orderBroadcast){
		 	 nowIndex++;
		 	 if(nowIndex == PlayState.getPlayState().getSongTableList().size()){
		 	 	nowIndex = 0;
			 }
		}else if(order == repeatBroadcast){
			nowIndex = nowIndex;
		}else if(order == randomBroadcast){
			 nowIndex = createRandom(0,PlayState.getPlayState().getSongTableList().size()-1);
		}




		SongTable currentSong = PlayState.getPlayState().getSongTableList().get(nowIndex);
		PlayState.getPlayState().setIndex(nowIndex);
		playSong(currentSong);
		GUI.getBottomController().initCutProgress();
		BottomController.readMoreController.init(currentSong, LeftController.currentMenu.getValue().getSongMenuName());
	}
	//产生随机数
	public static int createRandom(int min, int max) {
		Random r = new Random();
		return r.nextInt(max-min+1)+ min;
	}
	//改变播放按钮
	public static void changeButton(){
		BottomController bottomController = GUI.getBottomController();
		Button broadcast_suspend = bottomController.getBroadcast_suspend();
		//如果当前状态是播放暂停状态，则图标应该为播放状态
	//	System.out.println(broadcast_suspend.getStyleClass().get(1));
		if(PlayState.getPlayState().getPlayStatus() == playPause){
			if(!broadcast_suspend.getStyleClass().get(1).equals("broadcast_play")){
				broadcast_suspend.getStyleClass().set(1,"broadcast_play");
			}
		}
		if(PlayState.getPlayState().getPlayStatus() == playContinue){
			if(!broadcast_suspend.getStyleClass().get(1).equals("broadcast_pause")){
				broadcast_suspend.getStyleClass().set(1,"broadcast_pause");
			}
		}

	}




}

