package com.workthief;

import java.util.concurrent.atomic.AtomicInteger;

// Lock free workstealing deque
// Modified Chase-Lev algorithm, no array resizing
// http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.170.1097&rep=rep1&type=pdf
public class LockFreeDeque<T> {

    private volatile AtomicInteger top = new AtomicInteger();
    private volatile int bottom;
    private CircularArray<T> values;
    private AtomicInteger count = new AtomicInteger();

    public LockFreeDeque(int size){
        values = new CircularArray<>(size);
    }

    public void enqueue(T value){
        values.put(bottom, value);
        bottom++;
        count.incrementAndGet();
    }

    public T deqHead(){
        bottom--;
        int t = top.get();
        int size = bottom - t;
        if (size < 0) {
            bottom = t;
            return null;
        }
        T value = values.get(bottom);
        if (size > 0){
            count.decrementAndGet();
            return value;
        }
        if (!top.compareAndSet(t, t + 1)){
            value = null;
        } else{
            count.decrementAndGet();
        }
        bottom = t + 1;
        return value;
    }

    public T deqTail(){
        while(true){
            int t = top.get();
            int b = bottom;
            int size = b - t;
            if (size <= 0){
                return null;
            }

            T value = values.get(t);
            if (top.compareAndSet(t, t + 1)){
                count.decrementAndGet();
                return value;
            }
        }
    }

    public int size(){
        return count.get();
    }

    private class CircularArray<T> {
        private T[] values;

        CircularArray(int size) {
            values = (T[])new Object[size];
        }

        public T get(int i) {
            return values[i % values.length];
        }

        public void put(int i, T value) {
            values[i % values.length] = value;
        }
    }
}
