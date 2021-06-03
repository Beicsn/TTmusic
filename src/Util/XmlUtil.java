package Util;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class XmlUtil {

    private static String path = XmlUtil.class.getResource("/").getPath();
    private static String fileName = "SongMenu.xml";
    //初始化数据文件
    static {

            createSongMenuDoc();

    }
    public static  Document getSongMenuDoc(){

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
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                String time = format.format(date);
                Element root = document.addElement("songMenu-List");
                 root.addElement("song-menu").addAttribute("songMenuName","本地音频文件").addAttribute("createTime",time).addAttribute("parentId","-1").addAttribute("id","0");

                  root.addElement("song-menu").addAttribute("songMenuName","我喜欢的音乐").addAttribute("createTime",time).addAttribute("parentId","0").addAttribute("id","1");



                writeSongMenuDoc(document);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        public static void writeSongMenuDoc(Document document){
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
        public  static boolean   findSongMenu(int menuParentId,String menuName){
            Document document=XmlUtil.getSongMenuDoc();
            Element element = (Element) document.selectSingleNode("//song-menu[@parentId='"+menuParentId+"'][@songMenuName='"+menuName+"']");

            return  element!=null;

        }
        //删除歌单
        public static void deleteSongMenu(int songMenuId){
            Document songMenuDoc = getSongMenuDoc();
            Node node = songMenuDoc.selectSingleNode("//song-menu[@id='" + songMenuId + "']");
            System.out.println(node);
            Element parent = node.getParent();

            parent.remove(node);
            writeSongMenuDoc(parent.getDocument());
//删除歌单中的歌曲
            MenuSongXmlUtil.removeSongMenu(songMenuId);
        }


}
