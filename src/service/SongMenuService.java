package service;

import Controller.LeftController;
import Entity.SongMenu;
import Entity.SongTable;
import Gui.GUI;
import Util.PropertiesUtil;
import Util.XmlUtil;
import com.sun.javaws.jnl.XMLUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.benow.java.rest.XML;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SongMenuService {
    //创建新窗口的界面
     public static Stage stage = new Stage();
     public static    AnchorPane load;
    //映射songMenu在集合中的位置
    public static HashMap<Integer,Integer> songMenuMap = new HashMap<>();
    public static int parentId =-1;
        //返回TreeItem集合
        public ArrayList<TreeItem> getAllSongMenu() {
            Document document = XmlUtil.getSongMenuDoc();
            //获取根节点
            Element root = document.getRootElement();

            List<Element> elements = root.elements("song-menu");
            ArrayList<TreeItem> treeItems = new ArrayList();
            int i=0;

            if (elements != null) {
                for (Element child : elements) {
                    String songMenuName = child.attributeValue("songMenuName");
                    int parentId = Integer.parseInt(child.attributeValue("parentId"));
                    String createTime = child.attributeValue("createTime");
                    int id = Integer.parseInt(child.attributeValue("id"));
                   treeItems.add(new TreeItem( new SongMenu(songMenuName,createTime,parentId,id)));
                   //前面存放id，后面存放在集合中的索引
                   songMenuMap.put(id,i++);

//                    System.out.println(id);
                }
            }
            return treeItems;
        }
        //生成树结构，使得JAVAFX的TreeView能正常使用
        public void createTreeView(ArrayList<TreeItem> treeItems){
            for(TreeItem treeItem : treeItems){
                SongMenu value = (SongMenu) treeItem.getValue();
                int parentId = value.getParentId();
                 //排除根节点
                if(parentId!=-1) {
                    //得到父类TreeItem,加入子类TreeItem
                    treeItems.get(songMenuMap.get(parentId)).getChildren().add(treeItem);
                }
            }

        }
        //获得根节点TreeItem，即获取"本地音频"这个节点
        public TreeItem<TreeItem> getRootTreeItem(ArrayList<TreeItem> treeItems){

            return  treeItems.get(songMenuMap.get(0));
        }
        //设置TreeView的展示格式
        public void  setOutPutData(TreeView treeView){
            treeView.setCellFactory(new Callback<TreeView<SongMenu>, TreeCell<SongMenu>>() {
                @Override
                public TreeCell call(TreeView<SongMenu> param) {
                    TreeCell<SongMenu> cell = new TreeCell<SongMenu>(){


                        @Override
                        protected void updateItem(SongMenu item, boolean empty) {
                            super.updateItem(item, empty);
                            if(empty == false){
                                this.setText(item.getSongMenuName());
                            }else
                                this.setText("");
                        }

                    };
                    return cell;
                }
            });
        }
    //创建右键“创建歌单”点击事件的具体实现方法
    public void createSongMenu() throws  Exception{
        load.requestFocus();
        stage.show();


    }
    //删除歌单的具体实现
    public void deleteSongMenu(int songMenuId){
            XmlUtil.deleteSongMenu(songMenuId);
    }

static {
    FXMLLoader fxmlLoader = new FXMLLoader(SongMenu.class.getResource("../Gui/songMenuCreate.fxml"));

    try {
        load = fxmlLoader.load();
    } catch (IOException e) {
        e.printStackTrace();
    }
    LeftController controller = fxmlLoader.getController();


    Scene scene = new Scene(load);
    stage.setScene(scene);
     stage.initStyle(StageStyle.DECORATED);
    stage.setTitle("创建新歌单");

    stage.getIcons().add(new Image("./Img/Right/songMenuDark.png"));
    stage.initModality(Modality.APPLICATION_MODAL);
    scene.getStylesheets().add("./Css/songMenu.css");
    //不可改变大小
    stage.setResizable(false);
    //关闭监听
    stage.setOnCloseRequest(event -> {
       controller.getTextField().setText("");
    });

    load.requestFocus();
    controller.getTextField().textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(controller.getTextField().getText().length()>0){

                controller.getCreateButton().setDisable(false);

            }else{
                controller.getCreateButton().setDisable(true);
            }
        }

    });;
}
     public static  void printSongMenu(String songMenuName,String createTime, ObservableList<SongTable> songTables){
         String songMenuCatalog = PropertiesUtil.getPropertiesKey("songMenuCatalog");
         if(songMenuCatalog == null) {
             songMenuCatalog = GUI.class.getResource("/").getPath().toString()+"\\songMenu"+"\\";
             PropertiesUtil.setProperties("songMenuCatalog",songMenuCatalog);

         }
         File file = new File(songMenuCatalog);
         if(!file.exists()){
             file.mkdir();
         }
         try {
             //open file.
             WritableWorkbook book = Workbook.createWorkbook(new File(file.getPath()+"/"+songMenuName+".xls"));

             //create Sheet named "Sheet_1". 0 means this is 1st page.
             WritableSheet sheet = book.createSheet(createTime, 0);


             //define cell column and row in Label Constructor, and cell content write "test".
             //cell is 1st-Column,1st-Row. value is "test".
             String[] title = new String[]{"编号","歌曲名","歌手","专辑"};
             jxl.write.Label label = null;
             for(int i=0;i<title.length;i++){
                 label = new Label(i, 0, title[i]);
                 sheet.addCell(label);
             }
             for (int i=1;i<=songTables.size();i++) {
                    label = new Label(0, i,String.valueOf(i) );//编号
                     sheet.addCell(label);
                     label = new Label(1,i,songTables.get(i-1).getSongTitle());//歌曲名
                   sheet.addCell(label);
                    label = new Label(2,i,songTables.get(i-1).getSongSinger()); //歌手
                    sheet.addCell(label);
                    label = new Label(3,i,songTables.get(i-1).getAlbumName());//专辑名
                    sheet.addCell(label);
             }
             sheet.setColumnView(0,20);
             sheet.setColumnView(1, 100);
             sheet.setColumnView(2,50);
             sheet.setColumnView(3,50);
             //create cell using add numeric. WARN:necessarily use integrated package-path, otherwise will be throws path-error.
             //cell is 2nd-Column, 1st-Row. value is 789.123.

             //add defined cell above to sheet instance.


             //add defined all cell above to case.
             book.write();
             //close file case.
             book.close();

             Alert alert = new Alert(Alert.AlertType.INFORMATION);
             alert.setTitle("操作提示");

             alert.setHeaderText("该文件已保存在以下目录"+file.getPath());
             alert.setContentText("文件名为"+songMenuName+".xls");

             alert.showAndWait();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
}
