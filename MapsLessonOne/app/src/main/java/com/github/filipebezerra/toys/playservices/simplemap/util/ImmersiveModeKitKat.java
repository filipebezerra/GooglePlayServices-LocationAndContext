package com.github.filipebezerra.toys.playservices.simplemap.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * An API 19+ implementation of {@link ImmersiveMode}. Uses APIs available in KitKat and later
 * (specifically {@link View#setSystemUiVisibility(int)}) to start the system UI.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ImmersiveModeKitKat extends ImmersiveMode {

    /**
     * Constructor not intended to be called by clients. Use {@link ImmersiveMode#getInstance} to
     * obtain an instance.
     */
    protected ImmersiveModeKitKat(Activity activity) {
        super(activity);
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
        mActivity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(getNewUiOptions(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY));
    }
}
