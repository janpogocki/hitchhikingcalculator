package pl.janpogocki.hitchhikingcalculator;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;

import pl.janpogocki.hitchhikingcalculator.javas.PlayServicesGeoRetriever;

public class GPSService extends Service {
    private static float overallDistance;
    private PlayServicesGeoRetriever geoRetriever;

    public GPSService() {
    }

    public static double getOverallDistance() {
        return (double) Math.round((overallDistance/1000f) * 100) / 100;
    }

    public static void updateOverallDistance(float overallDistance) {
        GPSService.overallDistance += overallDistance;
    }

    public void GPSMessageNotification_notify(String distance){
        GPSMessageNotification.notify(this, distance);
    }

    @Override
    public void onCreate() {
        overallDistance = 0;

        startForeground(5, GPSMessageNotification.getNotification(this, "0.00"));

        HandlerThread handlerThread = new HandlerThread("GPSService");
        handlerThread.start();

        geoRetriever = new PlayServicesGeoRetriever(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
        GPSMessageNotification.cancel(this);
        geoRetriever.stopGPS();
    }
}
