package Entity;

import Controller.BottomController;
import Controller.ReadMoreController;
import Gui.GUI;
import javafx.scene.control.Button;

import java.util.Objects;

public class CutPart {
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
    //开始进度
    private double startProgress;
    //结束毫秒
    private double endMillis;
    //开始毫秒
    private double startMillis;
    private Button  broadCast = new Button("  播放  ");
    private Button delete =  new Button("  删除  ");
    private Button create =  new Button("  生成新音频  ");
    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public CutPart(){
       broadCast.setOnAction(event -> {

            ReadMoreController readMoreController = GUI.getBottomController().readMoreController;
            readMoreController.broadCastPart(startTime,startProgress,endMillis);
        });
       delete.setOnAction(event -> {
           ReadMoreController readMoreController = GUI.getBottomController().readMoreController;
           readMoreController.deleteCutPart(startTime,endTime,startProgress,startMillis,endMillis);

       });
       create.setOnAction(event -> {
           ReadMoreController readMoreController = GUI.getBottomController().readMoreController;
           readMoreController.createCutPartFile(startMillis,endMillis);

       });
        broadCast.setStyle("-fx-font-size: 15px");
        broadCast.getStyleClass().set(0,"backgroundButton");
        delete.setStyle("-fx-font-size: 15px");
        delete.getStyleClass().set(0,"backgroundButton");
       create.setStyle("-fx-font-size: 15px");
        create.getStyleClass().set(0,"backgroundButton");
    }

    public Button getCreate() {
        return create;
    }

    public void setCreate(Button create) {
        this.create = create;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }

    public Button getBroadCast() {
        return broadCast;
    }

    public void setBroadCast(Button broadCast) {
        this.broadCast = broadCast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CutPart cutPart = (CutPart) o;
        return Double.compare(cutPart.startProgress, startProgress) == 0 &&
                Double.compare(cutPart.endMillis, endMillis) == 0 &&
                Objects.equals(startTime, cutPart.startTime) &&
                Objects.equals(endTime, cutPart.endTime);
    }

    public double getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(double startMillis) {
        this.startMillis = startMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, startProgress, endMillis, broadCast, delete);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getStartProgress() {
        return startProgress;
    }

    public void setStartProgress(double startProgress) {
        this.startProgress = startProgress;
    }

    public double getEndMillis() {
        return endMillis;
    }

    public void setEndMillis(double endMillis) {
        this.endMillis = endMillis;
    }
}
