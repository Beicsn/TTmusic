package Util;

import javafx.scene.control.Alert;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;

public class newTest {
    public static void main(String[] args) {
        try {
            //open file.
            WritableWorkbook book = Workbook.createWorkbook(new File("d:/Test.xls"));

            //create Sheet named "Sheet_1". 0 means this is 1st page.
            WritableSheet sheet = book.createSheet("Sheet_1", 0);

            //define cell column and row in Label Constructor, and cell content write "test".
            //cell is 1st-Column,1st-Row. value is "test".
            String[] title = new String[]{"编号","歌曲名","歌手","专辑"};
            Label label = null;
            for(int i=0;i<title.length;i++){
              label = new Label(i, 0, title[i]);
                sheet.addCell(label);
            }


            //create cell using add numeric. WARN:necessarily use integrated package-path, otherwise will be throws path-error.
            //cell is 2nd-Column, 1st-Row. value is 789.123.

            //add defined cell above to sheet instance.


            //add defined all cell above to case.
            book.write();
            //close file case.
            book.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
