package service;

import Entity.Lrc;
import Entity.LrcInfo;
import Entity.SongTable;

import Gui.GUI;
import Gui.Popup;
import Util.LrcParser;
import Util.LrcUtil;
import Util.LrcWrite;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;


public class LrcJsoup {
    public static SongTable currentSong;
    public static ArrayList<String> lrcList = new ArrayList<>();
    public static LrcInfo lrcInfo = new LrcInfo();

    public static LrcInfo getLrcFromInternet(SongTable songTable) {

        LrcParser lrcParser = new LrcParser();
        if (currentSong == null || currentSong != songTable) {
            currentSong = songTable;
            lrcList.removeAll(lrcList);

            try {

                Connection.Response res = Jsoup.connect("https://www.22lrc.com/search.php?keyword=" + songTable.getSongTitle() + "&radio=0")
                        .method(Connection.Method.GET)
                        .execute();
                String body = res.body();
//            System.out.println(body);

                Document doc = Jsoup.parse(body);

                Elements selects = doc.select("table[cellspacing=\"0\"]").select("tr").select("td[class=\"gm\"]");
                for (Element element : selects) {
                    Element parent = element.parent();

                    if (songTable.getSongTitle().equals(parent.select("td").get(0).select("a").text()) && songTable.getSongSinger().equals(parent.select("td").get(2).select("a").text())) {
                        String href = parent.select("a[title=\"查看歌词\"]").attr("href");
                        Connection.Response xx = Jsoup.connect("https://www.22lrc.com/" + href)
                                .method(Connection.Method.GET)
                                .execute();
                        body = xx.body();


                        doc = Jsoup.parse(body);
                        Element select = doc.select("article[id=\"lrc\"]").get(0);
                        Elements p = select.select("p");
                        String s = p.toString();
                        s = s.substring(3);
                        s = s.substring(0, s.length() - 4);
                        String[] split = s.split("<br>");
                        for (String tmp : split) {
                            lrcParser.parserLine(tmp);
                            lrcList.add(tmp);

                        }
                        lrcParser.getLrcinfo().setInfos((ArrayList<Lrc>) lrcParser.getList());

                    }


                }

            } catch (IOException e) {
                Platform.runLater(() -> {
                    System.out.println("无网络,请检查你的互联网连接");
                });

            } catch (Exception e) {
                    e.printStackTrace();
            }
            lrcInfo = lrcParser.getLrcinfo();
        }
        return lrcInfo;
    }

    public static LrcInfo downLoadLrc(SongTable songTable) {

        if (currentSong != songTable) {
            getLrcFromInternet(songTable);
        }

                return  lrcInfo;
    }


}