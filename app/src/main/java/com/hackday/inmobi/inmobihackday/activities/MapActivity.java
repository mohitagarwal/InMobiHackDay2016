package com.hackday.inmobi.inmobihackday.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hackday.inmobi.inmobihackday.Config;
import com.hackday.inmobi.inmobihackday.R;
import com.hackday.inmobi.inmobihackday.helpers.HttpConnection;
import com.hackday.inmobi.inmobihackday.helpers.LatLngInterpolator;
import com.hackday.inmobi.inmobihackday.helpers.PathJSONParser;
import com.hackday.inmobi.inmobihackday.networking.RetroFitApiService;
import com.hackday.inmobi.inmobihackday.networking.model.User;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.AcceptRideRequestPOJO;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.AvailableRides;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.RegisterRidePOJO;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.RideDetails;
import com.hackday.inmobi.inmobihackday.networking.responsemodel.UpdateRiderLocationPOJO;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    public static final int REQUEST_CODE_LOCATION_SEARCH = 0;
    private static final float ZOOM_VALUE = 12;

    private GoogleMap mMap;

    private android.location.Location sourceLocation;
    private Double destinationLat;
    private Double destinationLng;
    private String destinationAddressString;
    private Marker currentPositionMarker;
    private LatLng currentLatLng;

    private boolean isMapReady = false;

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView editText = (TextView) findViewById(R.id.editText_destination);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, LocationSearchActivity.class);
                MapActivity.this.startActivityForResult(intent, REQUEST_CODE_LOCATION_SEARCH);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        tryToPlotPath();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOCATION_SEARCH && resultCode == Activity.RESULT_OK) {
            destinationLat = data.getDoubleExtra(LocationSearchActivity.INTENT_EXTRA_LAT, 12.9667);
            destinationLng = data.getDoubleExtra(LocationSearchActivity.INTENT_EXTRA_LONG, 77.5667);
            destinationAddressString = data.getStringExtra(LocationSearchActivity.INTENT_EXTRA_ADDRESS_STRING);
            String address = destinationAddressString;
            TextView editTextDestination = (TextView) findViewById(R.id.editText_destination);
            editTextDestination.setText(address);
            tryToPlotPath();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navdrawer, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng bangalore = new LatLng(12.9667, 77.5667);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bangalore));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_VALUE));

        if (currentLatLng != null && currentPositionMarker == null) {
            addCurrentLocationMarker();
        }
        isMapReady = true;
    }


    private void tryToPlotPath() {
        if (sourceLocation == null || destinationLat == null || destinationLng == null) {
            return;
        }
        mMap.clear();
        LatLng sourceLatLng = new LatLng(sourceLocation.getLatitude(), sourceLocation.getLongitude());
        LatLng destinationLatLng = new LatLng(destinationLat, destinationLng);

        mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("My source"));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(destinationAddressString));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, ZOOM_VALUE));

        addCurrentLocationMarker();

        (new ReadTask()).execute(getMapsApiDirectionsUrl(sourceLatLng, destinationLatLng));
    }

    private String getMapsApiDirectionsUrl(LatLng sourceLatLng, LatLng destinationLatLng) {
        String waypoints = "origin="
                + sourceLatLng.latitude + "," + sourceLatLng.longitude
                + "&destination="
                + destinationLatLng.latitude + "," + destinationLatLng.longitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    private void testAllAPI() {
//        getRides();
        registerRide();
        updateRiderLocation();
        acceptRideRequest();

    }

    private void getRides(com.hackday.inmobi.inmobihackday.networking.model.Location fromLocation, com.hackday.inmobi.inmobihackday.networking.model.Location toLocation) {
        Call<List<AvailableRides>> call = RetroFitApiService.getInstance().requestRide(Config.USER_ID,
                System.currentTimeMillis(),
                fromLocation.getLat(), fromLocation.getLng(),
                toLocation.getLat(), toLocation.getLng());

        call.enqueue(new Callback<List<AvailableRides>>() {
            @Override
            public void onResponse(Call<List<AvailableRides>> call, Response<List<AvailableRides>> response) {
                Toast.makeText(MapActivity.this, "Success getRides", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<AvailableRides>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Failure getRides", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerRide() {
        RegisterRidePOJO payload = new RegisterRidePOJO();
        payload.setTimestamp(System.currentTimeMillis());

        Call<RideDetails> call = RetroFitApiService.getInstance().registerRide(payload);
        call.enqueue(new Callback<RideDetails>() {
            @Override
            public void onResponse(Call<RideDetails> call, Response<RideDetails> response) {
                Toast.makeText(MapActivity.this, "Success registerRide", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<RideDetails> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Failure registerRide", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRiderLocation() {
        UpdateRiderLocationPOJO payload = new UpdateRiderLocationPOJO();

        User user = new User();
        user.setUserId("4");

        payload.setUser(user);

        Call<String> call = RetroFitApiService.getInstance().updateRiderLocation(payload);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(MapActivity.this, "Success updateRiderLocation", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Failure updateRiderLocation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptRideRequest() {
        AcceptRideRequestPOJO payload = new AcceptRideRequestPOJO();

        User user = new User();
        user.setUserId("4");

        payload.setRequestingUser(user);

        Call<String> call = RetroFitApiService.getInstance().acceptRideRequest(payload);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(MapActivity.this, "Success acceptRideRequest", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Failure acceptRideRequest", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // *********************************************************************************************
    // * LocationListener
    // *********************************************************************************************

    @Override
    public void onLocationChanged(android.location.Location location) {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (isMapReady && currentPositionMarker == null) {
            addCurrentLocationMarker();
        }
        if (currentPositionMarker != null) {
            currentPositionMarker.setPosition(currentLatLng);
            // animateMarkerToICS(currentPositionMarker, currentLatLng, new LatLngInterpolator.Spherical());
        }

        if (sourceLocation == null) {
            sourceLocation = location;
            tryToPlotPath();
        }
    }

    static void animateMarkerToICS(Marker marker, LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(100);
        animator.start();
    }

    private void addCurrentLocationMarker() {
        currentPositionMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("I'm here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM_VALUE));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new PathPlotterTask().execute(result);
        }
    }

    private class PathPlotterTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(15);
                polyLineOptions.color(Color.parseColor("#00A0FF"));
            }

            mMap.addPolyline(polyLineOptions);
        } // onPostExecute
    } // PathPlotterTask
}
