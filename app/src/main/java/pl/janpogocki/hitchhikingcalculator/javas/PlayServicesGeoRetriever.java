package pl.janpogocki.hitchhikingcalculator.javas;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

import pl.janpogocki.hitchhikingcalculator.GPSService;

/**
 * Created by Jan on 05.08.2018.
 * Find current geo-cooridantes
 */

public class PlayServicesGeoRetriever {
    private Context context;
    private double longitude = 0.0;
    private double latitude = 0.0;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location [] locations;
    private boolean firstGpsEntry = true;

    public PlayServicesGeoRetriever(Context context) {
        this.context = context;
        locations = new Location[2];
        createLocationRequest();
        startLocationUpdates();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(20000)
                .setFastestInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if (firstGpsEntry){
                        locations[0] = new Location(LocationManager.GPS_PROVIDER);
                        locations[0].setLatitude(latitude);
                        locations[0].setLongitude(longitude);

                        firstGpsEntry = false;
                    }
                    else {
                        locations[1] = new Location(LocationManager.GPS_PROVIDER);
                        locations[1].setLatitude(latitude);
                        locations[1].setLongitude(longitude);

                        float dstM = (locations[0].distanceTo(locations[1]));
                        GPSService.updateOverallDistance(dstM);
                        ((GPSService) context).GPSMessageNotification_notify(String.format(Locale.US, "%.2f", GPSService.getOverallDistance()));

                        locations[0] = locations[1];
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    public void stopGPS(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
