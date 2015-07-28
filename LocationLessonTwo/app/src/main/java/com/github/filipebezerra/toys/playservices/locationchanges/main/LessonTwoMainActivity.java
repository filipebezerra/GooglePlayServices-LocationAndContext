package com.github.filipebezerra.toys.playservices.locationchanges.main;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.filipebezerra.toys.playservices.locationchanges.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import timber.log.Timber;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class LessonTwoMainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
        ResultCallback<Status> {

    private static final int DEFAULT_INTERVAL = 1000;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private boolean mRequestingLocationUpdates = false;

    private Location mLastLocation;

    @Bind(R.id.toolbar) protected Toolbar mToolbar;
    @Bind(R.id.latitude_value_textview) protected TextView mLatitudeValueTextView;
    @Bind(R.id.longitude_value_textview) protected TextView mLongitudeValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(R.string.title_activity_lesson_two_main);

        setSupportActionBar(mToolbar);

        // build the Google Api client when Activity has been created
        buildGoogleApiClient();
    }

    /**
     * Creates the GoogleApiClient to access Google Play Services
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connects with the Google Play Services
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnects from Google Play Services if is connected.
        // Needs to be explicitly called even if the connection was broken
        if (mGoogleApiClient.isConnected()) {
            stopRequestingLocationUpdatesIfNeed();
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.action_interval:
                new MaterialDialog.Builder(this)
                        .title("Select request update interval")
                        .items(R.array.request_interval_list)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which,
                                    CharSequence text) {
                                long millis;

                                switch (which) {
                                    // 1 second
                                    case 0:
                                        millis = DEFAULT_INTERVAL;
                                        break;
                                    // 3 seconds
                                    case 1:
                                        millis = 3000;
                                        break;
                                    // 5 seconds
                                    case 2:
                                        millis = 5000;
                                        break;
                                    // 10 seconds
                                    case 3:
                                        millis = 10000;
                                        break;
                                    // off
                                    default:
                                        stopRequestingLocationUpdatesIfNeed();
                                        return;
                                }

                                startRequestingLocationUpdates(millis, true);

                                Snackbar.make(mLongitudeValueTextView,
                                        "Location updates interval has changed",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Connected with Google Play Services, at this moment you can call features from
    // Google Play Services like Location.
    @Override
    public void onConnected(Bundle bundle) {
        Timber.i("Connected with Google Play Services");
        Snackbar.make(mLongitudeValueTextView, "Connected with Google Play Services",
                Snackbar.LENGTH_SHORT).show();

        mLastLocation = FusedLocationApi.getLastLocation(mGoogleApiClient);
        updateUiIfHasLocation();

        startRequestingLocationUpdates(DEFAULT_INTERVAL, false);
    }

    // Previously connection has been suspended for some reason, but it's not broken
    @Override
    public void onConnectionSuspended(int cause) {
        Snackbar.make(mLongitudeValueTextView, "Connection with Google Play Services suspended",
                Snackbar.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    // Callback to a physical location change
    @Override
    public void onLocationChanged(Location location) {
        Timber.i("Location changed: %s", location.toString());
        Timber.i("New lat/lng is %s,%s", location.getLatitude(), location.getLongitude());

        if (mLastLocation != null) {
            final LatLng lastLatLng = new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude());
            final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // less than 10 meters, returns
            if (SphericalUtil.computeDistanceBetween(lastLatLng, currentLatLng) < 10) {
                Timber.i("The new location is less than 10 meters far from last location");
                return;
            }
        }

        mLastLocation = location;
        updateUiIfHasLocation();
        Snackbar.make(mLongitudeValueTextView, "Location changed", Snackbar.LENGTH_SHORT).show();
    }

    private void updateUiIfHasLocation() {
        if (mLastLocation != null) {
            mLatitudeValueTextView.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeValueTextView.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    private void stopRequestingLocationUpdatesIfNeed() {
        if (mRequestingLocationUpdates) {
            FusedLocationApi.removeLocationUpdates(mGoogleApiClient, LessonTwoMainActivity.this);
            mRequestingLocationUpdates = false;
        }
    }

    private void startRequestingLocationUpdates(final long interval, final boolean forceRequest) {
        if (forceRequest) {
            stopRequestingLocationUpdatesIfNeed();
        } else if (mRequestingLocationUpdates) {
            return;
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(interval)
                .setFastestInterval(interval);

        FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        mRequestingLocationUpdates = true;
    }

    // Connection with Google Play Services broken
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Snackbar.make(mLongitudeValueTextView, "Connection with Google Play Services failed",
                Snackbar.LENGTH_LONG).show();
    }

    // Callback from a new Location Updates request
    @Override
    public void onResult(Status status) {
        String strStatus;
        if (status.isInterrupted()) {
            strStatus = "interrupted";
        } else if (status.isCanceled()) {
            strStatus = "canceled";
        } else if (status.isSuccess()) {
            strStatus = "success";
        } else {
            strStatus = "unknown";
        }
        Snackbar.make(mLongitudeValueTextView,
                String.format("Location updates status is %s with code %d",
                        strStatus, status.getStatusCode()), Snackbar.LENGTH_LONG).show();
    }
}
