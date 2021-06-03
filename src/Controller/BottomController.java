package Controller;

import Gui.GUI;
import Util.PropertiesUtil;
import Util.TagInfoUtil;
import Util.XmlUtil;
import com.tulskiy.musique.audio.AudioFileReader;
import com.tulskiy.musique.audio.player.Player;
import com.tulskiy.musique.model.Track;
import com.tulskiy.musique.system.TrackIO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.omg.CORBA.PRIVATE_MEMBER;
import service.GlobalVariable;
import service.PlayOperate;
import service.PlayState;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;


public class BottomController {
    //播放暂停按钮
    @FXML
    private Button broadcast_suspend;
    @FXML
    private HBox CD_BOX;
    @FXML
    private Label label_singer;
    @FXML
    private Label label_songName;
    @FXML
    private Slider broadcast_progress;
    @FXML
    private Slider broadcast_startProgress;
    @FXML
    private Slider broadcast_endProgress;
    @FXML
    private ProgressBar broadcast_progressBar;
    @FXML
    private Label startTime;
    @FXML
    private Slider volume_size;
    @FXML
    private Button volume;
    @FXML
    private MenuButton broadcast_order;
    @FXML
    private ProgressBar volume_progressBar;
    public static int count =1;
    public static  ReadMoreController readMoreController;
    public Slider getBroadcast_progress() {
        return broadcast_progress;
    }

    public ProgressBar getBroadcast_progressBar() {
        return broadcast_progressBar;
    }


    @FXML
    private Label totalTime;
    public static AnchorPane readMorePane;
    @FXML
    public Button getBroadcast_suspend() {
        return broadcast_suspend;
    }

    public Slider getVolume_size() {
        return volume_size;
    }
    //控制音量
    public void volumeMoved(){
        double value = volume_size.getValue();
        PlayOperate.setVolume(value);
        volume_progressBar.setProgress(value/100);
        if(value == 0){
            this.volume.getStyleClass().set(1,"volumeZero");
        }else{
            this.volume.getStyleClass().set(1,"volume");
        }
    }
    //顺序播放
    public void orderBroadcast(){
        ImageView imageView = new ImageView("./Img/Bottom/order_broadcastDark.png");
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        this.broadcast_order.setGraphic(imageView);
        PlayState.getPlayState().setOrder(GlobalVariable.orderBroadcast);
        PropertiesUtil.setProperties("broadcastOrder",String.valueOf(GlobalVariable.orderBroadcast));
    }
    //随机播放
    public void randomBroadcast(){
        ImageView imageView = new ImageView("./Img/Bottom/random_broadcastDark.png");
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        this.broadcast_order.setGraphic(imageView);
        PlayState.getPlayState().setOrder(GlobalVariable.randomBroadcast);
        PropertiesUtil.setProperties("broadcastOrder",String.valueOf(GlobalVariable.randomBroadcast));
    }
    //单曲循环
    public void repeatBroadcast(){
        ImageView imageView = new ImageView("./Img/Bottom/repeat_broadcastDark.png");
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        this.broadcast_order.setGraphic(imageView);
        PlayState.getPlayState().setOrder(GlobalVariable.repeatBroadcast);
        PropertiesUtil.setProperties("broadcastOrder",String.valueOf(GlobalVariable.repeatBroadcast));
    }
    //点击按钮秒变静音
    public void volumeZero(){
        PlayOperate.setVolume((double) 0);
        this.volume.getStyleClass().set(1,"volumeZero");
        this.volume_size.setValue(0);
        this.volume_progressBar.setProgress(0);
    }
    public Label getStartTime() {
        return startTime;
    }

