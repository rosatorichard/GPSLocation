package com.batchmates.android.googlemapslocation;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationProvider;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.batchmates.android.googlemapslocation.model.AddressResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements android.location.LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_REQUEST_LOCATION = 0;
    private static final String TAG = "Main Activity";
    private Location myLocation;
    private TextView address;
    private MyReciever reciever;
    private EditText editTextStreet, editTextCity, editTextState, latitude, longitude;
    private boolean style = false;
    private LocationManager manager;

    private FusedLocationProviderClient fusedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextStreet = (EditText) findViewById(R.id.etLocation);
        editTextCity = (EditText) findViewById(R.id.etLocation2);
        editTextState = (EditText) findViewById(R.id.etLocation3);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        address = (TextView) findViewById(R.id.adress);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            weNeedThis();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission not granted");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "onCreate: We need this");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_REQUEST_LOCATION);
                Log.d(TAG, "onCreate: Requesting permission");

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                return;
            }

        }


        locationTracker();


    }

    private void weNeedThis() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        locationTracker();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void locationTracker() {

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        } else {
            fusedLocation = LocationServices.getFusedLocationProviderClient(this);
            fusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
//                    Log.d(TAG, "onSuccess: " + location.getLongitude() + " Lat: " + location.getLatitude() + " " + location);
                }
            });

            LocationManager locationManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Granted");
                    locationTracker();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: not Granted");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;

        Log.d(TAG, "onLocationChanged: lat: " + location.getLatitude() + " long: " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        switch (i) {
            case LocationProvider.AVAILABLE:
                Log.d(TAG, "onStatusChanged: Available");
                break;

            case LocationProvider.OUT_OF_SERVICE:
                Log.d(TAG, "onStatusChanged: Out of Service");
                break;

            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d(TAG, "onStatusChanged: Temp Unavailable");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "onProviderEnabled: ");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "onProviderDisabled: ");
    }

    public void goToMaps(View view) {

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("LOCAL", myLocation);
        intent.putExtra("STYLE", style);
        startActivity(intent);

    }

    public void goToLocation(View view) {

        String address = editTextStreet.getText().toString() + " " + editTextCity.getText().toString() + " " + editTextState.getText().toString();
        Log.d(TAG, "geocodeAddress: " + address);
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("geocode")
                .addPathSegment("json")
                .addQueryParameter("address", address)
                .addQueryParameter("key", "AIzaSyCUju7lJs-jCD19GbEZUzjg7Ww--r7vo_E")
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.toString());
                Gson gson = new Gson();
                AddressResponse addressResponse = gson.fromJson(response.body().string(), AddressResponse.class);
                Log.d(TAG, "onResponse: " + addressResponse.getResults().get(0).getGeometry().getLocation().getLat());
                Log.d(TAG, "onResponse: " + addressResponse.getResults().get(0).getGeometry().getLocation().getLng());
//                myLocation.setLatitude(addressResponse.getResults().get(0).getGeometry().getLocation().getLat());
//                myLocation.setLongitude(addressResponse.getResults().get(0).getGeometry().getLocation().getLng());
//
//
//                Log.d(TAG, "onResponse: "+myLocation.getLatitude()+ " jkghckjhgckhgckhgckjg "+ myLocation.getLongitude());

            }
        });

    }


    public void findbyCord(View view) {

        final String address = latitude.getText() + "," + longitude.getText();
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("geocode")
                .addPathSegment("json")
                .addQueryParameter("latlng", address)
                .addQueryParameter("key", "AIzaSyCUju7lJs-jCD19GbEZUzjg7Ww--r7vo_E")
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.toString());
                Gson gson = new Gson();
                AddressResponse addressResponse = gson.fromJson(response.body().string(), AddressResponse.class);
                Log.d(TAG, "onResponse: " + addressResponse.getResults().get(0).getFormattedAddress());
                myLocation.setLatitude(addressResponse.getResults().get(0).getGeometry().getLocation().getLat());
                myLocation.setLongitude(addressResponse.getResults().get(0).getGeometry().getLocation().getLng());


                Log.d(TAG, "onResponse: " + myLocation.getLatitude() + "  " + myLocation.getLongitude());
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("LOCAL", myLocation);
                intent.putExtra("STYLE", style);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        locationTracker();
        reciever = new MyReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("GEO");
        registerReceiver(reciever, intentFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(reciever);
    }

    public void geoService(View view) {

        Location local = myLocation;
        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra(MyIntentService.Constants.LOCATION_DATA_EXTRA, local);
        startService(intent);
    }

    public void changeMapLook(View view) {
        style = !style;

    }

    public class MyReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Reciever Reached");
//            Location recieveLocal=intent.getParcelableExtra("GEO");
//            Intent intent2= new Intent(context,MapsActivity.class);
//            intent2.putExtra("LOCAL",intent.getParcelableExtra("GEO"));

        }
    }

}
