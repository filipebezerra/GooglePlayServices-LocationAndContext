package com.github.filipebezerra.baseplayservices.connectionmessages;

import com.google.android.gms.common.ConnectionResult;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * Util class for Google Play Services, that provides common error messages for {@link
 * ConnectionCallbacks#onConnectionSuspended(int)}
 * and {@link OnConnectionFailedListener#onConnectionFailed(ConnectionResult)}.
 *
 * @author Filipe Bezerra
 * @version #, 06/04/2015
 * @since #
 */
public final class CommonGoogleApiConnectionErrorMessages {
    /**
     * Retrieves the error message for {@link OnConnectionFailedListener#onConnectionFailed(ConnectionResult).
     *
     * @param errorCode the code retrieved from {@link ConnectionResult#getErrorCode()}
     * @return the error message representing the @param errorCode
     */
    public static String getConnectionResultErrorMessage(final int errorCode) {
        switch (errorCode) {
            case ConnectionResult.API_UNAVAILABLE:
                return "One of the API components you attempted to connect to is not available.";
            case ConnectionResult.CANCELED:
                return "The client canceled the connection by calling disconnect()";
            case ConnectionResult.DEVELOPER_ERROR:
                return "The application is misconfigured.";
            case ConnectionResult.INTERNAL_ERROR:
                return "An internal error occurred.";
            case ConnectionResult.INTERRUPTED:
                return "An interrupt occurred while waiting for the connection complete.";
            case ConnectionResult.INVALID_ACCOUNT:
                return "The client attempted to connect to the service with an invalid account name specified.";
            case ConnectionResult.LICENSE_CHECK_FAILED:
                return "The application is not licensed to the user.";
            case ConnectionResult.NETWORK_ERROR:
                return "A network error occurred.";
            case ConnectionResult.RESOLUTION_REQUIRED:
                return "Completing the connection requires some form of resolution.";
            case ConnectionResult.SERVICE_DISABLED:
                return "The installed version of Google Play services has been disabled on this device.";
            case ConnectionResult.SERVICE_INVALID:
                return "The version of the Google Play services installed on this device is not authentic.";
            case ConnectionResult.SERVICE_MISSING:
                return "Google Play services is missing on this device.";
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return "The installed version of Google Play services is out of date.";
            case ConnectionResult.SIGN_IN_FAILED:
                return "The client attempted to connect to the service but the user is not signed in.";
            case ConnectionResult.SIGN_IN_REQUIRED:
                return "The client attempted to connect to the service but the user is not signed in.";
            case ConnectionResult.TIMEOUT:
                return "The timeout was exceeded while waiting for the connection to complete.";
            case ConnectionResult.SUCCESS:
                return "The connection was successful.";
            default:
                return "unknown error code";
        }
    }

    /**
     * Retrieves the error message for {@link ConnectionCallbacks#onConnectionSuspended(int)}.
     *
     * @param cause the code retrieved from {@link ConnectionCallbacks#onConnectionSuspended(int)}
     * @return the error message representing the @param cause
     */
    public static String getConnectionSuspendedCauseMessage(final int cause) {
        switch (cause) {
            case CAUSE_NETWORK_LOST:
                return "peer device connection was lost";
            case CAUSE_SERVICE_DISCONNECTED:
                return "service has been killed";
            default:
                return "unknown cause";
        }
    }
}
