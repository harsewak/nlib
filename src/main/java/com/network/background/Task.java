package com.network.background;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

/**
 * Created by harsewaksingh on 17/03/16.
 */
public abstract class Task implements Runnable {
    Handler handler;
    private TaskType taskType;
    protected long sleepTime = 0;

    protected Task(TaskType taskType) {
        this.taskType = taskType;
    }

    protected Task() {
        this.taskType = TaskType.DEFAULT;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        handler = new Handler(Looper.getMainLooper());
        //java.lang.Process.setThreadPriority(java.lang.Process.THREAD_PRIORITY_BACKGROUND);
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Log.d("Task", "interrupted exception wait time was " + sleepTime + " for " + taskType.toString());
            }
        }
        runInBackground();
    }

    public void runOnUiThread(Runnable r) {
        handler.post(r);
    }

    public void cancel() {
        TaskManager.getInstance().cancel(getTaskType());
    }


    protected TaskType getTaskType() {
        return taskType;
    }

    protected abstract void runInBackground();
}
