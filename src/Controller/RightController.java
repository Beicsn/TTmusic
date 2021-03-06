package Controller;

import Entity.MyCheckBox;
import Entity.Song;
import Entity.SongMenu;
import Entity.SongTable;
import Gui.GUI;
import Gui.Popup;
import Util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import service.PlayOperate;
import service.PlayState;
import service.SongMenuService;
import service.SongOperate;


import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

public class RightController {
    @FXML
    private TableView songTableView;
    @FXML
    private TableColumn<SongTable,Boolean> tableColumn_Like;
    @FXML
    private TableColumn<SongTable,String> tableColumn_Title;
    @FXML
    private TableColumn<SongTable,String> tableColumn_Album;
    @FXML
    private TableColumn<SongTable,String> tableColumn_Singer;
    @FXML
    private TableColumn<SongTable,String> tableColumn_Time;
    @FXML
    private TableColumn<SongTable,String> tableColumn_Size;
    @FXML
    private Label songMenuName;
    @FXML
    private Label createTime;
    @FXML
    private ContextMenu contextMenu;

    private SongTable selectedSongTable;


    @FXML
    private Menu addSong;

    @FXML
    private MenuItem removeSong;
    @FXML
    private  MenuItem removeSongForever;
    @FXML
    private  MenuItem  alterSongTitleItem;
    @FXML
    private MenuItem alterSingerItem;
    @FXML
    private MenuItem alterAlbumItem;

    private  ArrayList<SongTable> localSongTable;
    private HashMap<String,Integer> songMap = new HashMap<>();

    private  ObservableList<SongTable> songTableList = FXCollections.observableArrayList();

    public void setSongTableList(ArrayList<SongTable> songTableList) {
        this.songTableList.setAll(songTableList);
    }

    public ObservableList<SongTable> getSongTableList() {
        return songTableList;
    }



    public void setSongMenuName(String songName) {
        this.songMenuName.setText(songName);
    }

