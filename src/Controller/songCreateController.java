package Controller;

import Gui.GUI;
import Util.PropertiesUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import myorg.jaudiotagger.audio.exceptions.CannotReadException;
import myorg.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import myorg.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import myorg.jaudiotagger.tag.TagException;
import service.PlayOperate;
import service.SongOperate;

import java.io.File;
import java.io.IOException;


public class songCreateController {
    @FXML
    public Label filePlace;
    @FXML
    public TextField newSongNameText;
    @FXML
    public Button createNewSongButton;
    public void buttonCreateSong(){
        String path = PropertiesUtil.getPropertiesKey("cutPartCatalog");
        if(path == null){
            path = GUI.class.getResource("/").getPath().toString()+"\\cutPart\\";

            PropertiesUtil.setProperties("cutPartCatalog",path);
        }
        File file = new File(path);
            //文件夹要是不存在，则新建文件夹
        if(!file.exists()){
            file.mkdir();
        }
        try {
            File oldSong = new File(ReadMoreController.currentSong.getPath());
            File newSong = new File(path+newSongNameText.getText()+".mp3");
            SongOperate.cutSong(oldSong,ReadMoreController.startCurrentMillis,ReadMoreController.endCurrentMillis,newSong);
            ReadMoreController.stage.close();
            newSongNameText.setText("");
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
