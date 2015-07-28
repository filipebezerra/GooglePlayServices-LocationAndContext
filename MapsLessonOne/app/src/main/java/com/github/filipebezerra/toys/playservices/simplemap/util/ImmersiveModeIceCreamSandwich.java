package com.github.filipebezerra.toys.playservices.simplemap.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * An API 14+ implementation of {@link ImmersiveMode}. Uses APIs available in IceCreamSandwich and
 * later (specifically {@link View#setSystemUiVisibility(int)}) to show and start the system UI.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ImmersiveModeIceCreamSandwich extends ImmersiveModeKitKat {

    /**
     * Constructor not intended to be called by clients. Use {@link ImmersiveMode#getInstance} to
     * obtain an instance.
     */
    protected ImmersiveModeIceCreamSandwich(Activity activity) {
        super(activity);
    }

    /** {@inheritDoc} */
    @Override
    public void start() {
        mActivity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(getNewUiOptions(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION));
    }
}
