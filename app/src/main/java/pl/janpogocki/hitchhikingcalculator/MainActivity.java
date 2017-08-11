package pl.janpogocki.hitchhikingcalculator;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;

import pl.janpogocki.hitchhikingcalculator.javas.CenyPaliw;

public class MainActivity extends AppCompatActivity {

    EditText editText, editText2, editText3, editText4, editText5;
    Switch switch1;
    TableLayout tableLayout3, tableLayout4;
    Spinner spinner;
    TextView textView7;
    FloatingActionButton floatingActionButton;
    AsyncTaskRunnerKMListener asyncTaskRunnerKMListener;
    SharedPreferences sharedPreferences;
    Menu mainMenu;

    public CenyPaliw cenypaliw = null;

    private void updateFinalCosts(){
        if (!editText.getText().toString().equals("") && !editText2.getText().toString().equals("") && !editText3.getText().toString().equals("") && !editText5.getText().toString().equals("") && ((!editText4.getText().toString().equals("") && !switch1.isChecked()) || (switch1.isChecked()))) {
            double editTextDouble = Double.parseDouble(editText.getText().toString());
            double editText2Double = Double.parseDouble(editText2.getText().toString());
            int editText3Integer = Integer.parseInt(editText3.getText().toString());
            double editText5Double = Double.parseDouble(editText5.getText().toString());

            String settxt = "";
            try {
                if (switch1.isChecked())
                    settxt = String.format(Locale.US, "%.2f", cenypaliw.getCost4Person(editTextDouble, editText2Double, editText3Integer, cenypaliw.getCenaPaliwa(spinner.getSelectedItem().toString()), editText5Double));
                else {
                    double editText4Double = Double.parseDouble(editText4.getText().toString());
                    settxt = String.format(Locale.US, "%.2f", cenypaliw.getCost4Person(editTextDouble, editText2Double, editText3Integer, editText4Double, editText5Double));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            textView7.setText(settxt);
        }
    }

    private void enableSwitch(){
        switch1.setEnabled(true);
        switch1.setChecked(sharedPreferences.getBoolean("switch1", true));

        try {
            switch1.setText(cenypaliw.getDate());
            mainMenu.findItem(R.id.petrol_prices).setVisible(true);
        } catch (Exception e) {
            switch1.setText(R.string.problem_internet);
            switch1.setChecked(false);
            switch1.setEnabled(false);
            e.printStackTrace();
        }
    }

    private void updateDistance(double dst){
        editText.setText(String.format(Locale.US, "%.2f", dst));
    }

    private void showGPSErrorDialog(final Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(R.string.gps_off_title);
        alertDialog.setMessage(R.string.gps_off_text);

        alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void switchGPS(){
        if (isMyServiceRunning(GPSService.class))
            floatingActionButton.setImageResource(R.drawable.ic_location_start);
        else
            floatingActionButton.setImageResource(R.drawable.ic_location_stop);

        Intent intent = new Intent(this, GPSService.class);

        if (isMyServiceRunning(GPSService.class)) {
            stopService(intent);
            editText.setEnabled(true);
            asyncTaskRunnerKMListener.cancel(false);
        }
        else {
            editText.setEnabled(false);
            editText.setText("0.00");
            startService(intent);

            asyncTaskRunnerKMListener = new AsyncTaskRunnerKMListener();
            asyncTaskRunnerKMListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about_app){
            Intent launchNewIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(launchNewIntent);
        }
        else if (id == R.id.petrol_prices){
            String msg = null;
            try {
                msg = getResources().getString(R.string.petrol_prices,
                        String.format(Locale.US, "%.2f", CenyPaliw.getCenaPaliwa("Pb 95")),
                        String.format(Locale.US, "%.2f", CenyPaliw.getCenaPaliwa("Pb 98")),
                        String.format(Locale.US, "%.2f", CenyPaliw.getCenaPaliwa("ON")),
                        String.format(Locale.US, "%.2f", CenyPaliw.getCenaPaliwa("LPG")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("www.e-petrol.pl");
            alertDialog.setMessage(msg);

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // check whether GPS is up or down
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER) | manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (statusOfGPS)
                        switchGPS();
                    else {
                        showGPSErrorDialog(this);
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-7200725979200234/2509752468");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        switch1 = (Switch) findViewById(R.id.switch1);
        spinner = (Spinner) findViewById(R.id.spinner);
        textView7 = (TextView) findViewById(R.id.textView7);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        tableLayout3 = (TableLayout) findViewById(R.id.tableLayout3);
        tableLayout4 = (TableLayout) findViewById(R.id.tableLayout4);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        editText2.setText(sharedPreferences.getString("editText2", "7.00"));
        editText3.setText(sharedPreferences.getString("editText3", "1"));
        editText4.setText(sharedPreferences.getString("editText4", "3.50"));
        editText5.setText(sharedPreferences.getString("editText5", "0.00"));
        spinner.setSelection(sharedPreferences.getInt("spinner", 0));

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (switch1.isChecked()) {
            //włącznik włączony
            tableLayout3.setVisibility(View.VISIBLE);
            tableLayout4.setVisibility(View.GONE);
        } else {
            //włącznik wyłączony
            tableLayout3.setVisibility(View.GONE);
            tableLayout4.setVisibility(View.VISIBLE);
        }

        // SERVICE
        if (isMyServiceRunning(GPSService.class)) {
            floatingActionButton.setImageResource(R.drawable.ic_location_stop);
            editText.setEnabled(false);

            asyncTaskRunnerKMListener = new AsyncTaskRunnerKMListener();
            asyncTaskRunnerKMListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            floatingActionButton.setImageResource(R.drawable.ic_location_start);
            editText.setEnabled(true);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                } else {
                    // check whether GPS is up or down
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER) | manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (statusOfGPS)
                        switchGPS();
                    else {
                        showGPSErrorDialog(view.getContext());
                    }
                }
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateFinalCosts();

            if (isChecked) {
                //włącznik włączony
                tableLayout3.setVisibility(View.VISIBLE);
                tableLayout4.setVisibility(View.GONE);
            } else {
                //włącznik wyłączony
                tableLayout3.setVisibility(View.GONE);
                tableLayout4.setVisibility(View.VISIBLE);
            }

            sharedPreferences.edit().putBoolean("switch1", isChecked).apply();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFinalCosts();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // -----------------------------

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFinalCosts();
                sharedPreferences.edit().putString("editText2", s.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // -----------------------------

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFinalCosts();
                sharedPreferences.edit().putString("editText3", s.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // -----------------------------

        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFinalCosts();
                sharedPreferences.edit().putString("editText4", s.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // -----------------------------

        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFinalCosts();
                sharedPreferences.edit().putString("editText5", s.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFinalCosts();
                sharedPreferences.edit().putInt("spinner", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateFinalCosts();
            }
        });

    }

    private class AsyncTaskRunner extends AsyncTask<CenyPaliw, CenyPaliw, CenyPaliw> {
        private CenyPaliw cenypaliw = null;

        @Override
        protected CenyPaliw doInBackground(CenyPaliw... params) {
            try {
                cenypaliw = new CenyPaliw();
                return cenypaliw;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(CenyPaliw cenyPaliw) {
            enableSwitch();
        }
    }

    private class AsyncTaskRunnerKMListener extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            while (!isCancelled()){
                publishProgress();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            updateDistance(GPSService.getOverallDistance());
        }
    }
}
