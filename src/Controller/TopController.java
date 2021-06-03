package Controller;

import Entity.SongTable;
import Gui.GUI;
import Gui.Popup;
import Util.LocalSongXmlUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.ArrayList;

public class TopController {
    @FXML
    private Button close;
    @FXML
    private TextField searchText;
    public static AnchorPane searchPane;
    public static SearchController searchController;
    private   Stage setUpStage = new Stage();
    @FXML
    public void closeAction(ActionEvent actionEvent){
        Platform.exit();
        System.exit(0);
    }
    @FXML
    public void minimizeAction(ActionEvent actionEvent){
        GUI.getPrimaryStage().setIconified(true);
    }
 public void init() throws IOException {
     FXMLLoader setUpFxml = new FXMLLoader(getClass().getResource("../Gui/SetUp.fxml"));
     AnchorPane setUpPane = setUpFxml.load();
     SetUpController setUpController = setUpFxml.getController();
                     setUpController.init();
     Scene scene = new Scene(setUpPane);
     setUpStage.setScene(scene);
     setUpStage.setTitle("设置");
     setUpStage.getIcons().add(new Image("./Img/other/setting.png"));
     setUpStage.initModality(Modality.APPLICATION_MODAL);
     setUpStage.setResizable(false);

 }

    public Stage getSetUpStage() {
        return setUpStage;
    }

    public void goPrev(ActionEvent actionEvent) {
        if(GUI.TopPane.getLeft() == null) return;
        if(GUI.changePage){
            //如果页面被切换了，换回来
            GUI.TopPane.setRight(GUI.RightPane);
            GUI.changePage = false;
        }


    }
    public void setUpAction(){
        setUpStage.show();
    }
    public void searchSong(ActionEvent actionEvent) throws IOException {
        if(searchText.getText().equals("")){
            Popup.showTimedDialog(500,"请输入关键词");
            return;
        }
        FXMLLoader rightXml = new FXMLLoader(getClass().getResource("../Gui/searchTable.fxml"));
        AnchorPane rightPane = rightXml.load();
        searchPane = rightPane;
        searchController = rightXml.getController();

        ArrayList<SongTable> searchList = LocalSongXmlUtil.getSongTables(searchText.getText());
        searchController.init(searchList);
        searchController.searchLabel.setText("\""+searchText.getText()+"\""+"共搜索到"+searchList.size()+"个音频文件");
        GUI.TopPane.setRight(rightPane);

        System.out.println(searchList.size());
        //页面被切换
        GUI.changePage = true;

    }

    public void goNext(ActionEvent actionEvent) {
        if(GUI.TopPane.getLeft() == null) return;
        if(searchPane!=null && !GUI.changePage) {
            GUI.TopPane.setRight(searchPane);
            GUI.changePage = true;

        }
    }
}
