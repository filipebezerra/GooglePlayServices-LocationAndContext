package com.github.filipebezerra.toys.playservices.geofence.application;

import android.app.Application;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 27/07/2015
 * @since #
 */
public class LessonFourApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
