package com.github.filipebezerra.immersivemode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

/**
 * A utility class that helps with showing and hiding system UI such as the status bar and
 * navigation/system bar. This class uses backward-compatibility techniques described in <a href=
 * "http://developer.android.com/training/backward-compatible-ui/index.html"> Creating
 * Backward-Compatible UIs</a> to ensure that devices running any version of ndroid OS are
 * supported. More specifically, there are separate implementations of this abstract class: for
 * newer devices, {@link #getInstance} will return a {@link ImmersiveModeJellyBean} instance, while
 * on older devices {@link #getInstance} will return a {@link ImmersiveModeKitKat} instance. <p> For
 * more on system bars, see <a href= "http://developer.android.com/design/get-started/ui-overview.html#system-bars"
 * > System Bars</a>.
 *
 * @see android.view.View#setSystemUiVisibility(int)
 */
public abstract class ImmersiveMode {

    /**
     * The activity associated with this UI hider object.
     */
    protected Activity mActivity;

    /**
     * Creates and returns an instance of {@link ImmersiveMode} that is appropriate for this device.
     * The object will be either a {@link ImmersiveModeKitKat} or {@link ImmersiveModeJellyBean}
     * depending on the device.
     *
     * @param activity The activity whose window's system UI should be controlled by this class.
     */
    public static ImmersiveMode getInstance(Activity activity)
            throws UnsupportedOperationException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new ImmersiveModeKitKat(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new ImmersiveModeJellyBean(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new ImmersiveModeIceCreamSandwich(activity);
        } else {
            throw new UnsupportedOperationException("Android version not supported.");
        }
    }

    protected ImmersiveMode(Activity activity) {
        mActivity = activity;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected int getNewUiOptions(int flag) {
        int newUiOptions = mActivity.getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= flag;

        return newUiOptions;
    }

    /**
     * Hide the system UI.
     */
    public abstract void start();
}
