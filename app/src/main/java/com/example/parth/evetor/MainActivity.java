package com.example.parth.evetor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import static android.R.attr.category;
import static android.R.attr.fragment;
import static android.R.attr.name;
import static android.R.attr.state_empty;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    GoogleMap mGoogleMap;
    ArrayList<LatLng> markersArray = new ArrayList<LatLng>();
    MarkerOptions options = new MarkerOptions();

    GoogleApiClient mGoogleApiClient;

    double lat = 0;
    double lng = 0;

    private String TAG = MainActivity.class.getSimpleName();

    //URL of the JSON array
    private static String url = "http://app.toronto.ca/cc_sr_v1_app/data/edc_eventcal_APR";

    ArrayList<HashMap<String, String>> eventList;

    public Button btn;

    public void init() {

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent event = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(event);

            }

        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MainActivity.GetEvents().execute();

        if (googleServiceAvailable()) {
            setContentView(R.layout.activity_main);
            initMap();
        }

        eventList = new ArrayList<>();
        init();

    }

    //Initializes the Map
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
    }


    public boolean googleServiceAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();

        }

        return false;
    }

    //Goes to default city location zoom of when map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        goToLocationZoom(43.6547567, -79.3966769, 11);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

    }




    private void goToLocationZoom(double x, double y, float zoom) {

        Log.e("ArraySizeGo" , Integer.toString(markersArray.size()));
        LatLng ll = new LatLng(x, y);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);

    }

    Marker marker;

    //gets the lat and lng values from markersArray and marks them on the map
    public void geoLocate(View view) {

        Log.e("ArraySize:", Integer.toString(markersArray.size()));
        for(int i = 0; i < markersArray.size() ; i++){
            Log.e("Before marker:", markersArray.get(i).latitude + " "+ markersArray.get(i).longitude);

            MarkerOptions mark = new MarkerOptions().title(eventList.get(i).get("name")).position(new LatLng(markersArray.get(i).latitude, markersArray.get(i).longitude));
            mGoogleMap.addMarker(mark);

        }
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {



        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(location == null){
            Toast.makeText(this, "Cant find your location", Toast.LENGTH_LONG).show();
        }else{
            LatLng ll =new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,12);
            mGoogleMap.animateCamera(update);
        }
    }

    //fetches JSON data and parse it into EventList Array
    private  class GetEvents extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray events = new JSONArray(jsonStr);
                    LatLng temp;

                    //Runs until it reaches the length of the JSON Array and
                    //stores appropriate values into the EventList Array
                    for (int i = 0; i < events.length(); i++) {

                        JSONObject c = events.getJSONObject(i);
                        JSONObject eventO = c.getJSONObject("calEvent");
                        String name = eventO.getString("eventName");
                        JSONArray locations = eventO.getJSONArray("locations");
                        JSONObject insideLocations = locations.getJSONObject(0);


                        JSONObject coord;
                        JSONObject insideOfCoord;
                        JSONArray coordinates;

                        if(insideLocations.get("coords") instanceof JSONObject){

                            coord = insideLocations.getJSONObject("coords");
                            lng = coord.getDouble("lng");
                            lat = coord.getDouble("lat");
                            markersArray.add(new LatLng(lat,lng));
                            Log.e("top" ,i + " "  + Double.toString(lng));
                            Log.e("top" , i + " " +Double.toString(lat));

                        }else{

                            coordinates = insideLocations.getJSONArray("coords");
                            insideOfCoord = coordinates.getJSONObject(coordinates.length()-1);
                            lng = insideOfCoord.getDouble("lng");
                            lat = insideOfCoord.getDouble("lat");
                            markersArray.add(new LatLng(lat,lng));
                            Log.e("down" ,i + " "  + Double.toString(lng));
                            Log.e("down" , i + " " +Double.toString(lat));

                        }

                        HashMap<String, String> event = new HashMap<>();
                        event.put("name",name);
                        eventList.add(event);

                    }

                    int n = markersArray.size();


                } catch (final JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "JSON parsing error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } else {
                Log.e(TAG, "Could not get the JSON from server ");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Could not get the JSON from server.",
                                Toast.LENGTH_SHORT).show();
                    }
                });


            }
            return null;
        }

    }

}
