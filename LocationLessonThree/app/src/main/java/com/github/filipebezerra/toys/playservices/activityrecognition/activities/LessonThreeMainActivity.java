package com.github.filipebezerra.toys.playservices.activityrecognition.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.filipebezerra.toys.playservices.activityrecognition.R;
import com.github.filipebezerra.toys.playservices.activityrecognition.services.DetectedActivitiesIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import java.util.List;
import timber.log.Timber;

public class LessonThreeMainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = LessonThreeMainActivity.class.getName();

    private GoogleApiClient mGoogleApiClient;

    private ActivityDetectionBroadcastReceiver mActivityDetectionBroadcastReceiver;

    private static final int DEFAULT_INTERVAL = 1000;

    @Bind(R.id.main_container) protected ViewGroup mViewContainer;
    @Bind(R.id.request_activity_updates_button) protected Button mRequestActivityUpdatesButton;
    @Bind(R.id.remove_activity_updates_button) protected Button mRemoveActivityUpdatesButton;
    @Bind(R.id.detected_activities) protected TextView mActivitiesDetectedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_three_main);
        ButterKnife.bind(this);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_lesson_three_main_activity));
        Timber.tag(TAG);

        buildGoogleApiClient();
        mActivityDetectionBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
    }

    /**
     * Creates the GoogleApiClient to access Google Play Services
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connects with the Google Play Services
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mActivityDetectionBroadcastReceiver,
                        new IntentFilter(DetectedActivitiesIntentService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mActivityDetectionBroadcastReceiver);

        super.onPause();
    }

    @Override
    protected void onStop() {
        // Disconnects from Google Play Services if is connected.
        // Needs to be explicitly called even if the connection was broken
        if (mGoogleApiClient.isConnected()) {
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
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.d("Connected to Google Play Services");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Timber.d("Connection suspended with cause code %d", cause);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Timber.d("Connection failed with result %s", connectionResult.toString());
    }

    @OnClick(R.id.request_activity_updates_button)
    public void requestActivityUpdatesButtonHandler() {
        if (! mGoogleApiClient.isConnected()) {
            Snackbar.make(mViewContainer, getString(R.string.not_connected), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient,
                DEFAULT_INTERVAL, getActivityDetectionPendingIntent()).setResultCallback(this);

        mRequestActivityUpdatesButton.setEnabled(false);
        mRemoveActivityUpdatesButton.setEnabled(true);
    }

    @OnClick(R.id.remove_activity_updates_button)
    public void removeActivityUpdatesButtonHandler() {
        if (! mGoogleApiClient.isConnected()) {
            Snackbar.make(mViewContainer, getString(R.string.not_connected), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient,
                getActivityDetectionPendingIntent()).setResultCallback(this);

        mRequestActivityUpdatesButton.setEnabled(true);
        mRemoveActivityUpdatesButton.setEnabled(false);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent serviceIntent = new Intent(this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Timber.d("Successfully added activity detection");
        } else {
            Timber.d("Error adding or removing activity detection: %s", status.getStatusMessage());
        }
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("onReceive() from ActivityDetectionBroadcastReceiver");

            List<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(
                    DetectedActivitiesIntentService.EXTRA_ACTIVITIES);

            StringBuffer activitiesText = new StringBuffer();
            for (DetectedActivity activity : detectedActivities) {
                activitiesText.append(getActivityText(activity.getType()))
                        .append(" ")
                        .append(activity.getConfidence())
                        .append("%\n");
            }

            mActivitiesDetectedTextView.setText(activitiesText);
        }

        public String getActivityText(final int detectedActivityType) {
            Resources resources = getResources();
            switch (detectedActivityType) {
                case DetectedActivity.IN_VEHICLE:
                    return resources.getString(R.string.in_vehicle);
                case DetectedActivity.ON_BICYCLE:
                    return resources.getString(R.string.on_bicycle);
                case DetectedActivity.ON_FOOT:
                    return resources.getString(R.string.on_foot);
                case DetectedActivity.STILL:
                    return resources.getString(R.string.still);
                case DetectedActivity.TILTING:
                    return resources.getString(R.string.tilting);
                case DetectedActivity.WALKING:
                    return resources.getString(R.string.walking);
                case DetectedActivity.RUNNING:
                    return resources.getString(R.string.running);
                case DetectedActivity.UNKNOWN:
                    return resources.getString(R.string.unknown);
                default:
                    return 
                            resources.getString(R.string.unidentifiable_activity);
            }
        }
    }
}
