package Util;

import Controller.TopController;

import java.io.*;
import java.io.ObjectInputStream.GetField;
import java.net.URL;
import java.util.Properties;
import java.util.TreeMap;

public class PropertiesUtil {


    /**
     * 获取属性文件的数据 根据key获取值
     * @param fileName 文件名　(注意：加载的是src下的文件,如果在某个包下．请把包名加上)
     * @param key
     * @return
     */
    public static String getPropertiesKey(String key) {

        try {
            Properties prop = getProperties();
            return prop.getProperty(key);
        } catch (Exception e) {
            return "";
        }

    }



    /**
     * 返回　Properties
     * @param fileName 文件名　(注意：加载的是src下的文件,如果在某个包下．请把包名加上)
     * @param
     * @return
     */
    public static Properties getProperties(){
        Properties prop = new Properties();
        String savePath = PropertiesUtil.class.getResource("/ini.properties").getPath();
        //以下方法读取属性文件会缓存问题
//      InputStream in = TaskController.class
//              .getResourceAsStream("/config.properties");
        try {
            InputStream in =new BufferedInputStream(new FileInputStream(savePath));
            prop.load(in);
        } catch (Exception e) {
            return null;
        }
        return prop;
    }
    /**
     * 写入properties信息
     *
     * @param key
     *            名称
     * @param value
     *            值
     */
    public static void setProperties(String key, String value) {
        try {
            // 从输入流中读取属性列表（键和元素对）
            Properties prop = getProperties();
            prop.setProperty(key, value);
            String path = PropertiesUtil.class.getResource("/ini.properties").getPath();
            FileOutputStream outputFile = new FileOutputStream(path);
            prop.store(outputFile, "music-player");
            outputFile.close();
            outputFile.flush();
        } catch (Exception e) {
        }
    }




}
