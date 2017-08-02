package pl.janpogocki.hitchhikingcalculator.javas;

import android.app.IntentService;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Jan on 13.04.2017.
 * Find current geo-cooridantes
 */

public class GeoRetriever {
    private double longitude = 0.0;
    private double latitude = 0.0;
    private boolean gpsStatusEnabled = true;
    LocationManager locationManager;
    private VeggsterLocationListener veggsterLocationListener = new VeggsterLocationListener();

    public GeoRetriever(Context context){
        getLocation(veggsterLocationListener, context);
    }

    private void getLocation(LocationListener locationListener, Context context) {
        try {
            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);

            locationManager = (LocationManager) context
                    .getSystemService(IntentService.LOCATION_SERVICE);

            // Get the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                gpsStatusEnabled = false;
            } else {
                locationManager.requestLocationUpdates(
                        provider,
                        0,
                        10, locationListener);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void waitForLookup(){
        while (true) {
            if ((latitude != 0 && longitude != 0) || !gpsStatusEnabled)
                break;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void stopGPS(){
        locationManager.removeUpdates(veggsterLocationListener);
    }

    private class VeggsterLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {

        }

    }
}
