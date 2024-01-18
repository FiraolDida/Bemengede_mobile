package com.example.bemengede;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String URL = "http://beta.bemengede.ml/stations";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-160), new LatLng(71,136));

    private Boolean locationPermissionsGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private AutoCompleteTextView searchText;
    private ImageView findMyLocation;
    private RelativeLayout progressBarLayout;
    private RelativeLayout relativeLayout;
    private BottomNavigationView bottomNavigationView;

    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient googleApiClient;
    private View mapView;

    private Station station;
    ArrayList<Station> arrayList;

    //for test
    protected GeoDataClient mGeoDataClient;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLocationPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        searchText = findViewById(R.id.inputSearch);
        findMyLocation = findViewById(R.id.findMyLocation);
        progressBarLayout = findViewById(R.id.relativeLayoutProgressBar);
        relativeLayout = findViewById(R.id.relativeLayout);
        bottomNavigationView = findViewById(R.id.navigation);

        arrayList = new ArrayList<>();

        //for test
        mGeoDataClient = Places.getGeoDataClient(this,null);
        searchText.setOnItemClickListener(mAutocompleteClickListener);
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
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        googleMap.setPadding(0,120,0,0);

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0,0,30,30);

        mMap = googleMap;

        if (locationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);
            init();
        }else{
            // Add a marker in Ethiopia and move the camera
            Toast.makeText(this, "Please enable location!", Toast.LENGTH_SHORT).show();
            LatLng ethiopia = new LatLng(9, 38.76);
            mMap.addMarker(new MarkerOptions().position(ethiopia).title("Marker in Ethiopia"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ethiopia));
        }

    }

    private void init(){
        Log.d(TAG, "init: initializing");

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient,
                LAT_LNG_BOUNDS, null);
        searchText.setAdapter(placeAutocompleteAdapter);

//        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH
//                    || actionId == EditorInfo.IME_ACTION_DONE
//                    || event.getAction() == KeyEvent.ACTION_DOWN
//                    || event.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    //execute function for searching
//                    geoLocate();
//                }
//                return false;
//            }
//        });

        findMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progressBarLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
        locateStations();
        hideSoftKeyboard();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
//            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = searchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException" + e.getMessage() );
        }

        if (list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting devices current location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locationPermissionsGranted) {
                final Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            Log.d(TAG, "onComplete: Lat: " + currentLocation.getLatitude() + ", Lng: " + currentLocation.getLongitude());

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }
    
    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(markerOptions);
        }
        hideSoftKeyboard();
    }
    
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permission");
        String []permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
            }else {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        locationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0){
                    for (int i  = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission falied");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    locationPermissionsGranted = true;
                }
            }
        }
    }

    private void locateStations(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int count = 0;
                while (count < response.length()){
                    try {
                        JSONObject object = response.getJSONObject(count);
                        station = new Station(object.getString("name"), object.getDouble("latitude"), object.getDouble("longitude"));
                        arrayList.add(station);
                        createMarker(station.getName(), station.getLatitude(),station.getLongitude());
                        count++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Error fetching JSON", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        JSONRequestHelper.getInstance(getApplicationContext()).addToRequest(jsonArrayRequest);
    }

    public void createMarker(String name, double latitude, double longitude){
        mMap.addMarker(new MarkerOptions()
            .position(new LatLng(latitude, longitude))
            .title(name));
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
