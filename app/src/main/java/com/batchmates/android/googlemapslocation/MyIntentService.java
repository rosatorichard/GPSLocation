package com.batchmates.android.googlemapslocation;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.SyncStateContract;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    private static final String TAG = "Geo Service";
    // TODO: Rename actions, choose action names that describe tasks that this

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            Location localGeo = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            List<Address> s = geocoder.getFromLocation(localGeo.getLatitude(), localGeo.getLongitude(), 1);
            Log.d(TAG, "locationTracker: " + s.get(0).getAddressLine(0));
            Log.d(TAG, "locationTracker: " + s);
            Intent intent3= new Intent("GEO");
            intent3.putExtra("GEO",localGeo);
            sendBroadcast(intent3);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                "com.google.android.gms.location.sample.locationaddress";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }

}
