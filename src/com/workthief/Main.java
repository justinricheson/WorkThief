package com.workthief;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Schedulable> work = Arrays.asList(
            //new WebCrawlerSchedulable("http://www.google.com", 1),
            //new WebCrawlerSchedulable("http://www.apple.com", 1),
            new WebCrawlerSchedulable("http://www.stackoverflow.com", 3));

        WorkStealingScheduler scheduler = new WorkStealingScheduler(10, work);
        scheduler.start();

        for(int i = 0; i < work.size(); i++){
            List<String> result = ((WebCrawlerSchedulable)work.get(i)).getVisited();
            for(int j = 0; j < result.size(); j++){
                System.out.println(result.get(j));
            }
        }
    }
}
