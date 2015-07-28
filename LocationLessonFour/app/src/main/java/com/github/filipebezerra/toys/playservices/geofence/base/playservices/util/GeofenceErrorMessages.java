package com.github.filipebezerra.toys.playservices.geofence.base.playservices.util;

import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Util class for Geofence service provided by Google Play Services, that provides common error messages.
 *
 * @author Filipe Bezerra
 * @version #, 27/07/2015
 * @since #
 */
public final class GeofenceErrorMessages {
    public static String getErrorMessage(final int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence service is not available now";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Your app has registered more than 100 geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "You have provided more than 5 different PendingIntents to the addGeofences()";
            default:
                return "Unknown error code. The Geofence service is not available now";
        }
    }
}
