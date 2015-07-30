package com.github.filipebezerra.baseplayservices.events;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 29/07/2015
 * @since #
 */
public class GoogleApiConnectionSuspendedEvent {
    private final Pair<Integer, String> mSuspensionCause;

    public GoogleApiConnectionSuspendedEvent(@NonNull final Pair<Integer, String> suspensionCause) {
        mSuspensionCause = suspensionCause;
    }

    public @NonNull Pair<Integer, String> getSuspensionCause() {
        return mSuspensionCause;
    }
}
