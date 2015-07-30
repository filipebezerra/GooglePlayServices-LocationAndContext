package com.github.filipebezerra.baseplayservices.events;

import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 29/07/2015
 * @since #
 */
public class GoogleApiConnectionFailedEvent {
    private final ConnectionResult mResult;
    private final boolean mIsResolvingError;

    public GoogleApiConnectionFailedEvent(@NonNull final ConnectionResult result,
            final boolean isResolvingError) {
        mResult = result;
        mIsResolvingError = isResolvingError;
    }

    public @NonNull ConnectionResult getResult() {
        return mResult;
    }

    public boolean isResolvingError() {
        return mIsResolvingError;
    }
}
