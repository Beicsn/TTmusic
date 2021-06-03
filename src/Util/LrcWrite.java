package Util;


import Entity.SongTable;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class LrcWrite {

     public static String writeLrcFile(ArrayList<String> lrcList,SongTable songTable){
         String filePath = PropertiesUtil.getPropertiesKey("lrcCatalog");
         if(filePath==null){
             filePath = LrcWrite.class.getResource("/").getPath() +"\\lrc\\";
             PropertiesUtil.setProperties("lrcCatalog",filePath);
         }

         if(songTable == null || lrcList.size()==0) return null;
         File folder = new File(filePath);
         if(!folder.exists()){
             folder.mkdir();
         }
         String fileName = songTable.getSongTitle();
         File file = new File(filePath+fileName+".lrc");
         while(file.exists()){
             //如果已经存在相同名字的文件名,则生成随机名
             Random random=new Random();
             fileName = fileName+random.nextInt(10);
             file = new File(filePath+fileName+".lrc");
         }
         try {
             OutputStream outputStream = new FileOutputStream(file);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             for(String tmp : lrcList){
                 tmp +=  "\r\n";
                 bufferedOutputStream.write(tmp.getBytes());

             }
             bufferedOutputStream.flush();
             bufferedOutputStream.close();
             outputStream.close();
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }

        return fileName;
     }


}
