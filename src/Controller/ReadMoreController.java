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
    //?????????????????????
    public ExecutorService cutPartService = Executors.newSingleThreadExecutor();
    //???????????????????????????
    public ExecutorService lrcService = Executors.newSingleThreadExecutor();
    //???????????????????????????
    public ExecutorService jsoupService = Executors.newSingleThreadExecutor();
    public static songCreateController songCreateController;
    public static Stage stage = new Stage();
    public static AnchorPane load;
    public static SongTable currentSong;
    public LrcInfo lrcInfo;
    private UUID lrcOverThread;
    private UUID cutOverThread;
    //????????????
    public ObservableList<Lrc> lrcObservableList = FXCollections.observableArrayList();
    //?????????
    public ObservableList<Message> messageObservableList = FXCollections.observableArrayList();
    //?????????
    public ObservableList<CutPart> cutPartObservableList = FXCollections.observableArrayList();

    //??????????????????
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
        if (startTimeLabel.getText().equals("????????????")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("????????????");
            alert.setHeaderText("?????????????????????????????????");
            alert.setContentText("????????????????????????????????????");
            alert.showAndWait();
            return;
        }
        if (endTimeLabel.getText().equals("????????????")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("????????????");
            alert.setHeaderText("?????????????????????????????????");
            alert.setContentText("????????????????????????????????????");
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
                            //?????????????????????
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
                            System.out.println("????????????:" + startCurrentMillis);
                            System.out.println("????????????:" + endCurrentMillis);
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

        //????????????
//        stage.setOnCloseRequest(event -> {
//            controller.getTextField().setText("");
//        });
        if (startTimeLabel.getText().equals("????????????")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("????????????");
            alert.setHeaderText("?????????????????????????????????");
            alert.setContentText("????????????????????????????????????");
            alert.showAndWait();
            return;
        }
        if (endTimeLabel.getText().equals("????????????")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("????????????");
            alert.setHeaderText("?????????????????????????????????");
            alert.setContentText("????????????????????????????????????");
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
            songCreateController.filePlace.setText("??????????????????????????????:" + PropertiesUtil.getPropertiesKey("cutPartCatalog"));
            Scene scene = new Scene(load);
            stage.setScene(scene);
            stage.setTitle("????????????????????????");

            stage.getIcons().add(new Image("./Img/other/music.png"));
            stage.initModality(Modality.APPLICATION_MODAL);
            scene.getStylesheets().add("./Css/songMenu.css");
            //??????????????????
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
            this.album.setText("??????:" + currentSong.getAlbumName());
            this.singer.setText("??????:" + currentSong.getSongSinger());
            this.songName.setText("??????:" + currentSong.getSongTitle());
            this.songMenu.setText("??????:" + songMenu);
            this.startTimeLabel.setText("????????????");
            this.endTimeLabel.setText("????????????");
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

    //??????????????????
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

    //??????????????????
    public void deleteLrc() {
        if (lrcTable.getItems() == null) {
            Popup.showTimedDialog(500, "???????????????,????????????");
            return;
        }

        lrcTable.setItems(null);
        String lrcPath = LrcUtil.getLrcPath(currentSong);


        LrcUtil.deleteLrc(currentSong);
        if(lrcPath !=null){
            Platform.runLater(() -> {
                Alert _alert = new Alert(Alert.AlertType.CONFIRMATION);
                _alert.setTitle("??????");
                _alert.setHeaderText("?????????,?????????????????????????????????????????????????");
                _alert.setContentText("????????????????????????:" + lrcPath+",???????????????????????????????????????????????????");
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
        fileChooser.setTitle("??????????????????");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("lrc??????", "*.lrc"),
                new FileChooser.ExtensionFilter("txt??????", "*.txt")

        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(GUI.getPrimaryStage());
        if (selectedFiles != null) {
            if (selectedFiles.size() != 1) {
                Popup.showTimedDialog(500, "????????????????????????????????????");
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
                                Popup.showTimedDialog(1000, "???????????????");
                                return;
                            }
                            lrcObservableList.removeAll(lrcObservableList);
                            lrcColumn.setCellValueFactory(new PropertyValueFactory<>("currentContent"));
                            lrcObservableList.addAll(infos);
                            lrcTable.setItems(lrcObservableList);
                            scrollLrc();
                            Popup.showTimedDialog(500, "????????????");
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
                        Popup.showTimedDialog(1000, "?????????????????????");
                    });
                } else {
                    String fileName = LrcWrite.writeLrcFile(LrcJsoup.lrcList, currentSong);

                    if (LrcUtil.findLrc(currentSong)) {
                        Platform.runLater(() -> {
                        Alert _alert = new Alert(Alert.AlertType.CONFIRMATION);
                        _alert.setTitle("??????");
                        _alert.setHeaderText("?????????" + currentSong.getSongTitle() + "??????????????????????????????");
                        _alert.setContentText("??????????????????????????????:" + LrcUtil.getLrcPath(currentSong));
                        Optional<ButtonType> result = _alert.showAndWait();

                        if (result.get() == ButtonType.OK) {

                            LrcUtil.writeLrc(currentSong, PropertiesUtil.getPropertiesKey("lrcCatalog") + fileName + ".lrc");
                            lrcObservableList.removeAll(lrcObservableList);
                            lrcColumn.setCellValueFactory(new PropertyValueFactory<>("currentContent"));
                            lrcObservableList.addAll(infos);
                            lrcTable.setItems(lrcObservableList);
                            scrollLrc();

                        } else if (result.get() == ButtonType.CANCEL) {
                            System.out.println("??????");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("??????");
                            alert.setHeaderText("?????????????????????????????????????????????");
                            alert.setContentText("??????????????????????????????:" + PropertiesUtil.getPropertiesKey("lrcCatalog") + fileName + ".lrc");
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
                            Popup.showTimedDialog(1000, "????????????????????????????????????????????????");
                        });

                    }

                }

        });


    }
    public void addCutPart(){
        if (startTimeLabel.getText().equals("????????????")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("????????????");
            alert.setHeaderText("?????????????????????????????????");
            alert.setContentText("????????????????????????????????????");
            alert.showAndWait();
            return;
        }
        if (endTimeLabel.getText().equals("????????????")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("????????????");
            alert.setHeaderText("?????????????????????????????????");
            alert.setContentText("????????????????????????????????????");
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
            System.out.println("????????????");
            cutPartObservableList.add(cutPart);
            CutPartUtil.addChildPart(currentSong,cutPart);
        }else{
            Popup.showTimedDialog(1000,"?????????,??????????????????");
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
                            //?????????????????????
                            if(id!=cutOverThread){
                                this.cancel();
                            }

                            if(isCancelled()){
                                break;
                            }
                            Platform.runLater(() -> {
                                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
                            System.out.println("????????????:" + startTime);
                            System.out.println("????????????:" + endMillis);
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
        this.startTimeLabel.setText("????????????");
        this.endTimeLabel.setText("????????????");
        startCurrentMillis = startMillis;
        endCurrentMillis = endMillis;
        load.requestFocus();
        stage.show();
        stage.setOnCloseRequest(event -> {
            songCreateController.newSongNameText.setText("");
        });
    }
    public void alterMessage(){
        //???????????????????????????
          SongTable tmpSong =  currentSong;
        String message = MessageUtil.getMessage(currentSong);
        TextInputDialog dialog = new TextInputDialog(MessageUtil.getMessage(currentSong));
        dialog.setResizable(true);

        dialog.getDialogPane().setMinWidth(500);
        dialog.getDialogPane().setGraphic(new ImageView(GUI.class.getResource("/").toString() +"/Img/other/message.png"));
        dialog.setTitle("??????");
        dialog.setHeaderText("????????????????????????????????????????????????");


// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            //?????????????????? ????????????
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

