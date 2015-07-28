package com.github.filipebezerra.toys.playservices.geofence.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.github.filipebezerra.toys.playservices.geofence.R;
import com.github.filipebezerra.toys.playservices.geofence.main.LessonFourMainActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.List;
import timber.log.Timber;

import static com.github.filipebezerra.toys.playservices.geofence.base.playservices.util.GeofenceErrorMessages.getErrorMessage;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 27/07/2015
 * @since #
 */
public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = GeofenceTransitionsIntentService.class.getName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GeofenceTransitionsIntentService() {
        super(TAG);
        Timber.tag(TAG);
    }

    @Override protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Timber.e(getErrorMessage(geofencingEvent.getErrorCode()));
            return;
        }

        final int transition = geofencingEvent.getGeofenceTransition();
        Timber.i("Transition type of the geofence is %d", transition);

        switch (transition) {
            case GEOFENCE_TRANSITION_ENTER:
                Timber.i("This transition type indicating that the user enters the geofence(s).");
                break;
            case GEOFENCE_TRANSITION_EXIT:
                Timber.i("This transition type indicating that the user exits the geofence(s).");
                break;
            case GEOFENCE_TRANSITION_DWELL:
                Timber.i("This transition type indicating that the user enters and dwells in geofences for a given period of time");
                break;
        }

        final List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
        final String geofenceTransitionDetails = getGeofenceTransitionDetails(transition, geofences);

        sendNotification(geofenceTransitionDetails);
    }

    private void sendNotification(final String notificationDetails) {
        Intent resultIntent = new Intent(this, LessonFourMainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);

        // Add the back stack to the stack
        taskStackBuilder.addParentStack(LessonFourMainActivity.class);

        // Add the Intent that starts the Activity from the notification
        taskStackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setSmallIcon(android.R.drawable.ic_menu_myplaces)
                .setAutoCancel(true)
                //.setShowWhen()
                //.addAction()
                //.addExtras()
                //.setTicker()
                //.setSound()
                //.setVibrate()
                //.setLights()
                //.setCategory()
                //.setColor()
                //.setContent()
        ;

        try {
            builder.setLargeIcon(Picasso.with(this).load(R.mipmap.ic_launcher).get());
        } catch (IOException e) {
            Timber.e(e, "Error trying to load image resource into notification large icon.");
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private String getGeofenceTransitionDetails(int transition, List<Geofence> geofences) {
        StringBuilder buffer = new StringBuilder(getTransitionString(transition));

        for (Geofence geofence : geofences) {
            buffer.append(geofence.getRequestId()).append(", ");
        }

        return buffer.toString();
    }

    private String getTransitionString(final int transition) {
        switch (transition) {
            case GEOFENCE_TRANSITION_ENTER:
                return "User enters: ";
            case GEOFENCE_TRANSITION_EXIT:
                return "User exits: ";
            case GEOFENCE_TRANSITION_DWELL:
                return "User enters and dwells for a given period of time: ";
            default:
                return "Unknown transition";
        }
    }
}
