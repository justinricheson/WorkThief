package com.workthief;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkStealingScheduler implements Scheduler {

    private List<Thread> threads = new ArrayList<>();
    private AtomicInteger executing = new AtomicInteger();

    public WorkStealingScheduler(int numThreads, List<Schedulable> work) {
        if(numThreads < 1){
            throw new IllegalArgumentException("numThreads");
        }

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
            Thief thief = new Thief(queues.get(i), except(queues, i));
            Thread thread = new Thread(thief);
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

    private List<ConcurrentTaskQueue> except(List<ConcurrentTaskQueue> queues, int i){
        List<ConcurrentTaskQueue> result = new ArrayList<>();

        for(int j = 0; j < queues.size(); j++){
            if(j != i){
                result.add(queues.get(j));
            }
        }

        return result;
    }

    private class Thief implements Runnable{
        private List<ConcurrentTaskQueue> remoteQueues;
        private ConcurrentTaskQueue localQueue;

        public Thief(ConcurrentTaskQueue localQueue, List<ConcurrentTaskQueue> remoteQueues){
            this.remoteQueues = remoteQueues;
            this.localQueue = localQueue;
        }

        @Override
        public void run() {
            while(anyTasks() || executing.get() > 0){
                execute(localQueue, false); // Execute local queue first
                for (int i = 0; i < remoteQueues.size(); i++){ // Out of work, start stealing!

                    execute(remoteQueues.get(i), true);
                }

                Thread.yield();
            }
        }

        private boolean anyTasks(){
            for (ConcurrentTaskQueue queue : remoteQueues) {
                if (queue.size() > 0) {
                    return true;
                }
            }

            return localQueue.size() > 0;
        }

        private void execute(ConcurrentTaskQueue queue, boolean stealing){
            Schedulable next;
            while((next = stealing ? queue.deqTail() : queue.deqHead()) != null){
                try{
                    if(stealing) { System.out.println("STEALING"); }

                    executing.incrementAndGet();
                    next.run(localQueue); // Put new work back on local queue

                    if(localQueue.size() > 0){
                        break; // Try to switch back to local queue, minimize concurrent access to remote queues
                    }

                } catch(Exception e){ // Swallow exceptions originating from scheduled tasks
                } finally {
                    executing.decrementAndGet();
                }
            }
        }
    }
}