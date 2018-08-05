package pl.janpogocki.hitchhikingcalculator.javas;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
        locationRequest.setInterval(10000)
                .setFastestInterval(5000)
                .setMaxWaitTime(0)
                .setSmallestDisplacement(0)
                .setExpirationDuration(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null){
                        Location location = task.getResult();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        ((GPSService) context).setGpsStatusOk(true);
                    }
                }
            });

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
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
