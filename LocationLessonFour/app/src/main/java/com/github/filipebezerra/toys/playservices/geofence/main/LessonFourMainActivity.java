package com.github.filipebezerra.toys.playservices.geofence.main;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.OnClick;
import com.github.filipebezerra.toys.playservices.geofence.R;
import com.github.filipebezerra.toys.playservices.geofence.base.playservices.appcompatactivity.BaseLocationAppCompatActivity;
import com.github.filipebezerra.toys.playservices.geofence.constants.Constants;
import com.github.filipebezerra.toys.playservices.geofence.geofence.GeofenceTransitionsIntentService;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static com.github.filipebezerra.toys.playservices.geofence.base.playservices.util.GeofenceErrorMessages.getErrorMessage;
import static com.github.filipebezerra.toys.playservices.geofence.constants.Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS;
import static com.github.filipebezerra.toys.playservices.geofence.constants.Constants.GEOFENCE_RADIUS_IN_METERS;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

public class LessonFourMainActivity extends BaseLocationAppCompatActivity {

    private static final String TAG = LessonFourMainActivity.class.getName();

    private List<Geofence> mGeofences = new ArrayList<>();

    private boolean mRequestingGeofences = false;

    @Bind(R.id.main_container) protected ViewGroup mMainContainer;
    @Bind(R.id.fab) protected FloatingActionButton mAddOrRemoveRequestFab;

    @Override
    protected int getContentLayoutResId() {
        return R.layout.activity_lesson_four_main;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @NonNull
    @Override
    protected List<Api<Api.ApiOptions.NoOptions>> getUsedGoogleApis() {
        return Collections.singletonList(LocationServices.API);
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setTitle(R.string.title_lesson_four_main_activity);
        populateGeofenceList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lesson_four_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void addOrRemoveGeofencesButtonHandler() {
        if (! isGoogleApiClientConnected()) {
            Snackbar.make(mMainContainer, getString(R.string.not_connected), LENGTH_SHORT)
                    .show();
            return;
        }

        if (mRequestingGeofences) {
            LocationServices.GeofencingApi
                    .removeGeofences(mGoogleApiClient, createPendingIntent())
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Snackbar.make(mMainContainer, "Geofences Removed", LENGTH_SHORT)
                                        .show();
                                mRequestingGeofences = false;
                                mAddOrRemoveRequestFab.setImageResource(R.drawable.ic_add_white_24dp);
                            } else {
                                Snackbar.make(mMainContainer, getErrorMessage(status.getStatusCode()),
                                        LENGTH_LONG).show();
                            }
                        }
                    });
            mRequestingGeofences = false;
        } else {
            LocationServices.GeofencingApi
                    .addGeofences(mGoogleApiClient, createGeofencingRequest(), createPendingIntent())
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Snackbar.make(mMainContainer, "Geofences Added", LENGTH_SHORT)
                                        .show();
                                mRequestingGeofences = true;
                                mAddOrRemoveRequestFab.setImageResource(R.drawable.ic_clear_white_24dp);
                            } else {
                                Snackbar.make(mMainContainer, getErrorMessage(status.getStatusCode()),
                                        LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    /**
     *
     * @return
     */
    private GeofencingRequest createGeofencingRequest() {
        return new GeofencingRequest.Builder()
                .addGeofences(mGeofences)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    /**
     *
     * @return
     */
    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     *
     */
    private void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.LAT_LNG_MAP.entrySet()) {
            mGeofences.add(new Geofence.Builder()
                            .setRequestId(entry.getKey())
                            .setCircularRegion(
                                    entry.getValue().latitude,
                                    entry.getValue().longitude,
                                    GEOFENCE_RADIUS_IN_METERS
                            )
                            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                            .setTransitionTypes(GEOFENCE_TRANSITION_ENTER | GEOFENCE_TRANSITION_EXIT)
                            .build()
            );
        }
    }
}
