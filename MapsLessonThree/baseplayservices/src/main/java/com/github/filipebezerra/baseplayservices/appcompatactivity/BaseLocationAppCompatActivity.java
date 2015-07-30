package com.github.filipebezerra.baseplayservices.appcompatactivity;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 27/07/2015
 * @since #
 */
public abstract class BaseLocationAppCompatActivity extends BaseGoogleApiAppCompatActivity
        implements LocationListener {

    /**
     * Interval in millis for high accuracy location. This would be appropriate for mapping
     * applications that are showing your location in real-time. <br><br>Used in {@link
     * #createAndGetHighAccuracyLocationRequest()}.
     */
    public static final long INTERVAL_IN_MILLIS_FOR_HIGH_ACCURACY = TimeUnit.SECONDS.toMillis(5);

    /**
     * Interval in millis in conjunction with {@link #FASTEST_INTERVAL_IN_MILLIS_FOR_FASTER_LOW_POWER_IMPACT},
     * for applications definitely want to receive updates at a specified interval, and can receive
     * them faster when available, but still want a low power impact. <br><br>Used in {@link
     * #createAndGetFasterLowPowerImpactLocationRequest()}
     */
    public static final long INTERVAL_IN_MILLIS_FOR_FASTER_LOW_POWER_IMPACT
            = TimeUnit.MINUTES.toMillis(60);

    /**
     * Fastest interval in millis in conjunction with {@link #INTERVAL_IN_MILLIS_FOR_FASTER_LOW_POWER_IMPACT},
     * for applications definitely want to receive updates at a specified interval, and can receive
     * them faster when available, but still want a low power impact. <br><br>Used in {@link
     * #createAndGetFasterLowPowerImpactLocationRequest()}
     */
    public static final long FASTEST_INTERVAL_IN_MILLIS_FOR_FASTER_LOW_POWER_IMPACT
            = TimeUnit.MINUTES.toMillis(1);

    /**
     * No fastest interval in some cases in conjunction with {@link #NO_INTERVAL} for negligible
     * power impact, but to still receive location updates when available. <br><br>Used for example
     * in {@link #createAndGetHighAccuracyLocationRequest()} and {@link
     * #createAndGetNoPowerImpactLocationRequest()}
     */
    public static final int NO_FASTEST_INTERVAL = 0;

    /**
     * No interval in conjunction with {@link #NO_FASTEST_INTERVAL} for negligible power impact, but
     * to still receive location updates when available. <br><br>Used for example in {@link
     * #createAndGetNoPowerImpactLocationRequest()}
     */
    public static final int NO_INTERVAL = 0;

    private static final String STATE_REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_updates";
    private static final String STATE_LAST_KNOWN_LOCATION_KEY = "last_known_location";

    /**
     * Location request object to request updates.
     */
    private LocationRequest mLocationRequest;

    private boolean mRequestingLocationUpdates = false;

    private Location mLastKnowLocation;

    @Override protected void onPause() {
        super.onPause();

        stopRequestingLocationUpdates();
    }

    /**
     * This method can create or recreate a new instance of {@link LocationRequest} and assign to
     * {@link #mLocationRequest} configured with the arguments passed to this method. <br>If {@link
     * #mLocationRequest} points to a reference of {@link LocationRequest}, you can force recreation
     * of a new instance by passing <code>true</code> to {@param createNewIfExists}.
     *
     * @param intervalInMillis Set the desired interval for active location updates, in
     * milliseconds.
     * @param fastestInvervalInMillis Explicitly set the fastest interval for location updates, in
     * milliseconds.
     * @param priority Set the priority. Must be one of: <ul><li>{@link
     * LocationRequest#PRIORITY_NO_POWER}</li><li>{@link LocationRequest#PRIORITY_LOW_POWER}</li><li>{@link
     * LocationRequest#PRIORITY_BALANCED_POWER_ACCURACY}</li><li>{@link
     * LocationRequest#PRIORITY_HIGH_ACCURACY}</li></ul>
     * @param createNewIfExists If <code>true</code>, recreate the {@link #mLocationRequest}
     * @return {@link #mLocationRequest} created, recreated or only retrieved
     * @see #createAndGetHighAccuracyLocationRequest()
     * @see #createAndGetNoPowerImpactLocationRequest()
     * @see #createAndGetFasterLowPowerImpactLocationRequest()
     */
    public LocationRequest createAndGetLocationRequest(final long intervalInMillis,
            final long fastestInvervalInMillis, final int priority,
            final boolean createNewIfExists) {
        if (mLocationRequest != null && !createNewIfExists) {
            return mLocationRequest;
        }

        mLocationRequest = LocationRequest.create().setPriority(priority);

        if (intervalInMillis > 0) {
            mLocationRequest.setInterval(intervalInMillis);
        }

        if (fastestInvervalInMillis > 0) {
            mLocationRequest.setFastestInterval(fastestInvervalInMillis);
        }

        return mLocationRequest;
    }

    /**
     * Create or if {@link #mLocationRequest} is already created then recreate configured for
     * applications that needs high accuracy location updates.
     *
     * @return {@link #mLocationRequest} created, recreated or only retrieved
     */
    public LocationRequest createAndGetHighAccuracyLocationRequest() {
        return createAndGetLocationRequest(
                INTERVAL_IN_MILLIS_FOR_HIGH_ACCURACY,
                NO_FASTEST_INTERVAL,
                LocationRequest.PRIORITY_HIGH_ACCURACY, true);
    }

    /**
     * Create or if {@link #mLocationRequest} is already created then recreate configured for
     * applications that needs negligible power impact, but to still receive location updates when
     * available.
     *
     * @return {@link #mLocationRequest} created, recreated or only retrieved
     */
    public LocationRequest createAndGetNoPowerImpactLocationRequest() {
        return createAndGetLocationRequest(
                NO_INTERVAL,
                NO_FASTEST_INTERVAL,
                LocationRequest.PRIORITY_NO_POWER, true);
    }

    /**
     * Create or if {@link #mLocationRequest} is already created then recreate configured for
     * applications that needs to receive updates at a specified interval, and can receive them
     * faster when available, but still want a low power impact.
     *
     * @return {@link #mLocationRequest} created, recreated or only retrieved
     */
    public LocationRequest createAndGetFasterLowPowerImpactLocationRequest() {
        return createAndGetLocationRequest(
                INTERVAL_IN_MILLIS_FOR_FASTER_LOW_POWER_IMPACT,
                FASTEST_INTERVAL_IN_MILLIS_FOR_FASTER_LOW_POWER_IMPACT,
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, true);
    }

    @Override public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        if (canRequestLocationOnConnect()) {
            startRequestingLocationUpdates(createAndGetHighAccuracyLocationRequest());
        } else {
            mLastKnowLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    public void startRequestingLocationUpdates(@NonNull final LocationRequest locationRequest) {
        if (isGoogleApiClientConnected()) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Timber.i("Success. Now receiving location updates");
                                mRequestingLocationUpdates = true;
                            } else {
                                if (status.isCanceled() || status.isInterrupted()) {
                                    Timber.i(
                                            "Failure. Requesting for location updates was cancelled or interrupted");
                                } else {
                                    Timber.i(
                                            "Requesting for location updates results with status code %d",
                                            status.getStatusCode());
                                }
                                Timber.i(status.getStatusMessage());
                            }
                        }
                    });
        } else {
            Timber.i("GoogleClientApi it is not connected when requesting location updates");
        }
    }

    public void stopRequestingLocationUpdates() {
        if (isGoogleApiClientConnected() && mRequestingLocationUpdates) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Timber.i("Success. Location updates was stopped");
                                mRequestingLocationUpdates = false;
                            } else {
                                if (status.isCanceled() || status.isInterrupted()) {
                                    Timber.i(
                                            "Removing location updates was cancelled or interrupted");
                                } else {
                                    Timber.i(
                                            "Removing location updates results with status code %d",
                                            status.getStatusCode());
                                }
                                Timber.i(status.getStatusMessage());
                            }
                        }
                    });
        }
    }

    @Override public void onLocationChanged(Location location) {
        Timber.i("Location changed: %s", location.toString());
        Timber.i("New lat/lng: %s, %s", location.getLatitude(), location.getLongitude());

        if (mLastKnowLocation != null) {
            LatLng lastLatLng = new LatLng(mLastKnowLocation.getLatitude(),
                    mLastKnowLocation.getLongitude());
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (SphericalUtil.computeDistanceBetween(lastLatLng, currentLatLng) < getMinimumDistanceToLocationChange()) {
                return;
            }
        }

        mLastKnowLocation = location;
    }

    /**
     * Return the minimum distance in meters, to distinguish a change location between
     * two locations in the callback {@link #onLocationChanged(Location)}. By default
     * this method always returns 10 meters.
     * <br><br>Descendant classes may provide their own distance in meters.
     *
     * @return The minimum distance in meters
     */
    protected double getMinimumDistanceToLocationChange() {
        return 10;
    }

    /**
     * Define if a location request can be made as soon as {@link com.google.android.gms.common.api.GoogleApiClient}
     * is connected to Google Play Services.<br>The default implementation defines as
     * <code>false</code>. May be override to define no location requests when {@link
     * #onConnected(Bundle)}.
     */
    protected boolean canRequestLocationOnConnect() {
        return false;
    }

    @Override
    protected void restoreSavedState(Bundle inState) {
        super.restoreSavedState(inState);

        if (inState != null) {
            if (inState.keySet().contains(STATE_REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = inState.getBoolean(STATE_REQUESTING_LOCATION_UPDATES_KEY);
            }

            if (inState.keySet().contains(STATE_LAST_KNOWN_LOCATION_KEY)) {
                mLastKnowLocation = inState.getParcelable(STATE_LAST_KNOWN_LOCATION_KEY);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        outState.putParcelable(STATE_LAST_KNOWN_LOCATION_KEY, mLastKnowLocation);
    }
}
