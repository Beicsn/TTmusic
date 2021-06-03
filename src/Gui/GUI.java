package Gui;

import Controller.*;
import Util.XmlUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

public class GUI extends Application {
    //主窗口
    private static Stage primaryStage;

    private static RightController rightController;
    private static LeftController leftController;
    private static TopController topController;
    private static BottomController bottomController;

    public static BorderPane TopPane;
    public static TreeView LeftPane;
    public static  Stage setUpStage;
    //右边的布局是否被切换，默认无
    public static boolean changePage = false;
    //鼠标在X轴上的偏移量
    private double xOffset = 0;
    //鼠标在Y轴上的偏移量
    private double yOffset = 0;
    public static StackPane RightPane;

    public static TopController getTopController() {
        return topController;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    public static RightController getRightController() {
        return rightController;
    }

    public static LeftController getLeftController() {
        return leftController;
    }

    public static BottomController getBottomController() {
        return bottomController;
    }

    public static void setBottomController(BottomController bottomController) {
        GUI.bottomController = bottomController;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        new XmlUtil();
        this.primaryStage =  primaryStage;

        FXMLLoader topFxml = new FXMLLoader(getClass().getResource("top.fxml"));
        BorderPane topPane = topFxml.load();
         topController = topFxml.getController();

        TopPane = topPane;


        FXMLLoader leftXml = new FXMLLoader(getClass().getResource("left.fxml"));
        TreeView leftPane = leftXml.load();
        LeftPane = leftPane;
        leftController = leftXml.getController();

        FXMLLoader rightXml = new FXMLLoader(getClass().getResource("right.fxml"));
        StackPane rightPane = rightXml.load();
        RightPane = rightPane;
        rightController = rightXml.getController();

        FXMLLoader bottomXml = new FXMLLoader(getClass().getResource("bottom.fxml"));
        AnchorPane bottomPane = bottomXml.load();

        bottomController = bottomXml.getController();
//        FXMLLoader playListLoader = new FXMLLoader(getClass().getResource("PlayListPage.fxml"));
//         AnchorPane playListPane = playListLoader.load();
//        rightPane.getChildren().add(playListPane);
        leftController.initData();
        setUpStage = topController.getSetUpStage();
        rightController.init();
        bottomController.init();
        topController.init();
        topPane.setLeft(leftPane);
        topPane.setRight(rightPane);
        topPane.setBottom(bottomPane);


        topPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        topPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
        Scene scene = new Scene(topPane,1300,850);

        //使css文件载入
//        AnchorPane ac = new AnchorPane();
//        TreeItem treeItem = new TreeItem<>("根目录");
//        treeItem.setExpanded(true);
//
//        for(int i = 0;i < 5;i++){
//            TreeItem item = new TreeItem<>("节点:" + i);
//            TreeItem item1 = new TreeItem(2);
//            item.getChildren().add(item1);
//            treeItem.getChildren().add(item);
//        }
//        TreeView treeView = new TreeView(treeItem);
//        ContextMenu contextMenu = new ContextMenu();
//        MenuItem menuItem = new MenuItem("增加");
//        contextMenu.getItems().add(menuItem);
//        treeView.setContextMenu(contextMenu);
//        ac.getChildren().add(treeView);
//        load.setLeft(ac);
        ArrayList<String> cssList = new ArrayList<>();
        cssList.add("./Css/Top.css");
        cssList.add("./Css/Bottom.css");
        cssList.add("./Css/Right.css");
        cssList.add("./Css/readMore.css");
        cssList.add("./Css/left.css");
        scene.getStylesheets().addAll(cssList);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.getIcons().add(new Image("./Img/Top/logoDark.png"));

        System.out.println(GUI.class.getResource("/").getPath());
    }
}
