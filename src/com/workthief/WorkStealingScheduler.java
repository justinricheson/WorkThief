package com.workthief;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkStealingScheduler implements Scheduler {

    private int numThreads;
    private List<Thread> threads = new ArrayList<Thread>();
    private AtomicInteger executing = new AtomicInteger();

    public WorkStealingScheduler(int numThreads, List<Schedulable> work) {
        if(numThreads < 1){
            throw new IllegalArgumentException("numThreads");
        }

        this.numThreads = numThreads;

        List<ConcurrentTaskQueue> queues = new ArrayList<>();
        for(int i = 0; i < numThreads; i++){
            ConcurrentTaskQueue queue = new ConcurrentTaskQueue();
            queues.add(queue);
        }

        for(int i = 0; i < work.size(); i++){
            ConcurrentTaskQueue queue = queues.get(i % queues.size()); // Evenly distribute initial work
            queue.enqueue(work.get(i));
        }

        for(int i = 0; i < numThreads; i++){
            Thief thief = new Thief(i, queues);
            Thread thread = new Thread(thief);
            threads.add(thread);
        }
    }

    public void start() {
        for (int i = 0; i < threads.size(); i++){
            threads.get(i).start();
        }
        for (int i = 0; i < threads.size(); i++){
            try{
                threads.get(i).join();
            } catch(Exception e){ }
        }
    }

    private class Thief implements Runnable{
        private int threadId;
        private List<ConcurrentTaskQueue> queues;

        public Thief(int threadId, List<ConcurrentTaskQueue> queues){
            this.threadId = threadId;
            this.queues = queues;
        }

        @Override
        public void run() {
            while(anyTasks() || executing.get() > 0){
                execute(queues.get(threadId), false); // Execute local queue first
                for (int i = 0; i < queues.size(); i++){ // Out of work, start stealing!
                    if(i == threadId){
                        continue;
                    }

                    execute(queues.get(i), true);
                }

                Thread.yield();
            }
        }

        private boolean anyTasks(){
            for(int i = 0; i < queues.size(); i++){
                if(queues.get(i).size() > 0){
                    return true;
                }
            }

            return false;
        }

        private void execute(ConcurrentTaskQueue queue, boolean stealing){
            Schedulable next = null;
            while((next = stealing ? queue.deqTail() : queue.deqHead()) != null){
                try{
                    if(stealing){
                        System.out.println("STEALING");
                    }

                    executing.incrementAndGet();
                    next.run(queue);

                }catch(Exception e){
                    // Swallow exceptions originating from scheduled tasks
                }finally {
                    executing.decrementAndGet();
                }
            }
        }
    }
}