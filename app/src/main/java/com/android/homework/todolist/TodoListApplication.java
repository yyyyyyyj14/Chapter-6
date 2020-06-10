package com.android.homework.todolist;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * @author wangzhongshan
 * @date 2020-04-19.
 */
public class TodoListApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
