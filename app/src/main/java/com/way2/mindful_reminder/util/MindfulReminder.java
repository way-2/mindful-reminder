package com.way2.mindful_reminder.util;

import android.app.Application;
import android.content.Context;

public class MindfulReminder extends Application {
    private static MindfulReminder instance;

    public static MindfulReminder getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

}
