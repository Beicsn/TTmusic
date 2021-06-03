package Controller;

import Gui.GUI;
import Util.PropertiesUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class SetUpController {
    @FXML
    private Label songMenuLabel;
    @FXML
    private Label lrcLabel;
    @FXML
    private  Label cutPartLabel;
    public void init(){
        String songMenuCatalog = PropertiesUtil.getPropertiesKey("songMenuCatalog");
        if(songMenuCatalog == null){
            songMenuCatalog = GUI.class.getResource("/").getPath().toString()+"\\songMenu"+"\\";
            PropertiesUtil.setProperties("songMenuCatalog",songMenuCatalog);
        }
        songMenuLabel.setText(songMenuCatalog);
        String cutPartCatalog = PropertiesUtil.getPropertiesKey("cutPartCatalog");
        if(cutPartCatalog == null){
            cutPartCatalog = GUI.class.getResource("/").getPath().toString()+"\\cutPart"+"\\";

            PropertiesUtil.setProperties("cutPartCatalog",cutPartCatalog);
        }

        cutPartLabel.setText(cutPartCatalog);
        String lrcCatalog = PropertiesUtil.getPropertiesKey("lrcCatalog");
        if(lrcCatalog == null){
            lrcCatalog = GUI.class.getResource("/").getPath().toString()+"\\lrc"+"\\";
            PropertiesUtil.setProperties("lrcCatalog",lrcCatalog);
        }
        lrcLabel.setText(lrcCatalog);
    }
    public void setSongMenuCatalog(){

        DirectoryChooser directoryChooser=new DirectoryChooser();
        File file = directoryChooser.showDialog(GUI.setUpStage);
        if(file!=null){
            String path = file.getPath();//选择的文件夹路径
            path = path +"\\";
            System.out.println(path);
            songMenuLabel.setText(path);
            PropertiesUtil.setProperties("songMenuCatalog",path);

        }


    }
    public void setCutPartCatalog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(GUI.setUpStage);
        if (file != null) {
            String path = file.getPath();//选择的文件夹路径
            path = path +"\\";
            System.out.println(path);
            cutPartLabel.setText(path);
            PropertiesUtil.setProperties("cutPartCatalog", path);
            BottomController.readMoreController.songCreateController.filePlace.setText("新的音频文件将保存在:"+path);
        }
    }
    public void setLrcCatalog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(GUI.setUpStage);
        if (file != null) {
            String path = file.getPath();//选择的文件夹路径
            path = path +"\\";
            System.out.println(path);
            lrcLabel.setText(path);
            PropertiesUtil.setProperties("lrcCatalog", path);

        }
    }
}
