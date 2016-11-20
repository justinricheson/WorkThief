package com.workthief;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Schedulable> work = Arrays.asList(
            //new WebCrawlerSchedulable("http://www.google.com", 1),
            //new WebCrawlerSchedulable("http://www.apple.com", 1),
            new WebCrawlerSchedulable("http://www.stackoverflow.com", 3));

        WorkStealingScheduler scheduler = new WorkStealingScheduler(100, work);
        scheduler.start();

        for (Schedulable w : work) {
            Collection<String> result = ((WebCrawlerSchedulable) w).getVisited();
            result.forEach(System.out::println);
        }
    }
}
