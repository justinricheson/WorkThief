package com.workthief;

import java.util.Queue;
import java.util.LinkedList;

public class StandardTaskQueue implements TaskQueue {

    private Queue<Schedulable> queue = new LinkedList<>();

    @Override
    public void enqueue(Schedulable schedulable) {
        queue.add(schedulable);
    }

    public Schedulable dequeue(){
        if(queue.size() == 0){
            return  null;
        }
        return queue.remove();
    }
}
