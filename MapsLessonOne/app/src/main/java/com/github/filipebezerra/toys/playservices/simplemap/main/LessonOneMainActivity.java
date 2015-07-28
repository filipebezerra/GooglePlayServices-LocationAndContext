package com.github.filipebezerra.toys.playservices.simplemap.main;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.filipebezerra.toys.playservices.simplemap.R;
import com.github.filipebezerra.toys.playservices.simplemap.util.ImmersiveMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import timber.log.Timber;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

public class LessonOneMainActivity extends AppCompatActivity
    implements OnMapReadyCallback {

    private boolean mIsMapReady = false;
    private GoogleMap mGoogleMap;

    private static final LatLng sLatLngCondominioVillage = new LatLng(-16.6051744, -49.2689362);

    @Bind(R.id.main_container) protected ViewGroup mMainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_one_main);
        ButterKnife.bind(this);
        setTitle(R.string.title_activity_lesson_one_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ImmersiveMode.getInstance(this).start();
        } catch (UnsupportedOperationException e) {
            Timber.e(e, "Can't start immersive mode because it's not supported here");
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Timber.i("Map is ready");
        mIsMapReady = true;
        mGoogleMap = googleMap;

        moveCamera(sLatLngCondominioVillage);
    }

    @OnClick(R.id.fab) public void changeMapType() {
        if (checkAndNotifyIfMapIsNotReady()) {
            Snackbar.make(mMainContainer, String.format("You get %s view of the map", setMapType()),
                    LENGTH_SHORT).show();
        }
    }

    private String setMapType() {
        String mapTypeStr = "";

        switch (mGoogleMap.getMapType()) {
            case MAP_TYPE_NORMAL:
                mGoogleMap.setMapType(MAP_TYPE_SATELLITE);
                mapTypeStr = "Satellite";
                break;
            case MAP_TYPE_SATELLITE:
                mGoogleMap.setMapType(MAP_TYPE_HYBRID);
                mapTypeStr = "Hybrid";
                break;
            case MAP_TYPE_HYBRID:
                mGoogleMap.setMapType(MAP_TYPE_NORMAL);
                mapTypeStr = "Normal";
                break;
        }

        return mapTypeStr;
    }

    private void moveCamera(final LatLng latLng) {
        if (checkAndNotifyIfMapIsNotReady()) {
            CameraPosition cameraPosition = CameraPosition.builder()
                    .tilt(65)
                    .bearing(175)
                    .zoom(17)
                    .target(latLng)
                    .build();

            animateCameraIfMapIsReady(cameraPosition);
        }
    }

    private void moveCameraIfMapIsReady(final CameraPosition cameraPosition) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void animateCameraIfMapIsReady(final CameraPosition cameraPosition) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                10000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        Snackbar.make(mMainContainer, "Welcome to Village do Campus!", LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onCancel() {
                        Snackbar.make(mMainContainer, "You cancel your travel to Village do Campus!",
                                LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkAndNotifyIfMapIsNotReady() {
        if (!mIsMapReady) {
            Snackbar.make(mMainContainer, "Sorry, map is not ready!", LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