    public void setCreateTime(String createTime) {
        this.createTime.setText(createTime);
    }
    public void deleteSong(){

        System.out.println(selectedSongTable.getSongTitle());
        System.out.println(LeftController.currentMenu.getValue().getId());
        MenuSongXmlUtil.removeSongFromMenu(selectedSongTable,LeftController.currentMenu.getValue().getId());
        this.localSongTable.clear();
        this.songMap.clear();
        this.localSongTable = LocalSongXmlUtil.getSongTableList(songMap);
        songTableList.remove(selectedSongTable);
        contextMenu.hide();
    }
    public void deleteSongForever(){
        Alert _alert = new Alert(Alert.AlertType.CONFIRMATION);
        _alert.setTitle("??????");
        _alert.setHeaderText("???????????????"+selectedSongTable.getSongTitle()+"????????????????????????????");
        _alert.setContentText("????????????????????????????????????:" + selectedSongTable.getPath()+",??????????????????????????????????????????????????????????????????????????????");
        Optional<ButtonType> result = _alert.showAndWait();
        if(result.get() == ButtonType.OK){
            System.out.println(selectedSongTable.getSongTitle());
            System.out.println(LeftController.currentMenu.getValue().getId());
            //????????????????????????
            MenuSongXmlUtil.removeSongFromMenu(selectedSongTable,0);
            SongOperate.deleteSongForever(selectedSongTable);
            this.localSongTable.clear();
            this.songMap.clear();

            this.localSongTable = LocalSongXmlUtil.getSongTableList(songMap);
            songTableList.remove(selectedSongTable);
            contextMenu.hide();
        }

    }
    public void deleteSong(SongTable songTable){

        System.out.println(selectedSongTable.getSongTitle());
        System.out.println(LeftController.currentMenu.getValue().getId());
        MenuSongXmlUtil.removeSongFromMenu(selectedSongTable,LeftController.currentMenu.getValue().getId());
        this.localSongTable.clear();
        this.songMap.clear();
        this.localSongTable = LocalSongXmlUtil.getSongTableList(songMap);
        songTableList.remove(songTable);
        contextMenu.hide();
    }
     public  void  setLocalListLike(SongTable songTable){
              localSongTable.get(songMap.get(songTable.getPath())).setLike("YES");

     }
    public void setTableView(int songMenuId){

        songTableList.clear();
            if(songMenuId == 0)  {
                songTableList.addAll(localSongTable);


            }else{
                ArrayList<String>  songPath = MenuSongXmlUtil.getSongTableList(songMenuId);


                for(String path: songPath){
                    Integer integer = songMap.get(path);
                    songTableList.add(localSongTable.get(integer));
                }
            }


               songTableView.setItems(songTableList);
    }
    public void addMusicToMenuTable(SongTable songTable){
        songTableList.add(songTable);

    }
    public void init(){
         songMenuName.setText(LeftController.currentMenu.getValue().getSongMenuName());
         createTime.setText(LeftController.currentMenu.getValue().getCreateTime()+"??????");


         tableColumn_Title.setCellValueFactory(new PropertyValueFactory<>("songTitle"));
         tableColumn_Album.setCellValueFactory(new PropertyValueFactory<>("albumName"));
         tableColumn_Singer.setCellValueFactory(new PropertyValueFactory<>("songSinger"));
         tableColumn_Time.setCellValueFactory(new PropertyValueFactory<>("songTime"));
         tableColumn_Size.setCellValueFactory(new PropertyValueFactory<>("songSize"));
         tableColumn_Like.setCellValueFactory(new PropertyValueFactory<>("likeProperty"));
         tableColumn_Like.setCellFactory(CheckBoxTableCell.forTableColumn(new MyCheckBox.likeCheckBox(songTableView)));
          this.localSongTable = LocalSongXmlUtil.getSongTableList(songMap);
        songTableList.addAll(localSongTable);
        songTableView.setEditable(true);

         songTableView.setItems(songTableList);

        songTableView.setRowFactory(param -> {
            TableRow<SongTable> row = new TableRow<SongTable>();

            row.setOnMouseClicked(event -> {
                    if(!row.isEmpty()){
                        this.selectedSongTable = row.getItem();
                        this.addSong.setVisible(true);
                        this.removeSong.setVisible(true);
                        this.removeSongForever.setVisible(true);
                        this.alterSongTitleItem.setVisible(true);
                        this.alterSingerItem.setVisible(true);
                        this.alterAlbumItem.setVisible(true);
                    }else{
                        this.addSong.setVisible(false);
                        this.removeSong.setVisible(false);
                        this.removeSongForever.setVisible(false);
                        this.alterSongTitleItem.setVisible(false);
                        this.alterSingerItem.setVisible(false);
                        this.alterAlbumItem.setVisible(false);
                    }
                if (event.getClickCount() == 2 && (! row.isEmpty()) ){
                    if(PlayState.getPlayState().getCurrentSong()!=null &&row.getItem().getPath().equals(PlayState.getPlayState().getCurrentSong().getPath()) ){
                        Popup.showTimedDialog(1000, "????????????????????? ");
                    }else{
                        PlayOperate.playSong(row.getTableView().getItems().get(row.getIndex()));
                        PlayState.getPlayState().setIndex(row.getIndex());
                        PlayState.getPlayState().setSongTableList(songTableList);
                    }


                }
            });
       return row;
        });

        //?????????????????????????????????
        createAddSongMenuItem();
     }
     public void createAddSongMenuItem(){
         System.out.println(addSong.getText());
          addSong.getItems().removeAll(addSong.getItems());
         TreeItem<SongMenu> currentMenu = GUI.getLeftController().getLeftTreeView().getRoot();


         MenuItem menu1 =  isMenu(currentMenu);



         addSong.getItems().addAll(menu1);
        addSong.getItems().size();





     }
    public MenuItem isMenu(TreeItem<SongMenu> songMenu){


                Menu menu = new Menu();
        System.out.println(songMenu.getValue().getSongMenuName());
                menu.setText(songMenu.getValue().getSongMenuName());

                 menu.setOnAction(event -> {
                     MenuItemAction(songMenu.getValue().getId());
                     contextMenu.hide();
                 });
               if(songMenu.getChildren().size()!=0)
                for (TreeItem<SongMenu> children:songMenu.getChildren()){
                    menu.getItems().add(isMenu(children));
                }
                return menu;

            }
    public void MenuItemAction(int menuId){
        System.out.println(selectedSongTable.getPath());
        System.out.println(menuId);


        MenuSongXmlUtil.writeSongToSongList(selectedSongTable,menuId);

        Popup.showTimedDialog(500,"????????????");
    }


