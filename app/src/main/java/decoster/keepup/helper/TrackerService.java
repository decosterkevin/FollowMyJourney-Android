package decoster.keepup.helper;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;

/**
 * Created by Decoster on 04/02/2016.
 */
public class TrackerService {

    // Acquire a reference to the system Location Manager
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Activity activity;
    private Sender sender;
    private String PROVIDER;

    public TrackerService(Activity activity, Sender sender) {
        this.sender = sender;
        this.activity = activity;
        this.sender = new Sender();
        locationManager = (LocationManager) activity.getSystemService(activity.getApplicationContext().LOCATION_SERVICE);
        locationListener = new MyLocationListener(sender);
        PROVIDER = LocationManager.GPS_PROVIDER;
    }

    public boolean registerLocationService() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean error = false;
        PROVIDER = locationManager.getBestProvider(c, true);
        if (PROVIDER != null) {
            try {
                //long minTime, float minDistance
                locationManager.requestLocationUpdates(PROVIDER, Long.parseLong(prefs.getString("periodUpdate", "10.0")), Float.parseFloat(prefs.getString("periodUpdate", "10.0f")), locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
                error = true;
            }
        }
        else {
            error = true;
        }
        return error;
    }

    public boolean unregisterLocationService() {
        boolean error = false;
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
            error = true;
        }
        return error;
    }

    public void sendPicture() {

    }

    public Location getLastLocation() {
        Location lastLocation = null;
        try {
            lastLocation = locationManager.getLastKnownLocation(PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return lastLocation;
    }


}
