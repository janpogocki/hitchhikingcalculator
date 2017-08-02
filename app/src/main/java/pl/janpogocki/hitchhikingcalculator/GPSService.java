package pl.janpogocki.hitchhikingcalculator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;

import java.util.Locale;

import pl.janpogocki.hitchhikingcalculator.javas.GeoRetriever;

public class GPSService extends Service {
    AsyncTaskRunner asyncTaskRunner;
    private static float overallDistance;
    Location [] locations;

    public GPSService() {
        overallDistance = 0;
        locations = new Location[2];
    }

    public static double getOverallDistance() {
        return (double) Math.round((overallDistance/1000f) * 100) / 100;
    }

    private void GPSMessageNotification_notify(String distance){
        GPSMessageNotification.notify(this, distance);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GPSMessageNotification.notify(this, "0.00");

        asyncTaskRunner = new AsyncTaskRunner();
        asyncTaskRunner.setContext(this);
        asyncTaskRunner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        asyncTaskRunner.stopGPS();
        asyncTaskRunner.cancel(false);
        GPSMessageNotification.cancel(this);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        int i = 0;
        GeoRetriever geoRetriever;
        Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        public void stopGPS(){
            geoRetriever.stopGPS();
        }

        @Override
        protected void onPreExecute() {
            geoRetriever = new GeoRetriever(context);
        }

        @Override
        protected String doInBackground(String... params) {
            while (!isCancelled()){
                geoRetriever.waitForLookup();

                if (i == 0){
                    locations[0] = new Location(LocationManager.GPS_PROVIDER);
                    locations[0].setLatitude(geoRetriever.getLatitude());
                    locations[0].setLongitude(geoRetriever.getLongitude());
                }
                else {
                    locations[1] = new Location(LocationManager.GPS_PROVIDER);
                    locations[1].setLatitude(geoRetriever.getLatitude());
                    locations[1].setLongitude(geoRetriever.getLongitude());

                    float dstM = (locations[0].distanceTo(locations[1]));
                    overallDistance = overallDistance + dstM;
                    GPSMessageNotification_notify(String.format(Locale.US, "%.2f", getOverallDistance()));

                    locations[0] = locations[1];
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                i++;
            }

            return null;
        }
    }
}
