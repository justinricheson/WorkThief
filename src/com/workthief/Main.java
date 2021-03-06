package com.workthief;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.time.Instant;
import java.time.Duration;

public class Main {

    public static void main(String[] args) {
        run(1);
        run(2);
        run(5);
        run(10);
        run(100);
    }

    private static void run(int numThreads){
        List<Schedulable> work = Arrays.asList(
            //new WebCrawlerSchedulable("http://www.stackoverflow.com", 2),
            new HillisSteeleScanSchedulable(getValues(400000)));

        Instant starts = Instant.now();

        //StandardScheduler scheduler = new StandardScheduler(numThreads, work);
        //SharedQueueScheduler scheduler = new SharedQueueScheduler(numThreads, work);
        WorkStealingScheduler scheduler = new WorkStealingScheduler(numThreads, work);
        scheduler.start();

        for (Schedulable w : work) {
            //Collection<String> result = ((WebCrawlerSchedulable)w).getVisited();
            Collection<BigInteger> result = ((HillisSteeleScanSchedulable)w).getResult();
            BigInteger last = (BigInteger) (result.toArray()[result.size() - 1]);
            System.out.println(last.toString());
            //result.forEach(System.out::println);
        }

        Instant ends = Instant.now();
        System.out.println(Duration.between(starts, ends));
    }

    private static BigInteger[] getValues(int size){
        BigInteger[] result = new BigInteger[size];
        for(int i = 0; i < size; i++){
            result[i] = new BigInteger(Integer.toString(i));
        }
        return result;
    }
}
