package Util;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;

import Entity.SongTable;


public class LrcUtil {

    private static String path = LrcUtil.class.getResource("/").getPath();
    private static String fileName = "Lrc.xml";
    //初始化数据文件
    static {

            createSongMenuDoc();

    }
    public static  Document getDoc(){

         File songMenuXml = new File(path + fileName);
         if(!songMenuXml.exists()){
            createSongMenuDoc();
         }
        Document read = null;
        try {
           read  = new SAXReader().read(songMenuXml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return  read;

    }
    public static  void createSongMenuDoc(){
        //获取程序当前路径


        File file = new File(path + fileName);
        if (!file.exists()) {

            try {
                file.createNewFile();
                //创建document对象
                Document document = DocumentHelper.createDocument();
                //生成歌单的创建时间


                Element root = document.addElement("lrc-List");





                writeDoc(document);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        public static void writeDoc(Document document){
            XMLWriter xmlWriter =null;
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(path+fileName));
                //使用漂亮格式
                OutputFormat outputFormat = OutputFormat.createPrettyPrint();
                xmlWriter = new XMLWriter(fileOutputStream,outputFormat);
                xmlWriter.write(document);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    xmlWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    public static void  writeLrc(SongTable songTable,String lrcPath){
        Document doc = getDoc();
        Element rootElement = doc.getRootElement();

            if (!findLrc(songTable)){
                Element songElement = rootElement.addElement("lrc");
                songElement.addAttribute("songPath",songTable.getPath()).addAttribute("lrcPath",lrcPath);
            }else{
                Element element = (Element) rootElement.selectSingleNode("//lrc[@songPath='"+songTable.getPath()+"']");

                element.attribute("lrcPath").setValue(lrcPath);
            }
            writeDoc(doc);
        }


        //查找歌词
        public  static boolean   findLrc(SongTable songTable){
            Document document= LrcUtil.getDoc();
            Element element = (Element) document.selectSingleNode("//lrc[@songPath='"+songTable.getPath()+"']");
            return  element!=null;
        }
    //查找歌词
        public  static  String   getLrcPath(SongTable songTable){
        Document document= LrcUtil.getDoc();
        Element element = (Element) document.selectSingleNode("//lrc[@songPath='"+songTable.getPath()+"']");
        return  element ==null ? null : element.attribute("lrcPath").getText();
    }

        //删除歌词
        public static void deleteLrc(SongTable songTable){
        if(!findLrc(songTable)) return ;
            Document doc = getDoc();
            Node node = doc.selectSingleNode("//lrc[@songPath='"+songTable.getPath()+"']");
            System.out.println(node);
            Element parent = node.getParent();

            parent.remove(node);
            writeDoc(parent.getDocument());

        }
    //删除歌词
    public static void deleteLrc(String lrcPath){
        Document doc = getDoc();
        Node node = doc.selectSingleNode("//lrc[@lrcPath='"+lrcPath+"']");
        System.out.println(node);
        Element parent = node.getParent();

        parent.remove(node);
        writeDoc(parent.getDocument());

    }


}
