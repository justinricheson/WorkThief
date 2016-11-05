package com.workthief;

import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentDeque<T> {

    private Node head;
    private Node tail;
    private AtomicInteger count = new AtomicInteger();

    public int size(){
        return count.get();
    }

    public synchronized void enqueue(T value){
        count.incrementAndGet();

        Node node = new Node();
        node.value = value;

        if(head == null){
            head = node;
            tail = node;
            return;
        }

        tail.next = node;
        node.prev = tail;
        tail = node;
    }

    public synchronized T deqHead() {
        if(head == null){
            return null;
        }

        count.decrementAndGet();

        Node node = head;
        head = head.next;
        if(head != null){
            head.prev = null;
        } else{
            tail = null;
        }
        node.next = null;
        return node.value;
    }

    public synchronized T deqTail() {
        if(tail == null){
            return null;
        }

        count.decrementAndGet();

        Node node = tail;
        tail = tail.prev;
        if(tail != null){
            tail.next = null;
        } else{
            head = null;
        }
        node.prev = null;
        return node.value;
    }

    private class Node{
        public T value;
        public Node prev;
        public Node next;
    }
}
