package com.github.filipebezerra.baseplayservices.delegates;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.github.filipebezerra.baseplayservices.events.BusProvider;
import com.github.filipebezerra.baseplayservices.events.GoogleApiConnectedEvent;
import com.github.filipebezerra.baseplayservices.events.GoogleApiConnectionFailedEvent;
import com.github.filipebezerra.baseplayservices.events.GoogleApiConnectionSuspendedEvent;
import com.google.android.gms.common.ConnectionResult;
import com.squareup.otto.Bus;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 29/07/2015
 * @since #
 */
public class GoogleApiConnectionCallbacksDelegate {
    @NonNull private static final Bus sBus = BusProvider.getInstance();

    public void notifyOnConnected(@NonNull final Bundle connectionHint) {
        sBus.post(new GoogleApiConnectedEvent(connectionHint));
    }

    public void notifyOnConnectionSuspended(final int cause, @NonNull final String causeMessage) {
        final Pair<Integer, String> suspensionCause = new Pair<>(cause, causeMessage);
        sBus.post(new GoogleApiConnectionSuspendedEvent(suspensionCause));
    }

    public void notifyOnConnectionFailed(@NonNull final ConnectionResult result,
            final boolean isResolvingError) {
        sBus.post(new GoogleApiConnectionFailedEvent(result, isResolvingError));
    }
}
