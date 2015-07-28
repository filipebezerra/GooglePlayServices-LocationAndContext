package com.github.filipebezerra.toys.playservices.locationchanges.application;

import android.app.Application;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 28/07/2015
 * @since #
 */
public class LessonTwoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
