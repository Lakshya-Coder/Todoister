package com.bawp.todoister.util;

import android.content.Context;
import android.database.DatabaseUtils;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.bawp.todoister.data.TaskDao;
import com.bawp.todoister.model.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = { Task.class },
        version = 1,
        exportSchema = false
)
@TypeConverters({Convert.class})
public abstract class TaskRoomDatabase extends RoomDatabase {
    public static final int NUMBER_OF_THREADS = 4;
    public static final String TASK_DATABASE = "task_database";
    private static volatile TaskRoomDatabase INSTANCE;
    public static final ExecutorService dataBasExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static final Callback sRoomDatabaseCallback =
            new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    dataBasExecutor
                            .execute(() -> {
                                // invoke DAO
                                TaskDao taskDao = INSTANCE.taskDao();

                                taskDao.deleteAll();

                            });
                }
            };

    public static TaskRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                             Room.databaseBuilder(
                                     context,
                                     TaskRoomDatabase.class,
                                     TASK_DATABASE)
                                     .addCallback(sRoomDatabaseCallback)
                                     .build();
                }
            }
        }
        
        return INSTANCE;
    }

    public abstract TaskDao taskDao();
}
