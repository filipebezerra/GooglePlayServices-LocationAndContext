package com.github.filipebezerra.toys.playservices.mylocation.main;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.filipebezerra.toys.playservices.mylocation.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import timber.log.Timber;

public class LessonOneMainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = LessonOneMainActivity.class.getName();

    private static final String KEY_LAST_LOCATION = TAG + ".KEY_LAST_LOCATION";
    private static final String KEY_LAST_UPDATE_TIME = TAG + ".KEY_LAST_UPDATE_TIME";

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private Calendar mLastUpdate;

    @Bind(R.id.toolbar) protected Toolbar mToolbar;
    @Bind(R.id.latitude_value_text_view) protected TextView mLatitudeTextView;
    @Bind(R.id.longitude_value_text_view) protected TextView mLongitudeTextView;
    @Bind(R.id.last_update_text_view) protected TextView mLastUpdateTextView;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_lesson_one_main);
        ButterKnife.bind(this);
        Timber.tag(TAG);

        setSupportActionBar(mToolbar);

        buildGoogleApiClient();

        if (inState != null && inState.containsKey(KEY_LAST_LOCATION)) {
            mLastLocation = inState.getParcelable(KEY_LAST_LOCATION);
            mLastUpdate.setTimeInMillis(inState.getLong(KEY_LAST_UPDATE_TIME));
            updateUiLocationIfHasLocation();
        }

        setTitle(getString(R.string.title_lesson_two_main_activity));
    }

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
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLastLocation != null) {
            outState.putParcelable(KEY_LAST_LOCATION, mLastLocation);
            outState.putLong(KEY_LAST_UPDATE_TIME, mLastUpdate.getTimeInMillis());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.d("Connected to Google Play Services");
        retrieveLastLocation();
        updateUiLocationIfHasLocation();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Timber.d("Connection with Google Play Services suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Timber.d("Connection with Google Play Services failed");
    }

    @OnClick(R.id.my_location_fab)
    public void requestMyLocation() {
        retrieveLastLocation();
        updateUiLocationIfHasLocation();
    }

    private void updateUiLocationIfHasLocation() {
        if (mLastLocation != null) {
            mLatitudeTextView.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(mLastLocation.getLongitude()));
            mLastUpdateTextView.setText(SimpleDateFormat.getDateTimeInstance()
                    .format(mLastUpdate.getTime()));
        }
    }

    private void retrieveLastLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLastUpdate = Calendar.getInstance();
        }
    }
}
