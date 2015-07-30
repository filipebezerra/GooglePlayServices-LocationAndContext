package com.github.filipebezerra.toys.playservices.maps.streetview.application;

import android.app.Application;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 29/07/2015
 * @since #
 */
public class LessonThreeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
