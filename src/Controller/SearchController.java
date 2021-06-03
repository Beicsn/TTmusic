package Controller;

import Entity.MyCheckBox;
import Entity.SongMenu;
import Entity.SongTable;
import Gui.GUI;
import Gui.Popup;
import Util.LocalSongXmlUtil;
import Util.MenuSongXmlUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import service.PlayOperate;
import service.PlayState;


import java.util.ArrayList;

public class SearchController {
    @FXML
    private ContextMenu contextMenu;
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
    private SongTable selectedSongTable;
    @FXML
    private Menu addSong;
    @FXML
    public Label searchLabel;
    public void init(ArrayList<SongTable> songTables){
        tableColumn_Title.setCellValueFactory(new PropertyValueFactory<>("songTitle"));
        tableColumn_Album.setCellValueFactory(new PropertyValueFactory<>("albumName"));
        tableColumn_Singer.setCellValueFactory(new PropertyValueFactory<>("songSinger"));
        tableColumn_Time.setCellValueFactory(new PropertyValueFactory<>("songTime"));
        tableColumn_Size.setCellValueFactory(new PropertyValueFactory<>("songSize"));


        ObservableList<SongTable> songTableList = FXCollections.observableArrayList();
        songTableList.addAll(songTables);
        songTableView.setEditable(true);

        songTableView.setItems(songTableList);
        songTableView.setRowFactory(param -> {
            TableRow<SongTable> row = new TableRow<SongTable>();

            row.setOnMouseClicked(event -> {
                if(!row.isEmpty()){
                    selectedSongTable = row.getItem();
                    this.addSong.setVisible(true);

                }else{
                    this.addSong.setVisible(false);

                }
                if (event.getClickCount() == 2 && (! row.isEmpty()) ){
                    if(PlayState.getPlayState().getCurrentSong()!=null &&row.getItem().getPath().equals(PlayState.getPlayState().getCurrentSong().getPath()) ) {
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
        System.out.println(menuId);
        MenuSongXmlUtil.writeSongToSongList(selectedSongTable,menuId);
        Popup.showTimedDialog(500,"添加成功");
    }
}