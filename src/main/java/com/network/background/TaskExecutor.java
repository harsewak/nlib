package com.network.background;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by harsewaksingh on 17/03/16.
 */
public class TaskExecutor {

    private final int corePoolSize = Runtime.getRuntime().availableProcessors();
    private final int maximumPoolSize = corePoolSize;
    private final long keepALiveTime = 1;

    ThreadPoolExecutor threadPoolExecutor;
    BlockingQueue<Runnable> workQueue;

    TaskExecutor() {
        workQueue = new LinkedBlockingDeque<>();
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepALiveTime, TimeUnit.SECONDS, workQueue);
    }


    public void execute(Task task) {
        threadPoolExecutor.execute(task);
    }

    public void cancel() {
        threadPoolExecutor.shutdown();
    }
}
