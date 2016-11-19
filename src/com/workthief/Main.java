package com.workthief;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Schedulable> work = Arrays.asList(
            new WebCrawlerSchedulable("http://www.google.com", 1),
            new WebCrawlerSchedulable("http://www.apple.com", 1),
            new WebCrawlerSchedulable("http://www.stackoverflow.com", 2));

        WorkStealingScheduler scheduler = new WorkStealingScheduler(10, work);
        scheduler.start();

        for (Schedulable w : work) {
            List<String> result = ((WebCrawlerSchedulable) w).getVisited();
            result.forEach(System.out::println);
        }
    }
}
