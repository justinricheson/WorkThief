package com.workthief;

import java.util.ArrayList;
import java.util.List;

public class SharedQueueScheduler implements Scheduler {

    private List<Thread> threads = new ArrayList<>();

    public SharedQueueScheduler(int numThreads, List<Schedulable> work) {
        if(numThreads < 1){
            throw new IllegalArgumentException("numThreads");
        }

        ConcurrentTaskQueue queue = new ConcurrentTaskQueue();
        work.forEach(queue::enqueue); // Put all the work in the single queue

        for(int i = 0; i < numThreads; i++){
            Cooperative cooperative = new Cooperative(queue);
            Thread thread = new Thread(cooperative);
            threads.add(thread);
        }
    }

    public void start() {
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) { }
        }
    }

    private class Cooperative implements Runnable{
        private ConcurrentTaskQueue queue;

        public Cooperative(ConcurrentTaskQueue queue){
            this.queue = queue;
        }

        @Override
        public void run() {
            Schedulable next;
            while((next = queue.deqHead()) != null){
                try{
                    next.run(queue);
                }catch(Exception e){ } // Swallow exceptions originating from scheduled tasks
            }
        }
    }
}
