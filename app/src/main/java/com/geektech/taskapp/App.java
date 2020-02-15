package com.geektech.taskapp;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.geektech.taskapp.room.AppDataBase;

public class App extends Application {

    private static App instance;
    private static AppDataBase dataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dataBase = Room
                .databaseBuilder(
                this, AppDataBase.class, "database")
                .allowMainThreadQueries()
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public static AppDataBase getDataBase() {
        return dataBase;
    }
}