    public void setBroadcast_suspend(Button broadcast_suspend) {
        this.broadcast_suspend = broadcast_suspend;
    }
    //初始化
    public void init(){
        //初始化音量
        if(PropertiesUtil.getPropertiesKey("volume") != null) {
            double volume = Double.parseDouble(PropertiesUtil.getPropertiesKey("volume")) * 100;
            if(volume == 0){
                this.volume.getStyleClass().set(1,"volumeZero");
            }else{
                this.volume.getStyleClass().set(1,"volume");
            }
            this.volume_size.setValue(volume);
            this.volume_progressBar.setProgress(volume/100);
        }
        if(PropertiesUtil.getPropertiesKey("broadcastOrder")!=null){
            switch (PropertiesUtil.getPropertiesKey("broadcastOrder")){
                case "11":
                    GUI.getBottomController().repeatBroadcast();
                    break;
                case "12":
                    GUI.getBottomController().orderBroadcast();
                    break;
                case "13":
                    GUI.getBottomController().randomBroadcast();
                    break;
            }
        }
        broadcast_progress.valueProperty().addListener((observable, oldValue, newValue) -> {
                   broadcast_progressBar.setProgress(broadcast_progress.getValue()/100);
        });
        volume_size.valueProperty().addListener((observable, oldValue, newValue) -> {
            volume_progressBar.setProgress(volume_size.getValue()/100);
        });
        broadcast_startProgress.valueProperty().addListener((observable, oldValue, newValue) -> {
            String text = totalTime.getText();
            String[] split = text.split(":");
            int minutes = Integer.parseInt(split[0]);
            int seconds = Integer.parseInt(split[1]);
            int totalMillisSeconds = (minutes*60 + seconds) * 1000;
            double millis =  newValue.doubleValue() /100 * totalMillisSeconds;
            ReadMoreController.startCurrentMillis =  millis;
            ReadMoreController.startValue =  newValue.doubleValue();

            readMoreController.setStartTime(String.format("%02d:%02d",(int)millis/1000/60,(int) millis / 1000  %60));
        });
        broadcast_endProgress.valueProperty().addListener((observable, oldValue, newValue) -> {
            String text = totalTime.getText();
            String[] split = text.split(":");
            int minutes = Integer.parseInt(split[0]);
            int seconds = Integer.parseInt(split[1]);
            int totalMillisSeconds = (minutes*60 + seconds) * 1000;
            double millis =  newValue.doubleValue() /100 * totalMillisSeconds;
            ReadMoreController.endCurrentMillis =  millis;
            readMoreController.setEndTime(String.format("%02d:%02d",(int)millis/1000/60,(int) millis / 1000  %60));
        });
        //            broadcast_progress.valueProperty().addListener((observable, oldValue, newValue) ->
//            {  //System.out.println("进入到此values");
//                        if(!broadcast_progress.isValueChanging()){
//
//                            if(Math.round(broadcast_progress.getValue()) >= 100){
//
//                                PlayOperate.broadcastNext();
//                            }
//                        }
//            });
    }
    @FXML
    public  void playOrPause(ActionEvent actionEvent){
        String status = broadcast_suspend.getStyleClass().get(1);
        switch (status){
            case "broadcast_play":
                PlayOperate.continueSong();
                break;
            case "broadcast_pause":
                PlayOperate.pauseSong();
                break;
        }

    }
    public void setStartTime(String startTime){

        this.startTime.setText(startTime);


    }
    public void setSlider(String startTime,String totalTime){
        if(broadcast_progress.isDisable()){
            broadcast_progress.setDisable(false);
        }
        this.startTime.setText(startTime);
        this.totalTime.setText(totalTime);

    }
    public void sliderMoved(){

//        System.out.println("设置不可移动");
//        String[] split = totalTime.getText().split(":");
//        String s1 = split[0];
//        String s2 = split[1];
//        double minutes = Double.parseDouble(s1) *60;
//        double seconds = Double.parseDouble(s2);
//        double totalSeconds = minutes + seconds;
//        int tmp = (int) (broadcast_progress.getValue() / 100 * totalSeconds);
//
//        String startTime = String.format("%02d:%02d", tmp/60, tmp%60);
//        this.startTime.setText(startTime);
        PlayOperate.playSongByProgress(broadcast_progress.getValue());
        broadcast_progressBar.setProgress(broadcast_progress.getValue()/100);
        double currentMillis = PlayOperate.player.getCurrentMillis();
        String startTime = String.format("%02d:%02d", (int)currentMillis/1000 / 60,(int) currentMillis /1000 % 60);
        this.startTime.setText(startTime);
        PlayState.getPlayState().setSliderIsMoved(false);
    }
    public void sliderPressed(){
        System.out.println("设置不可移动");

        broadcast_progressBar.setProgress(broadcast_progress.getValue()/100);
        PlayState.getPlayState().setSliderIsMoved(true);
    }
    public void setLabel_singer(String singer){
        this.label_singer.setText(singer);
    }
    public void setLabel_songName(String songName){
        this.label_songName.setText(songName);
    }
    @FXML
    public void playPrev(ActionEvent actionEvent){
        PlayOperate.broadcastPrev();
    }
    @FXML
    public void playNext(ActionEvent actionEvent){
        PlayOperate.broadcastNext();
    }
    public void readMore() throws IOException {
        if(PlayState.getPlayState().getCurrentSong()== null) return ;
        count++;
        if(count%2 == 0){
            GUI.TopPane.setLeft(null);
            GUI.TopPane.setRight(readMorePane);
            readMoreController.init(PlayState.getPlayState().getCurrentSong(),LeftController.currentMenu.getValue().getSongMenuName());

        }else{
            GUI.TopPane.setLeft(GUI.LeftPane);
            GUI.TopPane.setRight(GUI.RightPane);
        }

    }
    static {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("../Gui/readMore.fxml"));
        try {
             readMorePane = fxmlLoader.load();
           readMoreController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void  setSliderVisible(){
        readMoreController.setStartTime("00:00");
        ReadMoreController.startValue =  0;
        ReadMoreController.startCurrentMillis = 0;

        String text = totalTime.getText();
        String[] split = text.split(":");
        int minutes = Integer.parseInt(split[0]);
        int seconds = Integer.parseInt(split[1]);
        int totalMillisSeconds = (minutes*60 + seconds) * 1000;
        ReadMoreController.endCurrentMillis = totalMillisSeconds;
        readMoreController.setEndTime(this.totalTime.getText());
        this.broadcast_startProgress.setVisible(true);
        this.broadcast_endProgress.setVisible(true);
    }
    public void  setSliderNotVisible(){
        readMoreController.setStartTime ("开始时间");
        readMoreController.setEndTime("截至时间");
        this.broadcast_startProgress.setVisible(false);
        this.broadcast_startProgress.setValue(0);
        this.broadcast_endProgress.setVisible(false);
        this.broadcast_endProgress.setValue(100);
    }
    public void initCutProgress(){
        this.broadcast_startProgress.setValue(0);
        this.broadcast_endProgress.setValue(100);
    }
}
