package com.abin.frequencycontrol.service.frequencycontrol.single;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    private static AtomicInteger pass1 = new AtomicInteger();
    private static AtomicInteger pass2 = new AtomicInteger();
    private static AtomicInteger pass3 = new AtomicInteger();
    private static AtomicInteger pass4 = new AtomicInteger();
    private static AtomicInteger total = new AtomicInteger();
    private static List<Integer> list1 = new ArrayList<>();
    private static List<Integer> list2 = new ArrayList<>();
    private static List<Integer> list3 = new ArrayList<>();
    private static List<Integer> list4 = new ArrayList<>();
    private static List<Integer> listTotal = new ArrayList<>();
    private static List<Double> x = new ArrayList<>();
    private static volatile boolean stop = false;
    private static final Integer QPS = 20;
    private static final Integer accuracy = 4;


    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = Arrays.asList(2, 2, 5, 20, 20, 20, 10, 5, 30, 30, 30, 2, 2, 2, 2, 2, 2, 2, 20, 20, 10, 10, 20, 10, 10, 5, 5, 5, 5, 2, 6, 4, 5, 7, 30, 30, 20, 20, 10, 20, 10, 20, 10, 20, 30, 20, 20, 10, 20, 10, 20, 10, 20, 30, 20, 20, 10, 20, 3, 3, 3, 20, 30, 20, 20, 10, 20, 10, 20, 10, 20);
        List<Integer> time = split(list);
        AtomicInteger index = new AtomicInteger(0);
        tick();
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(30, QPS);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        FixWindow fixWindow = new FixWindow(1000L, QPS);
        SlideWindow slideWindow = new SlideWindow(QPS, 1000L);
        LeakyBucketRateLimiter leakyBucketRateLimiter = new LeakyBucketRateLimiter(30, QPS);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        ExecutorService common = Executors.newFixedThreadPool(100);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int andIncrement = index.getAndIncrement();
            if (andIncrement < time.size()) {
                Integer integer = time.get(andIncrement);
                for (Integer i = 0; i < integer; i++) {
                    common.execute(() -> {
                        total.incrementAndGet();
                    });
                    common.execute(() -> {
                        fixWindowLimit(fixWindow);
                    });
                    common.execute(() -> {
                        slideWindowLimit(slideWindow);
                    });
                    executorService.execute(() -> {
                        leakyBucketRateLimit(leakyBucketRateLimiter);
                    });
                    common.execute(() -> {
                        tokenBucketRateLimit(tokenBucketRateLimiter);
                    });
                }
            } else {
                stop = true;
            }

        }, 0, 250 / accuracy, TimeUnit.MILLISECONDS);
        Thread.sleep(100000);
    }

    private static List<Integer> split(List<Integer> result) {
        ArrayList<Integer> res = new ArrayList<>(result.size() * 4);
        for (int i = 0; i < result.size(); i++) {
            Integer integer = result.get(i);
            Integer divisor = integer / accuracy;
            if (divisor == 0) divisor = 1;
            res.add(Math.min(integer, divisor));
            integer -= divisor;
            res.add(Math.min(integer, divisor));
            integer -= divisor;
            res.add(Math.min(integer, divisor));
            integer -= divisor;
            res.add(Math.min(integer, divisor));
        }
        return res;
    }

    private static void tokenBucketRateLimit(TokenBucketRateLimiter tokenBucketRateLimiter) {
        if (tokenBucketRateLimiter.tryAcquire()) {
            pass4.incrementAndGet();
        }
    }

    private static void leakyBucketRateLimit(LeakyBucketRateLimiter leakyBucketRateLimiter) {
        long l = leakyBucketRateLimiter.tryAcquire();
        if (l == 0) {
            pass3.incrementAndGet();
        } else if (l > 0) {
            try {
                Thread.sleep(l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pass3.incrementAndGet();
        }
    }

    private static void slideWindowLimit(SlideWindow fixWindow) {
        boolean b = fixWindow.tryAcquire();
        if (b) {
            pass2.incrementAndGet();
        }
    }

    private static void fixWindowLimit(FixWindow fixWindow) {
        boolean b = fixWindow.tryAcquire();
        if (b) {
            pass1.incrementAndGet();
        }
    }

    private static void limit(List<Integer> result) {
    }

    private static void tick() {
        Thread timer = new Thread(new TimerTask());
        timer.setName("sentinel-timer-task");
        timer.start();
    }

    static class TimerTask implements Runnable {
        @SneakyThrows
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            int i = 0;
            while (!stop) {
                i++;
                Thread.sleep(250);
                list1.add(pass1.getAndSet(0));
                list2.add(pass2.getAndSet(0));
                list3.add(pass3.getAndSet(0));
                list4.add(pass4.getAndSet(0));
                listTotal.add(total.getAndSet(0));
                x.add(i * 0.25);
            }
            System.out.println(x);
            System.out.println(listTotal);
            System.out.println(list1);
            System.out.println(list2);
            System.out.println(list3);
            System.out.println(list4);

        }
    }
}
