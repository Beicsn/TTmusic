package Util;

import Entity.SongTable;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;


public class MessageUtil {

    private static String path = MessageUtil.class.getResource("/").getPath();
    private static String fileName = "Message.xml";
    //初始化数据文件
    static {

            createMessageDoc();

    }
    public static  Document getDoc(){

         File songMenuXml = new File(path + fileName);
         if(!songMenuXml.exists()){
            createMessageDoc();
         }
        Document read = null;
        try {
           read  = new SAXReader().read(songMenuXml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return  read;

    }
    public static  void createMessageDoc(){
        //获取程序当前路径


        File file = new File(path + fileName);
        if (!file.exists()) {

            try {
                file.createNewFile();
                //创建document对象
                Document document = DocumentHelper.createDocument();
                //生成歌单的创建时间


                Element root = document.addElement("message-List");





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
    public static void  writeMessage(SongTable songTable,String message){
        Document doc = getDoc();
        Element rootElement = doc.getRootElement();

            if (!findMessage(songTable)){
                Element songElement = rootElement.addElement("message");
                songElement.addAttribute("songPath",songTable.getPath());
                songElement.setText(message);
            }else{
                Element element = (Element) rootElement.selectSingleNode("//message[@songPath='"+songTable.getPath()+"']");

                element.setText(message);
            }
            writeDoc(doc);
        }


        //查找message
        public  static boolean   findMessage(SongTable songTable){
            Document document= MessageUtil.getDoc();
            Element element = (Element) document.selectSingleNode("//message[@songPath='"+songTable.getPath()+"']");
            return  element!=null;
        }
    //得到message
        public  static  String   getMessage(SongTable songTable){
        Document document= MessageUtil.getDoc();
        Element element = (Element) document.selectSingleNode("//message[@songPath='"+songTable.getPath()+"']");
        return  element ==null ? null : element.getText();
    }
    //删除message
    public  static  void   deleteMessage(SongTable songTable){
        Document document= MessageUtil.getDoc();
        Element element = (Element) document.selectSingleNode("//message[@songPath='"+songTable.getPath()+"']");
        Element parent = element.getParent();
        parent.remove(element);
        writeDoc(parent.getDocument());
    }




}
