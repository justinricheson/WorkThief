package com.workthief;

public interface TaskQueue {
    void enqueue(Schedulable schedulable);
}
