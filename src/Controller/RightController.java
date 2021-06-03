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
        _alert.setTitle("警告");
        _alert.setHeaderText("你确定将《"+selectedSongTable.getSongTitle()+"》从本地磁盘删除吗?");
        _alert.setContentText("该歌曲文件地址和文件名为:" + selectedSongTable.getPath()+",该操作将删除本地磁盘文件并且删除所有歌单中该歌曲信息");
        Optional<ButtonType> result = _alert.showAndWait();
        if(result.get() == ButtonType.OK){
            System.out.println(selectedSongTable.getSongTitle());
            System.out.println(LeftController.currentMenu.getValue().getId());
            //从所有歌单中删除
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
         createTime.setText(LeftController.currentMenu.getValue().getCreateTime()+"创建");


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
                        Popup.showTimedDialog(1000, "这首歌正在播放 ");
                    }else{
                        PlayOperate.playSong(row.getTableView().getItems().get(row.getIndex()));
                        PlayState.getPlayState().setIndex(row.getIndex());
                        PlayState.getPlayState().setSongTableList(songTableList);
                    }


                }
            });
       return row;
        });

        //创建添加歌单的按钮选项
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

        Popup.showTimedDialog(500,"添加成功");
    }


    @FXML
    public void add_music(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("添加音频");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("所有文件","*.*"),
                new FileChooser.ExtensionFilter("mp3格式","*.mp3"),
                new FileChooser.ExtensionFilter("flac格式","*.flac")

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
                            //更新集合中喜爱的音乐
                            if(LeftController.currentMenu.getValue().getId() == 1){
                                localSongTable.get(songMap.get(file.getAbsolutePath())).setLike("YES");
                            }

                        }
                    } catch (Exception e) {
                        Alert _alert = new Alert(Alert.AlertType.INFORMATION);
                        _alert.setTitle("警告");
                        _alert.setHeaderText("有点问题  w(ﾟДﾟ)w");
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
            Popup.showTimedDialog(500,"无歌曲播放");
        }

    }
    public void printSongMenu(){
        SongMenuService.printSongMenu(songMenuName.getText(),createTime.getText(),songTableList);
    }
    public void alterSongTitle(){
        //防止切歌造成的误差
        String songTitle = selectedSongTable.getSongTitle();

        TextInputDialog dialog = new TextInputDialog(songTitle);
        dialog.setResizable(true);

        dialog.getDialogPane().setMinWidth(100);
        dialog.getDialogPane().setGraphic(new ImageView(GUI.class.getResource("/").toString() +"/Img/other/title.png"));
        dialog.setTitle("音乐标题");
        dialog.setHeaderText("请在下方文本框输入你想修改成的音乐标题");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if(result.get().equals(songTitle)) return ;
            if(result.get().equals("")){
                Popup.showTimedDialog(1000,"音乐标题不能为空");
            }else{
                localSongTable.get(songMap.get(selectedSongTable.getPath())).setSongTitle(result.get());
                LocalSongXmlUtil.alterSongTitle(selectedSongTable,result.get());
                setTableView(LeftController.currentMenu.getValue().getId());

            }
        }

// The Java 8 way to get the response value (with lambda expression).
    }
    public void alterSinger(){
        //防止切歌造成的误差
        String songSinger = selectedSongTable.getSongSinger();

        TextInputDialog dialog = new TextInputDialog(songSinger);
        dialog.setResizable(true);

        dialog.getDialogPane().setMinWidth(100);
        dialog.getDialogPane().setGraphic(new ImageView(GUI.class.getResource("/").toString() +"/Img/other/singer.png"));
        dialog.setTitle("歌手名");
        dialog.setHeaderText("请在下方文本框输入你想修改成的歌手名");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if(result.get().equals(songSinger)) return ;
            if(result.get().equals("")){
                Popup.showTimedDialog(1000,"歌手不能为空");
            }else{
                localSongTable.get(songMap.get(selectedSongTable.getPath())).setSongSinger(result.get());
                LocalSongXmlUtil.alterSinger(selectedSongTable,result.get());
                setTableView(LeftController.currentMenu.getValue().getId());

            }
        }

// The Java 8 way to get the response value (with lambda expression).
    }
    public void alterAlbum(){
        //防止切歌造成的误差
        String albumName = selectedSongTable.getAlbumName();

        TextInputDialog dialog = new TextInputDialog(albumName);
        dialog.setResizable(true);

        dialog.getDialogPane().setMinWidth(100);
        dialog.getDialogPane().setGraphic(new ImageView(GUI.class.getResource("/").toString() +"/Img/other/album.png"));
        dialog.setTitle("专辑名");
        dialog.setHeaderText("请在下方文本框输入你想修改成的专辑名");

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if(result.get().equals(albumName)) return ;
            if(result.get().equals("")){
                Popup.showTimedDialog(1000,"专辑名不能为空");
            }else{
                localSongTable.get(songMap.get(selectedSongTable.getPath())).setAlbumName(result.get());
                LocalSongXmlUtil.alterAlbum(selectedSongTable,result.get());
                setTableView(LeftController.currentMenu.getValue().getId());

            }
        }

// The Java 8 way to get the response value (with lambda expression).
    }

}

