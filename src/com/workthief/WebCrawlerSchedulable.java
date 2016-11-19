package com.workthief;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerSchedulable implements Schedulable {

    private String url;
    private int maxDepth;
    private static Queue<String> visited = new ConcurrentLinkedQueue<>();

    public WebCrawlerSchedulable(String url, int maxDepth){
        this.url = url;
        this.maxDepth = maxDepth;
    }

    public List<String> getVisited(){
        List<String> result = new ArrayList<>();
        while(visited.size() > 0){
            result.add(visited.remove());
        }
        return result;
    }

    @Override
    public void run(TaskQueue queue) {
        List<String> result = getLinks(url);
        visited.addAll(result);

        if(maxDepth > 1){
            for (String r : result) {
                queue.enqueue(new WebCrawlerSchedulable(r, maxDepth - 1));
            }
        }
    }

    private static List<String> getLinks(final String urlString) {
        List<String> result = new ArrayList<>();
        InputStream stream = null;

        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(5000);
            stream = connection.getInputStream();
            BufferedReader rdr = new BufferedReader(
                new InputStreamReader(stream));

            String line;
            while ((line = rdr.readLine()) != null) {
                if(line.contains("href=")){

                    // Quick and dirty
                    String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(line);
                    while (m.find())
                    {
                        result.add(m.group());
                    }
                }
            }
        }
        catch (Exception e) { }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) { }
        }

        return result;
    }
}
