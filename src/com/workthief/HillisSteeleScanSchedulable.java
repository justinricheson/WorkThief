package com.workthief;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class HillisSteeleScanSchedulable implements Schedulable {

    private BigInteger[] a;
    private BigInteger[] b;
    private int left;
    private int right;
    private int round;
    private AtomicInteger count;
    private boolean first;

    private HillisSteeleScanSchedulable(BigInteger[] a, BigInteger[] b, AtomicInteger count,
                                        int left, int right, int round){
        this.a = a;
        this.b = b;
        this.left = left;
        this.right = right;
        this.round = round;
        this.count = count;
    }
    public HillisSteeleScanSchedulable(BigInteger[] values){
        a = new BigInteger[values.length];
        b = new BigInteger[values.length];
        for(int i = 0; i < b.length; i++){
            b[i] = values[i];
        }
        first = true;
        count = new AtomicInteger();
    }

    public Collection<BigInteger> getResult(){
        int r = 1;
        int numRounds = 0;
        while(r <= a.length){
            r = r << 1;
            numRounds++;
        }

        BigInteger[] t = numRounds % 2 == 0 ? a : b;
        List<BigInteger> result = new ArrayList<>();
        for (int i = 0; i < t.length; i++)
        {
            result.add(t[i]);
        }
        return result;
    }

    @Override
    public void run(TaskQueue queue) {
        if(first){
            initNextPass(queue);
            return;
        }

        if(left == right){
            b[left] = a[left];
            if(b[left] == null){
                String breakhere = "";
            }
        }else{
            b[right] = a[left].add(a[right]);
            if(b[right] == null){
                String breakhere = "";
            }
        }

        count.incrementAndGet();
        if(count.get() == a.length){
            initNextPass(queue);
        }
    }

    private void initNextPass(TaskQueue queue){
        count.set(0);

        int offset = 1 << round;
        if(offset >= a.length){
            return;
        }

        int nextRound = round + 1;
        for (int i = a.length - 1; i >= 0; i--){
            int r = i;
            int l = r - offset;
            if(l < 0){
                l = r;
            }

            queue.enqueue(new HillisSteeleScanSchedulable(
                    b, a, count, l, r, nextRound));
        }
    }
}
