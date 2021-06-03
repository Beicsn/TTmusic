package Util;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Entity.SongTable;
import Entity.CutPart;
public class CutPartUtil {
    private static String path =CutPartUtil.class.getResource("/").getPath();
    private static String fileName = "CutPart.xml";
    //初始化数据文件
    static {

        createCutPartDoc();

    }
    public static Document getCutPartDoc(){

        File songMenuXml = new File(path + fileName);
        if(!songMenuXml.exists()){
            createCutPartDoc();
        }
        Document read = null;
        try {
            read  = new SAXReader().read(songMenuXml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return  read;

    }
    public static  void createCutPartDoc(){
        //获取程序当前路径


        File file = new File(path + fileName);
        if (!file.exists()) {

            try {
                file.createNewFile();
                //创建document对象
                Document document = DocumentHelper.createDocument();

                Element root = document.addElement("cutPart");



                writeCutPartDoc(document);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void  writeCutPartDoc(Document document){
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
    public  static boolean   findParentSong(SongTable songTable){
        Document document = getCutPartDoc();
        Element element = (Element) document.selectSingleNode("//song[@path='"+songTable.getPath()+"']");

        return  element!=null;

    }
    //删除某歌曲下的所有子音频
    public static void deleteCutPart(SongTable songTable){
        //找不到，作罢
        if(!findParentSong(songTable)) return ;
        Document cutPartDoc = getCutPartDoc();

        Element element = (Element) cutPartDoc.selectSingleNode("//song[@path='" + songTable.getPath() + "']");
        Element parent = element.getParent();
        parent.remove(element);
        writeCutPartDoc(parent.getDocument());
    }
    //删除子音频
    public static void deleteChildPart(SongTable songTable,CutPart cutPart){
        Document cutPartDoc = getCutPartDoc();
        Node node = cutPartDoc.selectSingleNode("//song[@path='"+songTable.getPath()+"']//children//child[@startProgress='" + cutPart.getStartProgress() + "'][@endMillis='"+cutPart.getEndMillis()+"'][@startMillis='"+cutPart.getStartMillis()+"']");
        Element parent = node.getParent();
        parent.remove(node);
        writeCutPartDoc(parent.getDocument());
    }
    //添加子音频
    public static void addChildPart(SongTable songTable,CutPart cutPart){
        Document cutPartDoc = getCutPartDoc();
        Element rootElement = cutPartDoc.getRootElement();
        if(!findParentSong(songTable)){
            rootElement.addElement("song").addAttribute("path",songTable.getPath())
            .addElement("children");
            ;
        }
        Element element = (Element) rootElement.selectSingleNode("//song[@path='" + songTable.getPath() + "']");
        Element children = element.element("children");
        children.addElement("child").addAttribute("startProgress", String.valueOf(cutPart.getStartProgress())).addAttribute("endMillis", String.valueOf(cutPart.getEndMillis())).addAttribute("startMillis", String.valueOf(cutPart.getStartMillis()));
        Document document = children.getDocument();
        writeCutPartDoc(document);

    }
    public static ArrayList<CutPart> getCutPartList(SongTable songTable){
        Document cutPartDoc = getCutPartDoc();
        Element rootElement = cutPartDoc.getRootElement();
        ArrayList<CutPart>cutParts = new ArrayList<>();
        if(!findParentSong(songTable)) return  cutParts;
        System.out.println("到此");
        Element element =(Element) rootElement.selectSingleNode("//song[@path='" + songTable.getPath() + "']");
        List<Element> elements = element.element("children").elements("child");
        for(Element el :elements){

            String startProgress = el.attributeValue("startProgress");
            String endMillis = el.attributeValue("endMillis");
            String startMillis = el.attributeValue("startMillis");
            double start = Double.parseDouble(startMillis);
            double end = Double.parseDouble(endMillis);
            String startTime =  String.format("%02d:%02d",(int)start/1000/60,(int)start/1000%60);
            String endTime =  String.format("%02d:%02d",(int)end/1000/60,(int)end /1000%60);

            CutPart cutPart = new CutPart();
            cutPart.setStartProgress(Double.parseDouble(startProgress));
            cutPart.setEndTime(endTime);
            cutPart.setStartTime(startTime);
            cutPart.setEndMillis(Double.parseDouble(endMillis));
            cutPart.setStartMillis(Double.parseDouble(startMillis));
            cutParts.add(cutPart);

        }
        return cutParts;
    }

}
