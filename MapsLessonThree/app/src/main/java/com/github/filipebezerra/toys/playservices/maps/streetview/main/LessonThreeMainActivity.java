package com.github.filipebezerra.toys.playservices.maps.streetview.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import com.github.filipebezerra.systemuihelper.SystemUiHelper;
import com.github.filipebezerra.toys.playservices.maps.streetview.R;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import timber.log.Timber;

import static com.github.filipebezerra.systemuihelper.SystemUiHelper.FLAG_IMMERSIVE_STICKY;
import static com.github.filipebezerra.systemuihelper.SystemUiHelper.LEVEL_HIDE_STATUS_BAR;

public class LessonThreeMainActivity extends AppCompatActivity
        implements OnStreetViewPanoramaReadyCallback, SystemUiHelper.OnVisibilityChangeListener {

    private static final String TAG = LessonThreeMainActivity.class.getName();

    private StreetViewPanorama mStreetViewPanorama;

    private SystemUiHelper mSystemUiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(R.string.title_activity_lesson_three_main);
        Timber.tag(TAG);

        SupportStreetViewPanoramaFragment fragment = (SupportStreetViewPanoramaFragment)
                getSupportFragmentManager().findFragmentById(R.id.street_view_panorama_fragment);

        fragment.getStreetViewPanoramaAsync(this);

        mSystemUiHelper = new SystemUiHelper(this, LEVEL_HIDE_STATUS_BAR, FLAG_IMMERSIVE_STICKY,
                this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSystemUiHelper.isShowing()) {
            mSystemUiHelper.hide();
        }
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;

        mStreetViewPanorama.setPosition(new LatLng(36.0579667, -112.1430996));

        StreetViewPanoramaCamera camera = StreetViewPanoramaCamera.builder()
                .bearing(180)
                .build();

        mStreetViewPanorama.animateTo(camera, 1000);
    }

    @Override
    public void onVisibilityChange(final boolean visible) {
        if (visible) {
            mSystemUiHelper.delayHide(3000);
        }
    }
}
