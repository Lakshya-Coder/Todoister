package com.bawp.todoister.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bawp.todoister.data.DoisterRepository;
import com.bawp.todoister.util.TaskRoomDatabase;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    public static DoisterRepository doisterRepository;
    public final LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);

        doisterRepository = new DoisterRepository(application);
        allTasks = doisterRepository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() { return allTasks; }
    public static void insert(Task task) { doisterRepository.insert(task); }
    public static LiveData<Task> get(long taskId) { return doisterRepository.get(taskId); }
    public static void update(Task task) { doisterRepository.update(task); }
    public static void delete(Task task) { doisterRepository.delete(task); }
    public static void deleteAll() { doisterRepository.deleteAll(); }
}
