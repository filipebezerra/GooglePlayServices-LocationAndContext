package com.github.filipebezerra.toys.playservices.maps.showingmarkers.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * An API 16+ implementation of {@link ImmersiveMode}. Uses APIs available in JellyBean and later
 * (specifically {@link View#setSystemUiVisibility(int)}) to show and start the system UI.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ImmersiveModeJellyBean extends ImmersiveModeKitKat {

    /**
     * Constructor not intended to be called by clients. Use {@link ImmersiveMode#getInstance} to
     * obtain an instance.
     */
    protected ImmersiveModeJellyBean(Activity activity) {
        super(activity);
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
        mActivity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(getNewUiOptions(View.SYSTEM_UI_FLAG_FULLSCREEN));
    }

}
