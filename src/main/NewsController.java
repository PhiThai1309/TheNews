package main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NewsController {
    private final ArrayList<Item> items = new ArrayList<>();
    private int categoryIndex = 0;

    public ArrayList<Item> getItems(){
        return this.items;
    }
    public NewsController(int categoryIndex) {
        this.categoryIndex = categoryIndex;
        String rssVE = "https://vnexpress.net/rss/tin-moi-nhat.rss";
        String rssTuoiTre = "https://tuoitre.vn/rss/tin-moi-nhat.rss";
        String rssThanhNien = "https://thanhnien.vn/rss/home.rss";
        String zing = "https://zingnews.vn/";
        String nhanDan = "https://nhandan.vn";

        /*new Thread(() -> readRSSVe(rssVE)).start();
        new Thread(() -> readRSSTuoiTre(rssTuoiTre)).start();
        new Thread(() -> readRSSThanhNien(rssThanhNien)).start();
        new Thread(() -> readZing(zing)).start();
        new Thread(() -> readNhanDan(nhanDan)).start();*/

        readRSSVe(rssVE);
        System.out.println(items.size());

        readRSSTuoiTre(rssTuoiTre);
        System.out.println(items.size());

        readRSSThanhNien(rssThanhNien);
        System.out.println(items.size());

        readZing(zing);
        System.out.println(items.size());

        readNhanDan(nhanDan);
        System.out.println(items.size());
        Collections.sort(items);
    }

    public void readRSSVe(String urlAddress){
        try{
            URL rssURL = new URL(urlAddress);
            BufferedReader in = new BufferedReader(new InputStreamReader(rssURL.openStream()));
            String title = "", pubDate = "", link = "", imgSrc = "", line;
            LocalDateTime date = LocalDateTime.MIN;
            boolean inItem = false;

            while ((line = in.readLine()) != null){
                if (line.contains("<item>")){
                    inItem = true;
                }
                else if (line.contains("<title>") && inItem){
                    title = extract(line, "<title>", "</title>");
                }
                else if (line.contains("<pubDate>") && inItem){
                    pubDate = extract(line, "<pubDate>", "</pubDate>");
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss +0700");
                    date = LocalDateTime.parse(pubDate, df);
                }
                else if (line.contains("<link>") && inItem){
                    link = extract(line, "<link>", "</link>");
                }
                else if (line.contains("<description>") && inItem){
                    try{
                        imgSrc = extract(line, "<description>", "</description>");
                        imgSrc = extract(imgSrc, "<img src=\"", "\"");
                    }catch (StringIndexOutOfBoundsException e){
                        imgSrc = "";
                    }
                }
                else if (line.contains("</item>") && inItem){
                    inItem = false;
                    Item item = new Item(title, link, date, imgSrc, Source.VE);
                    items.add(item);
                }
            }

            in.close();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("Can't connect to " + urlAddress);
        }
    }

    public void readRSSTuoiTre(String urlAddress){
        try{
            URL rssURL = new URL(urlAddress);
            BufferedReader in = new BufferedReader(new InputStreamReader(rssURL.openStream()));
            String title = "", pubDate = "", link = "", imgSrc = "", line;
            LocalDateTime date = LocalDateTime.MIN;
            boolean inItem = false;

            while ((line = in.readLine()) != null){
                if (line.contains("<item>")){
                    inItem = true;
                }
                else if (line.contains("<title>") && inItem){
                    title = extract(line, "<title>", "</title>");
                    title = extract(title, "<![CDATA[", "]]>");
                }
                else if (line.contains("<pubDate>") && inItem){
                    pubDate = extract(line, "<pubDate>", "</pubDate>");
                    pubDate = extract(pubDate, "<![CDATA[", " GMT+7");
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss");
                    date = LocalDateTime.parse(pubDate, df);
                }
                if (line.contains("<link>") && inItem){
                    link = extract(line, "<link>", "</link>");
                    link = extract(link, "<![CDATA[", "]]>");
                }
                else if (line.contains("<description>") && inItem){
                    try{
                        imgSrc = extract(line, "<description>", "</description>");
                        imgSrc = extract(imgSrc, "<img src=\"", "\"");
                        imgSrc = imgSrc.replace("zoom/80_50/", "");
                    }catch (StringIndexOutOfBoundsException e){
                        imgSrc = "";
                    }
                }
                else if (line.contains("</item>") && inItem){
                    inItem = false;
                    Item item = new Item(title, link, date, imgSrc, Source.TT);
                    items.add(item);
                }
            }

            in.close();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("Can't connect to " + urlAddress);
        }
    }

    public void readRSSThanhNien(String urlAddress) {
        try{
            URL rssURL = new URL(urlAddress);
            BufferedReader in = new BufferedReader(new InputStreamReader(rssURL.openStream()));
            String title = "", pubDate = "", link = "", imgSrc = "", line;
            LocalDateTime date = LocalDateTime.MIN;
            boolean inItem = false;

            while ((line = in.readLine()) != null){
                if (line.contains("<item>"))
                    inItem = true;

                if (inItem){
                    try{
                        // Extract title
                        title = extract(line, "<title>", "</title>");
                        if (title.contains("<![CDATA[ "))
                            title = extract(title, "<![CDATA[ ", "]]>");

                        // Extract link
                        link = extract(line, "<link>", "</link>");

                        // Extract pubDate
                        pubDate = extract(line, "<pubDate>", " GMT");
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss");
                        date = LocalDateTime.parse(pubDate, df);

                        // Extract image
                        imgSrc = extract(line, "<image>", "</image>");

                        Item item = new Item(title, link, date, imgSrc, Source.TN);
                        items.add(item);
                        inItem = false;
                    }
                    catch (StringIndexOutOfBoundsException e){

                    }
                }
            }

            in.close();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("Can't connect to " + urlAddress);
        }
    }

    public void readZing(String urlAddress) {
        try{
            Document doc = Jsoup.connect(urlAddress).get();
            Elements body = doc.select("section[id~=.*-latest]");
            Elements featured = doc.select("section[id~=.*-featured]");
            body.addAll(featured);

            String title = "", pubDate = "", link = "", imgSrc = "";
            LocalDateTime date = LocalDateTime.MIN;

            for (Element e : body.select("article.article-item")){
                // Get image source
                imgSrc = e.select("img").attr("src");
                if (!imgSrc.contains("https")) imgSrc = e.select("img").attr("data-src");

                // Get title and link
                title = e.getElementsByClass("article-title").text();
                link = e.select("a").attr("href");
                link = "https://zingnews.vn" + link;

                // Get published date
                pubDate = e.select("span.time").text();
                pubDate += " " + e.select("span.date").text();
                pubDate = pubDate.trim();
                if (pubDate.compareTo("") == 0) pubDate = e.select("span.friendly-time").text();
                DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm d/M/yyyy");
                date = LocalDateTime.parse(pubDate, df);

                // Create and add news item to list
                Item item = new Item(title, link, date, imgSrc, Source.ZING);
                items.add(item);
            }
        }
        catch (IOException e){
            System.out.println("Can't connect to " + urlAddress);
        }
    }

    public void readNhanDan(String urlAddress) {
        try{
            Document doc = Jsoup.connect(urlAddress).get();
            Elements body = doc.getElementsByClass("swrapper");

            String title = "", pubDate = "", link = "", imgSrc = "";

            for (Element e : body.select("article")){
                LocalDateTime date = LocalDateTime.MIN;

                // Get title
                title = e.getElementsByClass("box-title").text();
                if (title.compareTo("") == 0) continue;

                // Get image source
                imgSrc = e.select("img").attr("data-src");

                // Get link
                link = e.select("a").attr("href");
                if (!link.contains("https://nhandan.vn")) link = "https://nhandan.vn" + link;

                // Get pubDate
                pubDate = e.getElementsByClass("box-meta-small").text();
                if (pubDate.compareTo("") != 0){
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm dd/M/yyyy");
                    date = LocalDateTime.parse(pubDate, df);
                }

                // Create and add news item to list
                Item item = new Item(title, link, date, imgSrc, Source.ND);
                items.add(item);
            }
        }
        catch (IOException e){
            System.out.println("Can't connect to " + urlAddress);
        }
    }

    public String extract(String line, String start, String end){
        int firstPos = line.indexOf(start);
        String temp = line.substring(firstPos);
        temp = temp.replace(start, "");
        int lastPos = temp.indexOf(end);
        temp = temp.substring(0, lastPos);
        return temp;
    }
}
