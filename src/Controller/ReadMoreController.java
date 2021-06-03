package Controller;

import Entity.*;
import Gui.GUI;
import Gui.Popup;
import Util.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import service.GlobalVariable;
import service.LrcJsoup;
import service.PlayOperate;
import service.PlayState;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadMoreController {

    public static double startCurrentMillis;
    public static double startValue;
    public static double endCurrentMillis;


    @FXML
    public Label startTimeLabel;
    @FXML
    public Label endTimeLabel;
    @FXML
    public Label songName;
    @FXML
    public Label singer;
    @FXML
    public Label album;
    @FXML
    public Label songMenu;
    @FXML
    public TableColumn<Lrc, String> lrcColumn;
    @FXML
    public TableView<Lrc> lrcTable;
    @FXML
    public TableView<CutPart> cutPartTable;
    @FXML
    public TableColumn<CutPart, String> cutStartColumn;
    @FXML
    public TableColumn<CutPart, String> cutEndColumn;
    @FXML
    public TableColumn<CutPart, String> operation1Column;
    @FXML
    public TableColumn<CutPart, String> operation2Column;
    @FXML
    public TableColumn<CutPart, String> operation3Column;
    @FXML
    public TableView<Message> messageTable;
    @FXML TableColumn<Message,String> messageColumn;
    //剪辑试听单线程
    public ExecutorService cutPartService = Executors.newSingleThreadExecutor();
    //歌词自动滚动单线程
    public ExecutorService lrcService = Executors.newSingleThreadExecutor();
    //执行网络检索单线程
    public ExecutorService jsoupService = Executors.newSingleThreadExecutor();
    public static songCreateController songCreateController;
    public static Stage stage = new Stage();
    public static AnchorPane load;
    public static SongTable currentSong;
    public LrcInfo lrcInfo;
    private UUID lrcOverThread;
    private UUID cutOverThread;
    //歌词列表
    public ObservableList<Lrc> lrcObservableList = FXCollections.observableArrayList();
    //留言板
    public ObservableList<Message> messageObservableList = FXCollections.observableArrayList();
    //子音频
    public ObservableList<CutPart> cutPartObservableList = FXCollections.observableArrayList();

    //选中的子音频
    public CutPart cutPart;


    public void startCut() {
        currentSong = PlayState.getPlayState().getCurrentSong();
        this.startValue = GUI.getBottomController().getBroadcast_progress().getValue();


        this.startCurrentMillis = PlayOperate.player.getCurrentMillis();
        String startTime = String.format("%02d:%02d", (int) startCurrentMillis / 1000 / 60, (int) startCurrentMillis / 1000 % 60);
        this.startTimeLabel.setText(startTime);

    }

    public void endCut() {


        this.endCurrentMillis = PlayOperate.player.getCurrentMillis();
        String endTime = String.format("%02d:%02d", (int) endCurrentMillis / 1000 / 60, (int) endCurrentMillis / 1000 % 60);
        this.endTimeLabel.setText(endTime);
        PlayOperate.pauseSong();

    }

    public void listenCutPart() {
        if (startTimeLabel.getText().equals("开始时间")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("操作提示");
            alert.setHeaderText("请先确认截取的开始时间");
            alert.setContentText("点击开始截取按钮即可开始");
            alert.showAndWait();
            return;
        }
        if (endTimeLabel.getText().equals("截止时间")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("操作提示");
            alert.setHeaderText("请先确认截取的截止时间");
            alert.setContentText("点击停止截取按钮即可停止");
            alert.showAndWait();
            return;
        }

        GUI.getBottomController().getStartTime().setText(startTimeLabel.getText());

        if (PlayState.getPlayState().getPlayStatus() == GlobalVariable.playPause) {
            PlayOperate.continueSong();
        }
        PlayOperate.playSongByProgress(startValue);
        this.lrcOverThread = UUID.randomUUID();
        cutPartService.execute(
                new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {
                        AtomicBoolean judge = new AtomicBoolean(false);
                        UUID id=lrcOverThread;
                        while (!judge.get()) {
                            //避免多任务执行
                            if(id!=lrcOverThread){
                                this.cancel();
                            }

                            if(isCancelled()){
                                break;
                            }
                            Platform.runLater(() -> {
                                System.out.println(PlayOperate.player.getCurrentMillis());
                                if (PlayOperate.player.getCurrentMillis() >= endCurrentMillis) {
                                    judge.set(true);
                                }
                            });

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        }
                        if (judge.get()) {
                            PlayOperate.pauseSong();
                            System.out.println("开始时间:" + startCurrentMillis);
                            System.out.println("截止时间:" + endCurrentMillis);
                        }
                        return null;
                    }
                }


        );
//        GUI.getBottomController().getBroadcast_progress().valueProperty().addListener((observable, oldValue, newValue) -> {
//            if(cutProperties && ((Double) observable.getValue() == endValue)){
//                System.out.println(startCurrentMillis);
//                System.out.println(endCurrentMillis);
//                System.out.println(endValue);
//                cutProperties = false;
//                PlayOperate.pauseSong();
//            }
//
//        });


    }

    public void createNewFile() {

        //关闭监听
//        stage.setOnCloseRequest(event -> {
//            controller.getTextField().setText("");
//        });
        if (startTimeLabel.getText().equals("开始时间")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("操作提示");
            alert.setHeaderText("请先确认截取的开始时间");
            alert.setContentText("点击开始截取按钮即可开始");
            alert.showAndWait();
            return;
        }
        if (endTimeLabel.getText().equals("截止时间")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("操作提示");
            alert.setHeaderText("请先确认截取的截止时间");
            alert.setContentText("点击停止截取按钮即可停止");
            alert.showAndWait();
            return;
        }

        load.requestFocus();
        stage.show();
    }

    static {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ReadMoreController.class.getResource("/Gui/songCreate.fxml"));
            load = fxmlLoader.load();
             songCreateController = fxmlLoader.getController();
            songCreateController.filePlace.setText("文件将保存在该目录下:" + PropertiesUtil.getPropertiesKey("cutPartCatalog"));
            Scene scene = new Scene(load);
            stage.setScene(scene);
            stage.setTitle("创建新的剪辑音频");

            stage.getIcons().add(new Image("./Img/other/music.png"));
            stage.initModality(Modality.APPLICATION_MODAL);
            scene.getStylesheets().add("./Css/songMenu.css");
            //不可改变大小
            stage.setResizable(false);
            songCreateController.newSongNameText.textProperty().addListener((observable, oldValue, newValue) -> {
                if (observable.getValue().length() > 0) {
                    songCreateController.createNewSongButton.setDisable(false);
                } else {
                    songCreateController.createNewSongButton.setDisable(true);
                }
            });
            stage.setOnCloseRequest(event -> {
                songCreateController.newSongNameText.setText("");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void init(SongTable song, String songMenu) {

        if (currentSong != song) {
            currentSong = song;
            this.album.setText("专辑:" + currentSong.getAlbumName());
            this.singer.setText("歌手:" + currentSong.getSongSinger());
            this.songName.setText("歌曲:" + currentSong.getSongTitle());
            this.songMenu.setText("来源:" + songMenu);
            this.startTimeLabel.setText("开始时间");
            this.endTimeLabel.setText("截止时间");
            readLrc(currentSong);
            readCutPart(currentSong);
            readMessage(currentSong);
        }

        cutStartColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        cutEndColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        operation1Column.setCellValueFactory(new PropertyValueFactory<>("broadCast"));
        operation2Column.setCellValueFactory(new PropertyValueFactory<>("delete"));
        operation3Column.setCellValueFactory(new PropertyValueFactory<>("create"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        cutPartTable.setRowFactory(param -> {
            TableRow<CutPart> row = new TableRow<CutPart>();
            row.setOnMouseClicked(event -> {
//                if(row!=null)
//               cutPart = cutPartTable.getItems().get(row.getIndex());

            });
            return row;
        });


        lrcColumn.setCellFactory(new Callback<TableColumn<Lrc, String>, TableCell<Lrc, String>>() {
            @Override
            public TableCell<Lrc, String> call(TableColumn<Lrc, String> param) {
                TableCell<Lrc, String> tc = new TableCell<Lrc, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        if (item != null) {
                            setText(item);
                        } else {
                            setText("");
                        }
                    }
                };
                tc.setAlignment(Pos.CENTER);
                tc.setStyle("-fx-font-size: 15px");


                return tc;
            }
        });
        lrcColumn.setCellFactory(new Callback<TableColumn<Lrc, String>, TableCell<Lrc, String>>() {
            @Override
            public TableCell<Lrc, String> call(TableColumn<Lrc, String> param) {
                TableCell<Lrc, String> tc = new TableCell<Lrc, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        if (item != null) {
                            setText(item);
                        } else {
                            setText("");
                        }
                    }
                };
                tc.setAlignment(Pos.CENTER);
                tc.setStyle("-fx-font-size: 15px");


                return tc;
            }
        });
        lrcColumn.setCellFactory(new Callback<TableColumn<Lrc, String>, TableCell<Lrc, String>>() {
            @Override
            public TableCell<Lrc, String> call(TableColumn<Lrc, String> param) {
                TableCell<Lrc, String> tc = new TableCell<Lrc, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        if (item != null) {
                            setText(item);
                        } else {
                            setText("");
                        }
                    }
                };
                tc.setAlignment(Pos.CENTER);
                tc.setStyle("-fx-font-size: 15px");


                return tc;
            }
        });
        cutStartColumn.setCellFactory(new Callback<TableColumn<CutPart, String>, TableCell<CutPart, String>>() {
            @Override
            public TableCell<CutPart, String> call(TableColumn<CutPart, String> param) {
                TableCell<CutPart, String> tc = new TableCell<CutPart, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        if (item != null) {
                            setText(item);
                        } else {
                            setText("");
                        }
                    }
                };
                tc.setAlignment(Pos.CENTER);
                tc.setStyle("-fx-font-size: 15px");


                return tc;
            }
        });
        cutEndColumn.setCellFactory(new Callback<TableColumn<CutPart, String>, TableCell<CutPart, String>>() {
            @Override
            public TableCell<CutPart, String> call(TableColumn<CutPart, String> param) {
                TableCell<CutPart, String> tc = new TableCell<CutPart, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        if (item != null) {
                            setText(item);
                        } else {
                            setText("");
                        }
                    }
                };
                tc.setAlignment(Pos.CENTER);
                tc.setStyle("-fx-font-size: 15px");


                return tc;
            }
        });

    }

    public void readLrc(SongTable song) {

        LrcParser lp = new LrcParser();
        try {
            lrcInfo = lp.parser(song);
            if (lrcInfo == null) {
                lrcTable.setItems(null);
                return;
            }


            ArrayList<Lrc> infos = lrcInfo.getInfos();
            lrcObservableList.removeAll(lrcObservableList);
            lrcColumn.setCellValueFactory(new PropertyValueFactory<>("currentContent"));
            lrcObservableList.addAll(infos);
            lrcTable.setItems(lrcObservableList);
            scrollLrc();

        } catch (Exception e) {
            System.out.println("parser erro");
            e.printStackTrace();
        }


    }

    //自动滚动歌词
    public void scrollLrc() {


        lrcService.execute(new Runnable() {
                               @Override
                               public void run() {
                                   {

                                       AtomicInteger i = new AtomicInteger(0);
                                       AtomicBoolean judge = new AtomicBoolean(false);
                                       while (true) {
                                         

                                           try {


                                               Platform.runLater(() -> {
                                                   if (lrcTable.getItems() == null) {
                                                       judge.set(true);
                                                       return;

                                                   }
                                                   while (i.get() < lrcTable.getItems().size() && lrcTable.getItems().get(i.get()).getCurrentTime() - PlayOperate.player.getCurrentMillis() < 0) {
                                                       i.incrementAndGet();


                                                   }

                                                   while (i.get() < lrcTable.getItems().size() && lrcTable.getItems().get(i.get()).getCurrentTime() - PlayOperate.player.getCurrentMillis() > 100) {
                                                       i.decrementAndGet();
                                                   }
                                                   if (i.get() >= lrcTable.getItems().size()) {
                                                       i.set(lrcTable.getItems().size() - 1);
                                                   }
                                                   int finalI = i.get();
                                                   Lrc lrc = lrcTable.getItems().get(finalI);
                                                   if (lrc.getCurrentTime() - PlayOperate.player.getCurrentMillis() < 100) {
                                                       int tmp = finalI -6< 0 ? 0 : finalI - 6;
                                                       lrcTable.scrollTo(tmp);
                                                       lrcTable.getSelectionModel().select(finalI);
                                                       if (i.get() < lrcTable.getItems().size()) {
                                                           i.getAndIncrement();
                                                       }

                                                   }

                                               });
                                                if(judge.get()) break;

                                               Thread.sleep(100);
                                           } catch (InterruptedException e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }
                               }
                           }
        );

    }

    //删除歌词文件
    public void deleteLrc() {
        if (lrcTable.getItems() == null) {
            Popup.showTimedDialog(500, "无歌词文件,无需删除");
            return;
        }

        lrcTable.setItems(null);
        String lrcPath = LrcUtil.getLrcPath(currentSong);


        LrcUtil.deleteLrc(currentSong);
        if(lrcPath !=null){
            Platform.runLater(() -> {
                Alert _alert = new Alert(Alert.AlertType.CONFIRMATION);
                _alert.setTitle("警告");
                _alert.setHeaderText("已删除,是否需要删除本地磁盘中的歌词文件?");
                _alert.setContentText("该歌词文件存在于:" + lrcPath+",确认删除将导致本地磁盘中该文件消失");
                Optional<ButtonType> result = _alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    File file = new File(lrcPath);
                    if(file.exists()){
                        file.delete();
                    }
                }
            });
        }

    }

    //
    public void addLrc() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("添加歌词文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("lrc格式", "*.lrc"),
                new FileChooser.ExtensionFilter("txt格式", "*.txt")

        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(GUI.getPrimaryStage());
        if (selectedFiles != null) {
            if (selectedFiles.size() != 1) {
                Popup.showTimedDialog(500, "一次只能上传一个歌词文件");
            } else {
                File file = selectedFiles.get(0);
                LrcUtil.writeLrc(currentSong, file.getPath());
                readLrc(currentSong);


            }
        }
    }

    public void getLrcFromInternet() {
        jsoupService.execute(
                new Runnable() {
                    @Override
                    public void run() {


                            lrcInfo = LrcJsoup.getLrcFromInternet(currentSong);

                            ArrayList<Lrc> infos = lrcInfo.getInfos();


                        Platform.runLater(() -> {
                            if (infos == null || infos.size() < 1) {
                                Popup.showTimedDialog(1000, "无网络歌词");
                                return;
                            }
                            lrcObservableList.removeAll(lrcObservableList);
                            lrcColumn.setCellValueFactory(new PropertyValueFactory<>("currentContent"));
                            lrcObservableList.addAll(infos);
                            lrcTable.setItems(lrcObservableList);
                            scrollLrc();
                            Popup.showTimedDialog(500, "获取成功");
                        });


                    }
                }
        );

    }

    public void downLoadLrc() {
        jsoupService.execute(() -> {

                lrcInfo = LrcJsoup.downLoadLrc(currentSong);
                ArrayList<Lrc> infos = lrcInfo.getInfos();

                if (infos == null||infos.size() == 0) {
                    Platform.runLater(() -> {
                        Popup.showTimedDialog(1000, "无网上歌词资源");
                    });
                } else {
                    String fileName = LrcWrite.writeLrcFile(LrcJsoup.lrcList, currentSong);

                    if (LrcUtil.findLrc(currentSong)) {
                        Platform.runLater(() -> {
                        Alert _alert = new Alert(Alert.AlertType.CONFIRMATION);
                        _alert.setTitle("警告");
                        _alert.setHeaderText("已存在" + currentSong.getSongTitle() + "的歌词，是否需要替换");
                        _alert.setContentText("原歌曲歌词文件存在于:" + LrcUtil.getLrcPath(currentSong));
                        Optional<ButtonType> result = _alert.showAndWait();

                        if (result.get() == ButtonType.OK) {

                            LrcUtil.writeLrc(currentSong, PropertiesUtil.getPropertiesKey("lrcCatalog") + fileName + ".lrc");
                            lrcObservableList.removeAll(lrcObservableList);
                            lrcColumn.setCellValueFactory(new PropertyValueFactory<>("currentContent"));
                            lrcObservableList.addAll(infos);
                            lrcTable.setItems(lrcObservableList);
                            scrollLrc();

                        } else if (result.get() == ButtonType.CANCEL) {
                            System.out.println("测试");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("提示");
                            alert.setHeaderText("当前歌词文件仍为原来的歌词文件");
                            alert.setContentText("下载的歌词文件的地址:" + PropertiesUtil.getPropertiesKey("lrcCatalog") + fileName + ".lrc");
                            alert.showAndWait();
                        }
                        });
                    } else {
                        Platform.runLater(() -> {
                        LrcUtil.writeLrc(currentSong, PropertiesUtil.getPropertiesKey("lrcCatalog")+ fileName + ".lrc");
                        lrcObservableList.removeAll(lrcObservableList);
                        lrcColumn.setCellValueFactory(new PropertyValueFactory<>("currentContent"));
                        lrcObservableList.addAll(infos);
                        lrcTable.setItems(lrcObservableList);
                        scrollLrc();
                            Popup.showTimedDialog(1000, "下载成功，已保存为当前歌曲的歌词");
                        });

                    }

                }

        });


    }
    public void addCutPart(){
        if (startTimeLabel.getText().equals("开始时间")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("操作提示");
            alert.setHeaderText("请先确认截取的开始时间");
            alert.setContentText("点击开始截取按钮即可开始");
            alert.showAndWait();
            return;
        }
        if (endTimeLabel.getText().equals("截止时间")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("操作提示");
            alert.setHeaderText("请先确认截取的截止时间");
            alert.setContentText("点击停止截取按钮即可停止");
            alert.showAndWait();
            return;
        }
        CutPart cutPart = new CutPart();
        cutPart.setStartTime(this.startTimeLabel.getText());
        cutPart.setEndMillis(endCurrentMillis);
        cutPart.setEndTime(this.endTimeLabel.getText());
        cutPart.setStartProgress(startValue);
        cutPart.setStartMillis(startCurrentMillis);
        if(!cutPartObservableList.contains(cutPart)){
            System.out.println("添加成功");
            cutPartObservableList.add(cutPart);
            CutPartUtil.addChildPart(currentSong,cutPart);
        }else{
            Popup.showTimedDialog(1000,"已存在,无需重复添加");
        }



    }
    public void readCutPart(SongTable song){
        ArrayList<CutPart> cutPartList = CutPartUtil.getCutPartList(song);
        System.out.println(cutPartList.size());
        cutPartObservableList.removeAll(cutPartObservableList);
        cutPartObservableList.addAll(cutPartList);
        cutPartTable.setItems(cutPartObservableList);
    }
    public void broadCastPart(String startTime,double startProgress,double endMillis){
         PlayOperate.pauseSong();
        GUI.getBottomController().getStartTime().setText(startTime);


        PlayOperate.playSongByProgress(startProgress);
        if (PlayState.getPlayState().getPlayStatus() == GlobalVariable.playPause) {
            PlayOperate.continueSong();
        }
        this.cutOverThread = UUID.randomUUID();
        cutPartService.execute(
                new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {
                        AtomicBoolean judge = new AtomicBoolean(false);
                        UUID id = cutOverThread;
                        AtomicInteger i = new AtomicInteger(1);
                        while (!judge.get()) {
                            //避免多任务执行
                            if(id!=cutOverThread){
                                this.cancel();
                            }

                            if(isCancelled()){
                                break;
                            }
                            Platform.runLater(() -> {
                                //此步的目的是为了给多线程执行的时间，因为需要等待主线程执行修改播放时间的操作。避免出现子线程优先执行的步骤
                                if(i.get() == 1){
                                    try {
                                        Thread.sleep(500);
                                        i.incrementAndGet();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println(PlayOperate.player.getCurrentMillis());
                                if (PlayOperate.player.getCurrentMillis() >= endMillis) {
                                    judge.set(true);
                                }
                            });

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        }
                        if (judge.get()) {
                            PlayOperate.pauseSong();
                            System.out.println("开始时间:" + startTime);
                            System.out.println("截止时间:" + endMillis);
                        }
                        return null;
                    }
                }


        );
    }
    public void deleteCutPart(String startTime,String endTime,double startProgress,double startMillis,double endMillis){
        CutPart cutPart = new CutPart();
        cutPart.setStartTime(startTime);
        cutPart.setEndTime(endTime);
        cutPart.setStartProgress(startProgress);
        cutPart.setStartMillis(startMillis);
        cutPart.setEndMillis(endMillis);
        cutPartObservableList.remove(cutPart);
        CutPartUtil.deleteChildPart(currentSong,cutPart);

    }
    public void createCutPartFile(double startMillis,double endMillis){
        this.startTimeLabel.setText("开始时间");
        this.endTimeLabel.setText("截止时间");
        startCurrentMillis = startMillis;
        endCurrentMillis = endMillis;
        load.requestFocus();
        stage.show();
        stage.setOnCloseRequest(event -> {
            songCreateController.newSongNameText.setText("");
        });
    }
    public void alterMessage(){
        //防止切歌造成的误差
          SongTable tmpSong =  currentSong;
        String message = MessageUtil.getMessage(currentSong);
        TextInputDialog dialog = new TextInputDialog(MessageUtil.getMessage(currentSong));
        dialog.setResizable(true);

        dialog.getDialogPane().setMinWidth(500);
        dialog.getDialogPane().setGraphic(new ImageView(GUI.class.getResource("/").toString() +"/Img/other/message.png"));
        dialog.setTitle("附注");
        dialog.setHeaderText("请在下方文本框输入你想附注的内容");


// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            //如果没修改， 不做变化
            if(result.get().equals( message))  return ;
            if(result.get() == ""){
                MessageUtil.deleteMessage(tmpSong);
            }else{
                MessageUtil.writeMessage(tmpSong,result.get());
            }

            if(tmpSong == currentSong) {
                readMessage(currentSong);
            }
        }

// The Java 8 way to get the response value (with lambda expression).

    }
    public void readMessage(SongTable currentSong){
        String mess = MessageUtil.getMessage(currentSong);

        if(mess != null && mess != ""){
            Message message = new Message();
            message.setMessage(mess);
            messageObservableList.removeAll(messageObservableList);
            messageObservableList.add(message);
            messageTable.setItems(messageObservableList);
        }else{
            messageObservableList.removeAll(messageObservableList);
            messageTable.setItems(messageObservableList);
        }

    }
    public void setSliderVisible(){
      GUI.getBottomController().setSliderVisible();
    }
    public void setSliderNotVisible(){
        GUI.getBottomController().setSliderNotVisible();
    }
    public  void setStartTime(String startTime){
        this.startTimeLabel.setText(startTime);
    }
    public  void setEndTime(String endTime){
        this.endTimeLabel.setText(endTime);
    }
}

