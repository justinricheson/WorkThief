package com.workthief;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerSchedulable implements Schedulable {

    private String url;
    private int maxDepth;
    private static ConcurrentLinkedQueue<String> visited = new ConcurrentLinkedQueue<>();

    public WebCrawlerSchedulable(String url, int maxDepth){
        this.url = url;
        this.maxDepth = maxDepth;
    }

    public Collection<String> getVisited(){
        return Collections.unmodifiableCollection(visited);
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
        BufferedReader rdr = null;

        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(5000);
            stream = connection.getInputStream();
            rdr = new BufferedReader(
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
                if (rdr != null){
                    rdr.close();
                }
            } catch (Exception e) { }
        }

        return result;
    }
}
