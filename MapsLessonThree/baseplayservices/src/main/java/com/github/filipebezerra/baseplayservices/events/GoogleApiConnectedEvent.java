package com.github.filipebezerra.baseplayservices.events;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 29/07/2015
 * @since #
 */
public class GoogleApiConnectedEvent {
    private final Bundle mConnectionHint;

    public GoogleApiConnectedEvent(@NonNull final Bundle connectionHint) {
        mConnectionHint = connectionHint;
    }

    public @NonNull Bundle getConnectionHint() {
        return mConnectionHint;
    }
}
