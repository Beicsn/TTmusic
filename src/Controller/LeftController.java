package Controller;

import Entity.Song;
import Entity.SongMenu;
import Gui.GUI;
import Util.PropertiesUtil;
import Util.XmlUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.dom4j.Document;
import org.dom4j.Element;
import service.GlobalVariable;
import service.SongMenuService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LeftController implements  Controller{
    @FXML
    private   TreeView leftTreeView;
    private  static  ArrayList<TreeItem> allSongMenu;
    @FXML
    private ContextMenu leftContextMenu;
    @FXML
    private   TextField Text_newSongMenu;
    @FXML
    private  Button Button_create;
    @FXML
    private MenuItem createMenuItem;
    @FXML
    private  MenuItem deleteMenuItem;
    @FXML
    public static TreeItem<SongMenu>   currentMenu;

    //歌单列表的实现类
    private SongMenuService songMenuService = new SongMenuService();
    public  void initData(){
        //获取歌单集合并且转换成TreeItem格式
        this.allSongMenu = songMenuService.getAllSongMenu();
        //创建左侧歌单菜单栏
         songMenuService.createTreeView(allSongMenu);
         //设置菜单栏的展示形式
         songMenuService.setOutPutData(leftTreeView);
         this.leftTreeView.setRoot(songMenuService.getRootTreeItem(allSongMenu));
         //当前歌单，默认为本地音频
         currentMenu =  allSongMenu.get(SongMenuService.songMenuMap.get(0));
        System.out.println( currentMenu.getValue().getId());
        leftTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> {
            if(observable!=null) {
                 if(GUI.changePage){
                     GUI.TopPane.setRight(GUI.RightPane);
                     GUI.changePage = false;
                 }
                 createMenuItem.setVisible(true);
                 deleteMenuItem.setVisible(true);


                this. currentMenu = (TreeItem) observable.getValue();
                System.out.println("当前的树节点id是"+currentMenu.getValue().getId());
                if(currentMenu.getChildren().size()!=0 || currentMenu.getValue().getId() ==0 || currentMenu.getValue().getId()==1){
                    deleteMenuItem.setDisable(true);
                }else{
                     deleteMenuItem.setDisable(false);
                }
                RightController rightController = GUI.getRightController();
                if(oldValue != newValue) {

                    rightController.setCreateTime(currentMenu.getValue().getCreateTime() +"创建");
                    rightController.setSongMenuName(currentMenu.getValue().getSongMenuName());
                    rightController.setTableView(currentMenu.getValue().getId());
                }
//                SongMenu songMenu = (SongMenu) treeItem.getValue();
//                System.out.println(songMenu.getParentId());
//                currentSongMenuId = songMenu.getId();


            }else{
                createMenuItem.setVisible(false);
                deleteMenuItem.setVisible(false);

            }
        });

    }

    //右键删除歌单点击事件
    public void deleteSongMenu(){
        System.out.println("删除的是"+currentMenu.getValue().getId());
        songMenuService.deleteSongMenu(currentMenu.getValue().getId());
        currentMenu.getParent().getChildren().remove(currentMenu);
        GUI.getRightController().createAddSongMenuItem();
        leftContextMenu.hide();

    }
    @FXML
    //创建右键“创建歌单”点击事件
    public void createSongMenu() throws  Exception {

        songMenuService.createSongMenu();
        leftContextMenu.hide();
    }

    public TreeView getLeftTreeView() {
        return leftTreeView;
    }

    public TextField getTextField(){
        return  this.Text_newSongMenu;
    }
    public Button  getCreateButton(){
        return  this.Button_create;
    }
    @FXML
    public void buttonCreateMenu(){
        String songMenuName =   this.Text_newSongMenu.getText();
        int  parentId = currentMenu.getValue().getId();
        System.out.println( currentMenu.getValue().getCreateTime());
        if(XmlUtil.findSongMenu(parentId,songMenuName)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("错误提示");
            alert.setHeaderText("该目录下已存在同名歌单");
            alert.setContentText("换个歌单名试试");

            alert.showAndWait();
              return ;
        }
        Document songMenuDoc = XmlUtil.getSongMenuDoc();
        int songMenuId = Integer.parseInt(PropertiesUtil.getPropertiesKey("songMenuId"));

        //生成歌单的创建时间
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        String time = format.format(date);
        TreeItem<SongMenu> songMenuTreeItem = new TreeItem<>(new SongMenu(songMenuName,time,parentId, songMenuId));
        currentMenu.getChildren().add(songMenuTreeItem);
        allSongMenu.add(songMenuTreeItem);
        SongMenuService.songMenuMap.put(songMenuId,allSongMenu.size()-1);
        SongMenuService.stage.close();
        this.Text_newSongMenu.setText("");
        songMenuDoc.getRootElement().addElement("song-menu").addAttribute("songMenuName",songMenuName).addAttribute("createTime",time).addAttribute("parentId", String.valueOf( currentMenu.getValue().getId())).addAttribute("id", String.valueOf(songMenuId++));
            XmlUtil.writeSongMenuDoc(songMenuDoc);
            PropertiesUtil.setProperties("songMenuId", String.valueOf(songMenuId));
        GUI.getRightController().createAddSongMenuItem();
        if( GUI.getTopController().searchController!=null)
        GUI.getTopController().searchController.createAddSongMenuItem();
    }






}
//  root = new TreeItem<>(new SongMenu("本地音频",-1,0));
//        TreeItem leave1 = new TreeItem(new SongMenu("1",0,1));
//        TreeItem leave2 = new TreeItem(new SongMenu("2",1,2));
//        TreeItem leave3 = new TreeItem(new SongMenu("3",2,3));
//        TreeItem leave4 = new TreeItem(new SongMenu("4",1,4));
//
//        ArrayList<TreeItem> treeItems = new ArrayList<>();
//        treeItems.add(root);
//        //前面存放的是自己的id，后面存放的是自己在数组中的索引
//        songMenuMap.put(0,0);
//        treeItems.add(leave1);
//        songMenuMap.put(1,1);
//        treeItems.add(leave2);
//        songMenuMap.put(2,2);
//        treeItems.add(leave3);
//        songMenuMap.put(3,3);
//        treeItems.add(leave4);
//        songMenuMap.put(4,4);
//        for(int i= treeItems.size()-1;i>=0;i--){
//        SongMenu value =(SongMenu)treeItems.get(i).getValue();
//        System.out.println(value.getParentId());
//        if(value.getParentId()!=-1){
//        treeItems.get((Integer) songMenuMap.get(value.getParentId())).getChildren().add(treeItems.get(i));
//        }
//        }
//
//
//
//
//
//        this.leftTreeView.setRoot(treeItems.get((Integer) songMenuMap.get(0)));