package uk.co.omgubuntu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pircbotx.PircBotX;

public class Main {

    public static void main(String[] args) throws Exception {
        boolean first = true;
        String channel = "#omg!ubuntu!";
        SimpleDateFormat date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        HashSet<Date> seen = new HashSet<Date>();
        PircBotX bot = new PircBotX();
        bot.setLogin("bot");
        bot.setName("omg-ubuntu");
        bot.setVersion("OMG! Ubuntu! Alerts bot");
        bot.connect("chat.freenode.net");
        bot.joinChannel(channel);
        while (true) {
            try {
                Elements items = Jsoup.connect("http://feeds.feedburner.com/d0od?format=xml").get().getElementsByTag("item");
                for (Element item : items) {
                    String title = item.getElementsByTag("title").first().text();
                    String link = item.getElementsByTag("guid").first().text();
                    String author = item.getElementsByTag("dc:creator").first().text();
                    Date pubDate = date.parse(item.getElementsByTag("pubDate").first().text());
                    if (!first && !seen.contains(pubDate)) {
                        bot.sendMessage(channel, "OMG! " + author + " just posted a new article titled " + title + ". Check it out at " + link);
                    }
                    seen.add(pubDate);
                }
                first = false;
            } catch (Exception ex) {
                bot.sendMessage(channel, "Encountered exception: " + ex.getClass().getSimpleName());
                ex.printStackTrace();
                seen.clear();
            }
            try {
                Thread.sleep(5 * 1000 * 60);
            } catch (InterruptedException ex) {
            }
        }
    }
}
