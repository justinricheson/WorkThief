package com.workthief;

public class ConcurrentTaskQueue extends ConcurrentDeque<Schedulable> implements TaskQueue {

    public void enqueue(Schedulable schedulable){
        super.enqueue(schedulable);
    }
}
