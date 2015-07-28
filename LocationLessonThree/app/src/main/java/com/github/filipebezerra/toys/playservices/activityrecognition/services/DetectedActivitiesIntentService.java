package com.github.filipebezerra.toys.playservices.activityrecognition.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import java.util.ArrayList;
import timber.log.Timber;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 26/07/2015
 * @since #
 */
public class DetectedActivitiesIntentService extends IntentService {

    private static final String TAG = DetectedActivitiesIntentService.class.getName();

    public static final String EXTRA_ACTIVITIES = TAG + ".EXTRA_ACTIVITIES";
    public static final String ACTION_BROADCAST = TAG + ".ACTION_BROADCAST";

    /**
     * Creates an IntentService. Invoked by your subclass's constructor. Uses a TAG identifier of
     * this class, only for debugging purposes.
     */
    public DetectedActivitiesIntentService() {
        super(TAG);
        Timber.tag(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("onHandleIntent() from %s", TAG);

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        Timber.d(result.toString());
        Timber.d("%d probable activities", result.getProbableActivities().size());

        ArrayList<DetectedActivity> activities = new ArrayList<>(result.getProbableActivities());

        Intent sendResultIntent = new Intent(ACTION_BROADCAST);
        sendResultIntent.putParcelableArrayListExtra(EXTRA_ACTIVITIES, activities);

        boolean hasReceivers = LocalBroadcastManager.getInstance(this)
                .sendBroadcast(sendResultIntent);

        Timber.d("Has receivers %b", hasReceivers);
    }
}
