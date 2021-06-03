package Gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Popup {
    //弹框提示设置,无关闭按钮，自动关闭
    public static void showTimedDialog(long time, String message) {
        Stage popup = new Stage();
        popup.setAlwaysOnTop(true);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: black");
        root.setAlignment(Pos.BASELINE_CENTER);
        root.setSpacing(20);
        Label label = new Label(message);
         label.setStyle("-fx-text-fill: white");
        root.getChildren().addAll(label);
        Scene scene = new Scene(root);
        popup.setScene(scene);
        popup.setTitle("提示信息");
        popup.show();

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(time);
                if (popup.isShowing()) {
                    Platform.runLater(() -> popup.close());
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}
