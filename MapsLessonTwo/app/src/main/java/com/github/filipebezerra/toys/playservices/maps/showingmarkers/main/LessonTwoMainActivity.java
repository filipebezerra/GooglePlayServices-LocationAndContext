package com.github.filipebezerra.toys.playservices.maps.showingmarkers.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.filipebezerra.toys.playservices.maps.showingmarkers.R;
import com.github.filipebezerra.toys.playservices.maps.showingmarkers.util.ImmersiveMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

public class LessonTwoMainActivity extends AppCompatActivity
    implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = LessonTwoMainActivity.class.getName();

    private boolean mIsMapReady = false;
    private GoogleMap mGoogleMap;

    private static final LatLng sLatLngCondominioVillage = new LatLng(-16.6051744, -49.2689362);
    private static final LatLng sLatLngSeattle = new LatLng(47.6204, -122.3491);
    private static final LatLng sLatLngNewYork = new LatLng(-40.7127, -74.0059);
    private static final LatLng sLatLngDublin = new LatLng(-53.3478, -6.2597);

    private static final Map<Integer, LatLng> sBusStopPoints = new HashMap<>();

    static {
        sBusStopPoints.put(3359, new LatLng(-16.60111109, -49.27135171));
        sBusStopPoints.put(3361, new LatLng(-16.59908693, -49.27658814));
        sBusStopPoints.put(3360, new LatLng(-16.59934693, -49.27625791));
        sBusStopPoints.put(3358, new LatLng(-16.60105789, -49.27179134));
        //sBusStopPoints.put(4718, new LatLng(-16.603079, -49.267683));
        //sBusStopPoints.put(6364, new LatLng(-16.603008, -49.266238));
        //sBusStopPoints.put(6363, new LatLng(-16.602799, -49.266303));
        //sBusStopPoints.put(3357, new LatLng(-16.602507, -49.267705));
        //sBusStopPoints.put(6517, new LatLng(-16.601785, -49.266453));
        //sBusStopPoints.put(6516, new LatLng(-16.601713, -49.26648));
        sBusStopPoints.put(3359, new LatLng(-16.60111109, -49.27135171));
    }

    @Bind(R.id.main_container) protected ViewGroup mMainContainer;
    @Bind(R.id.drawer_layout) protected DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view) protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_two_main);
        ButterKnife.bind(this);
        setTitle(R.string.title_activity_lesson_two_main);
        Timber.tag(TAG);

        navigationView.setNavigationItemSelectedListener(this);

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
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Timber.i("Map is ready");
        mIsMapReady = true;
        mGoogleMap = googleMap;

        mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(final Marker marker) {
                Snackbar.make(mMainContainer, String.format("onMarkerDragStart() -> %s/%s",
                        marker.getId(), marker.getTitle()), LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDrag(final Marker marker) {
                Snackbar.make(mMainContainer, String.format("onMarkerDrag() -> %s/%s",
                        marker.getId(), marker.getTitle()), LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDragEnd(final Marker marker) {
                Snackbar.make(mMainContainer, String.format("onMarkerDragEnd() -> %s/%s",
                        marker.getId(), marker.getTitle()), LENGTH_SHORT).show();
            }
        });

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(final CameraPosition cameraPosition) {
                final double distance = SphericalUtil.computeDistanceBetween(cameraPosition.target,
                        sLatLngCondominioVillage);
                if (distance < 1) {
                    mGoogleMap.addPolygon(new PolygonOptions().geodesic(true)
                            .addAll(sBusStopPoints.values()));

                    for (Map.Entry<Integer, LatLng> next : sBusStopPoints.entrySet()) {
                        addSimpleMarker(next.getValue(), String.valueOf(next.getKey()),
                                R.drawable.ic_station_bus);
                    }
                }
            }
        });

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

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        menuItem.setCheckable(true);
        mDrawerLayout.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()) {
            case R.id.nav_current_location:
                Snackbar.make(mMainContainer, "Going to current location!", LENGTH_SHORT).show();
                moveCamera(sLatLngCondominioVillage);
                return true;
            case R.id.nav_seattle:
                Snackbar.make(mMainContainer, "Going to Seattle!", LENGTH_SHORT).show();
                moveCamera(sLatLngSeattle);
                return true;
            case R.id.nav_new_york:
                Snackbar.make(mMainContainer, "Going to New York!", LENGTH_SHORT).show();
                moveCamera(sLatLngNewYork);
                return true;
            case R.id.nav_dublin:
                Snackbar.make(mMainContainer, "Going to Dublin!", LENGTH_SHORT).show();
                moveCamera(sLatLngDublin);
                return true;
            case R.id.nav_move_camera:
                return true;
            case R.id.nav_animate_camera:
                return true;
            default:
                return false;
        }
    }

    private void moveCamera(final LatLng latLng) {
        if (checkAndNotifyIfMapIsNotReady()) {
            CameraPosition cameraPosition = CameraPosition.builder()
                    .tilt(45)
                    .bearing(0)
                    .zoom(14)
                    .target(latLng)
                    .build();

            if (navigationView.getMenu().findItem(R.id.nav_animate_camera).isChecked()) {
                animateCameraIfMapIsReady(cameraPosition);
            } else {
                moveCameraIfMapIsReady(cameraPosition);
            }
        }
    }

    private void moveCameraIfMapIsReady(final CameraPosition cameraPosition) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void animateCameraIfMapIsReady(final CameraPosition cameraPosition) {
        final LatLng target = cameraPosition.target;
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                10000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        final String targetName = getTargetName(target);

                        Snackbar.make(mMainContainer,
                                String.format("Welcome to %s!", targetName), LENGTH_LONG)
                                .show();

                        addCircle(target);
                        addMarker(target);
                    }

                    @Override
                    public void onCancel() {
                        Snackbar.make(mMainContainer, String.format("You cancel your travel to %s!",
                                getTargetName(target)), LENGTH_SHORT).show();
                    }
                });
    }

    private void addCircle(final LatLng latLng) {
        mGoogleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(500)
                .strokeColor(Color.BLUE)
                .strokeWidth(5)
                .fillColor(Color.argb(64, 0, 0, 255))
        );
    }

    private void addSimpleMarker(final LatLng latLng, final String title,
            final @DrawableRes int icon) {
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .draggable(false)
                .title(title)
                .icon(getMakerIconFromResource(icon));

        mGoogleMap.addMarker(marker);
    }

    private void addMarker(final LatLng latLng) {
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .draggable(false)
                .title(String.format("Hi from %s", getTargetName(latLng)))
                .snippet(getMarkerSnippet())
                .icon(getMakerIcon());

        mGoogleMap.addMarker(marker);
    }

    private String getMarkerSnippet() {
        String formattedDate = DateFormat.getTimeFormat(LessonTwoMainActivity.this)
                .format(new Date());

        return String.format("You've been here at %s", formattedDate);
    }

    private BitmapDescriptor getMakerIconFromResource(@DrawableRes int iconResId) {
        return BitmapDescriptorFactory.fromResource(iconResId);
    }

    private BitmapDescriptor getMakerIcon() {
        if (mGoogleMap.getMapType() == MAP_TYPE_NORMAL) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_black_24dp);
        } else {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_white_24dp);
        }
    }

    private String getTargetName(final LatLng latLng) {
        if (latLng.equals(sLatLngCondominioVillage)) {
            return "Village do Campus";
        } else if (latLng.equals(sLatLngSeattle)) {
            return "Seattle";
        } else if (latLng.equals(sLatLngNewYork)) {
            return "New York";
        } else if (latLng.equals(sLatLngDublin)) {
            return "Dublin";
        } else {
            return "Unknown";
        }
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