    @FXML
    public void add_music(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("????????????");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("????????????","*.*"),
                new FileChooser.ExtensionFilter("mp3??????","*.mp3"),
                new FileChooser.ExtensionFilter("flac??????","*.flac")

        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(GUI.getPrimaryStage());
        ArrayList<SongTable> songList = new ArrayList<>();

        if(selectedFiles != null){
                for(File file : selectedFiles){
                    try {

                        System.out.println(file.getAbsolutePath());
                        Song song = SongOperate.addSong(file.getAbsolutePath(), LeftController. currentMenu.getValue().getId());
                        SongTable st = SongUtil.songToSongTable(song);
                        songList.add(st);
                        if(songMap.get(file.getAbsolutePath()) == null) {
                            songMap.put(file.getAbsolutePath(),localSongTable.size());
                            localSongTable.add(st);
                            if(LeftController.currentMenu.getValue().getId() == 0){
                                songTableList.add(st);
                            }

                        }else{
                            //??????????????????????????????
                            if(LeftController.currentMenu.getValue().getId() == 1){
                                localSongTable.get(songMap.get(file.getAbsolutePath())).setLike("YES");
                            }

                        }
                    } catch (Exception e) {
                        Alert _alert = new Alert(Alert.AlertType.INFORMATION);
                        _alert.setTitle("??????");
                        _alert.setHeaderText("????????????  w(????????)w");
                        _alert.setContentText("[" + file.getAbsolutePath() + "]" + e.getMessage());
                        _alert.show();
                        break;
                    }
                }
            MenuSongXmlUtil.writeSongsToSongList(songList,LeftController.currentMenu.getValue().getId());


        }

    }
    public void broadCastAll(){
        if(songTableList.size()!=0){
            PlayOperate.playSong(songTableList.get(0));
            PlayState.getPlayState().setIndex(0);
            PlayState.getPlayState().setSongTableList(songTableList);
        }else{
            Popup.showTimedDialog(500,"???????????????");
        }

    }
    public void printSongMenu(){
        SongMenuService.printSongMenu(songMenuName.getText(),createTime.getText(),songTableList);
    }
    public void alterSongTitle(){
        //???????????????????????????
        String songTitle = selectedSongTable.getSongTitle();

        TextInputDialog dialog = new TextInputDialog(songTitle);
        dialog.setResizable(true);

        dialog.getDialogPane().setMinWidth(100);
        dialog.getDialogPane().setGraphic(new ImageView(GUI.class.getResource("/").toString() +"/Img/other/title.png"));
        dialog.setTitle("????????????");
        dialog.setHeaderText("?????????????????????????????????????????????????????????");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if(result.get().equals(songTitle)) return ;
            if(result.get().equals("")){
                Popup.showTimedDialog(1000,"????????????????????????");
            }else{
                localSongTable.get(songMap.get(selectedSongTable.getPath())).setSongTitle(result.get());
                LocalSongXmlUtil.alterSongTitle(selectedSongTable,result.get());
                setTableView(LeftController.currentMenu.getValue().getId());

            }
        }

// The Java 8 way to get the response value (with lambda expression).
    }
    public void alterSinger(){
        //???????????????????????????
        String songSinger = selectedSongTable.getSongSinger();

        TextInputDialog dialog = new TextInputDialog(songSinger);
        dialog.setResizable(true);

        dialog.getDialogPane().setMinWidth(100);
        dialog.getDialogPane().setGraphic(new ImageView(GUI.class.getResource("/").toString() +"/Img/other/singer.png"));
        dialog.setTitle("?????????");
        dialog.setHeaderText("??????????????????????????????????????????????????????");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if(result.get().equals(songSinger)) return ;
            if(result.get().equals("")){
                Popup.showTimedDialog(1000,"??????????????????");
            }else{
                localSongTable.get(songMap.get(selectedSongTable.getPath())).setSongSinger(result.get());
                LocalSongXmlUtil.alterSinger(selectedSongTable,result.get());
                setTableView(LeftController.currentMenu.getValue().getId());

            }
        }

// The Java 8 way to get the response value (with lambda expression).
    }
    public void alterAlbum(){
        //???????????????????????????
        String albumName = selectedSongTable.getAlbumName();

        TextInputDialog dialog = new TextInputDialog(albumName);
        dialog.setResizable(true);

        dialog.getDialogPane().setMinWidth(100);
        dialog.getDialogPane().setGraphic(new ImageView(GUI.class.getResource("/").toString() +"/Img/other/album.png"));
        dialog.setTitle("?????????");
        dialog.setHeaderText("??????????????????????????????????????????????????????");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if(result.get().equals(albumName)) return ;
            if(result.get().equals("")){
                Popup.showTimedDialog(1000,"?????????????????????");
            }else{
                localSongTable.get(songMap.get(selectedSongTable.getPath())).setAlbumName(result.get());
                LocalSongXmlUtil.alterAlbum(selectedSongTable,result.get());
                setTableView(LeftController.currentMenu.getValue().getId());

            }
        }

// The Java 8 way to get the response value (with lambda expression).
    }

}

