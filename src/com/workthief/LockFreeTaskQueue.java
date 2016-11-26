package com.workthief;

public class LockFreeTaskQueue extends LockFreeDeque<Schedulable> implements TaskQueue {

    public LockFreeTaskQueue(){ this(1000000); }
    public LockFreeTaskQueue(int size) {
        super(size);
    }
}
