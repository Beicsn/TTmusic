package Entity;

import Util.MenuSongXmlUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.util.Callback;
public class MyCheckBox {
   public static class likeCheckBox implements Callback<Integer, ObservableValue<Boolean>> {

        private TableView<SongTable> tv;

        public TableView<SongTable> getTv() {
            return tv;
        }

        public likeCheckBox(TableView<SongTable> tv) {
            this.tv = tv;
        }

        @Override
        public ObservableValue<Boolean> call(Integer param) {
            System.out.println("获取到下标 "+param);
            SongTable songTable = tv.getItems().get(param);
            ObservableValue<Boolean> booleanProperty = songTable.likePropertyProperty();
            if(songTable.getLike().equals("NO")&& songTable.likePropertyProperty().getValue().booleanValue() == true){
                System.out.println("添加到喜欢列表");
                songTable.setLike("YES");
                MenuSongXmlUtil.setSongLike(songTable);
            }
            if(songTable.getLike().equals("YES")&& songTable.likePropertyProperty().getValue().booleanValue() == false){
                System.out.println("取消添加到喜欢");
                songTable.setLike("NO");
                MenuSongXmlUtil.setSongNotLike(songTable);
            }

            return booleanProperty;
        }
    }
}

