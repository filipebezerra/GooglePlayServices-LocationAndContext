package com.github.filipebezerra.toys.playservices.geofence.constants;

import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 27/07/2015
 * @since #
 */
public final class Constants {
    private Constants() {}

    /**
     *
     */
    public static final HashMap<String, LatLng> LAT_LNG_MAP = new HashMap<>();

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    /**
     *
     */
    public static final float GEOFENCE_RADIUS_IN_METERS = 1; // 1 mile, 1.6 km

    /**
     * Initialize {@link @LAT_LNG_MAP}
     */
    static {
        LAT_LNG_MAP.put("Interna do Campus - 3358", new LatLng(-16.60105789, -49.27179134));
        LAT_LNG_MAP.put("Interna do Campus - 3359", new LatLng(-16.60111109, -49.27135171));
        LAT_LNG_MAP.put("Village do Campus 1", new LatLng(-16.6051744, -49.2689362));
        LAT_LNG_MAP.put("Village do Campus 2", new LatLng(-16.6010019, -49.2705359));
        LAT_LNG_MAP.put("Village do Campus 3", new LatLng(-16.601002, -49.270533));
    }
}
