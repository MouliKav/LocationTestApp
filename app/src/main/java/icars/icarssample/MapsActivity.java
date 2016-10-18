package icars.icarssample;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String[] mCities;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter mAdapter;
    ListView mDrawerList;
    Boolean isLocationSet = false;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1340;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mCities = getResources().getStringArray(R.array.cities);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        //Checks if the location access is already enabled
        if (!checkPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            setUpMapIfNeeded();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);


        mMap.setOnMyLocationChangeListener(myLocationChangeListener());
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                if (!isLocationSet) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 20));
                    isLocationSet = true;
                }
            }
        };
    }

    //Checks for API compatibility
    public static boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        } else {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
    }

    //Callback for the location permission response
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setUpMapIfNeeded();
        } else {
            Toast.makeText(getApplicationContext(), "Please give access to location in settings", Toast.LENGTH_SHORT).show();
        }
    }


    public void onMenuClicked(View v) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mCities);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String city;
                mDrawerLayout.closeDrawers();
                if (position == 0) {
                    city = "San Francisco";
                } else {
                    city = "New York";
                }
                convertAddress(city);
            }

        });
    }

    //GeoCoder to convert the given address to LatLong values
    private void convertAddress(String cityName) {
        try {
            Geocoder coder = new Geocoder(MapsActivity.this, Locale.getDefault());
            LatLng p1 = null;
            List<Address> address;
            address = coder.getFromLocationName(cityName, 5);
            if (address == null) {

            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(p1.latitude, p1.longitude))
                    .title(cityName));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p1, 20));

        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

}


