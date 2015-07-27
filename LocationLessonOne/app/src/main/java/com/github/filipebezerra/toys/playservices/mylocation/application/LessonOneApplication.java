package com.github.filipebezerra.toys.playservices.mylocation.application;

import android.app.Application;
import timber.log.Timber;

public class LessonOneApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
