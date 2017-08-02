package pl.janpogocki.hitchhikingcalculator.javas;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import static android.content.Context.LOCATION_SERVICE;

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
        Location location = null;
        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

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
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0L,
                            0.0F, locationListener);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                0L,
                                0.0F, locationListener);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
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

    public void resetGPS(){
        latitude = 0;
        longitude = 0;
        gpsStatusEnabled = true;
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
