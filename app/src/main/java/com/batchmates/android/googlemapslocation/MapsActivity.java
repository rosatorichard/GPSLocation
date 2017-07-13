package com.batchmates.android.googlemapslocation;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Maps Activity";
    private GoogleMap mMap;
    private Location location;
    private boolean styleChange=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        location=getIntent().getParcelableExtra("LOCAL");
        mapFragment.getMapAsync(this);
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
        styleChange=getIntent().getBooleanExtra("STYLE",false);
        if(styleChange==true) {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_json));
        }
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        Log.d(TAG, "onMapReady: "+location);
        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());

        LatLng local=new LatLng(35,-86);
        LatLng local2=new LatLng(35,-82);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(local).title("Marker in herp"));
        mMap.addMarker(new MarkerOptions().position(local2).title("Marker in derp"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


}
