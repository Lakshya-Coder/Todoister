package com.bawp.todoister.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bawp.todoister.model.Task;
import com.bawp.todoister.util.TaskRoomDatabase;

import java.util.List;

public class DoisterRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTask;

    public DoisterRepository(Application application) {
        TaskRoomDatabase taskRoomDatabase = TaskRoomDatabase
                .getDatabase(application);

        taskDao = taskRoomDatabase.taskDao();
        allTask = taskDao.getTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTask;
    }

    public void insert(Task task) {
        TaskRoomDatabase.dataBasExecutor.execute(
                () -> taskDao.insert(task)
        );
    }

    public LiveData<Task> get(long taskId) {
        return taskDao.get(taskId);
    }

    public void update(Task task) {
        TaskRoomDatabase.dataBasExecutor.execute(
                () -> taskDao.update(task)
        );
    }

    public void delete(Task task) {
        TaskRoomDatabase.dataBasExecutor.execute(
                () -> taskDao.delete(task)
        );
    }

    public void deleteAll() {
        TaskRoomDatabase.dataBasExecutor.execute(
                taskDao::deleteAll
        );
    }
}