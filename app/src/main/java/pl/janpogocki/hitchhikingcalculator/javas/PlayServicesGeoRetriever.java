package pl.janpogocki.hitchhikingcalculator.javas;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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

    public PlayServicesGeoRetriever(Context context) {
        this.context = context;
        createLocationRequest(context);
        startLocationUpdates();
    }

    private void createLocationRequest(final Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        ((GPSService) context).setGpsStatusOk(true);
                    }
                }
            }
        };

        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000)
                .setFastestInterval(1500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null){
                    Location location = task.getResult();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    ((GPSService) context).setGpsStatusOk(true);
                }
            });

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void stopGPS(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
