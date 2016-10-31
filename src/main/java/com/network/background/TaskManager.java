package com.network.background;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by harsewaksingh on 17/03/16.
 */
public class TaskManager {
    private static TaskManager ourInstance = new TaskManager();

    public static TaskManager getInstance() {
        return ourInstance;
    }

    private TaskManager() {
        executorHashMap = new HashMap<>();
    }

    public void cancel(TaskType taskType) {
        TaskExecutor taskExecutor = executorHashMap.get(taskType);
        if (taskExecutor != null) {
            taskExecutor.cancel();
        }
    }

    HashMap<TaskType, TaskExecutor> executorHashMap;

    public void execute(Task task) {
        TaskExecutor taskExecutor = executorHashMap.get(task.getTaskType());
        if (taskExecutor == null) {
            taskExecutor = new TaskExecutor();
            executorHashMap.put(task.getTaskType(), taskExecutor);
        }
        taskExecutor.execute(task);
    }

    public void cancelAll() {
        Set<Map.Entry<TaskType, TaskExecutor>> set = executorHashMap.entrySet();
        for (Map.Entry<TaskType, TaskExecutor> entry : set) {
            entry.getValue().cancel();
        }
    }
}
