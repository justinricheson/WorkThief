package com.workthief;

import java.util.List;
import java.util.ArrayList;

public class StandardScheduler implements Scheduler {

    private List<Thread> threads = new ArrayList<>();

    public StandardScheduler(int numThreads, List<Schedulable> work){
        if(numThreads < 1){
            throw new IllegalArgumentException("numThreads");
        }

        List<StandardTaskQueue> queues = new ArrayList<>();
        for(int i = 0; i < numThreads; i++){
            StandardTaskQueue queue = new StandardTaskQueue();
            queues.add(queue);
        }

        for(int i = 0; i < work.size(); i++){
            StandardTaskQueue queue = queues.get(i % queues.size()); // Evenly distribute initial work
            queue.enqueue(work.get(i));
        }

        for(int i = 0; i < numThreads; i++){
            GoodSamaritan goodSamaritan = new GoodSamaritan(queues.get(i));
            Thread thread = new Thread(goodSamaritan);
            threads.add(thread);
        }
    }

    public void start(){
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) { }
        }
    }

    private class GoodSamaritan implements Runnable{
        private StandardTaskQueue queue;

        public GoodSamaritan(StandardTaskQueue queue){
            this.queue = queue;
        }

        @Override
        public void run() {
            Schedulable next;
            while((next = queue.dequeue()) != null){
                try{
                    next.run(queue);
                }catch(Exception e){ } // Swallow exceptions originating from scheduled tasks
            }
        }
    }
}
