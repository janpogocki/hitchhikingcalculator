package pl.janpogocki.hitchhikingcalculator;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText editText, editText2, editText3, editText4;
    Switch switch1; //przełącznik do pobierania z interentu bądź nie
    TableLayout tableLayout3, tableLayout4;
    Spinner spinner;
    TextView textView7;
    Button button;

    public CenyPaliw cenypaliw = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        try {
            runner.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        switch1 = (Switch) findViewById(R.id.switch1);
        spinner = (Spinner) findViewById(R.id.spinner);
        textView7 = (TextView) findViewById(R.id.textView7);
        button = (Button) findViewById(R.id.button);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tableLayout3 = (TableLayout) findViewById(R.id.tableLayout3);
                tableLayout4 = (TableLayout) findViewById(R.id.tableLayout4);
                if (isChecked) {
                    //włącznik włączony
                    tableLayout3.setVisibility(View.VISIBLE);
                    tableLayout4.setVisibility(View.GONE);
                } else {
                    //włącznik wyłączony
                    tableLayout3.setVisibility(View.GONE);
                    tableLayout4.setVisibility(View.VISIBLE);
                }
            }
        });

        try {
            switch1.setText(cenypaliw.getDate());
        } catch (Exception e) {
            switch1.setText("Problem!");
            switch1.setChecked(false);
            switch1.setEnabled(false);
            e.printStackTrace();
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("") && !editText2.getText().toString().equals("") && !editText3.getText().toString().equals("") && ((!editText4.getText().toString().equals("") && !switch1.isChecked()) || (switch1.isChecked()))) {
                    double editTextDouble = Double.parseDouble(editText.getText().toString());
                    double editText2Double = Double.parseDouble(editText2.getText().toString());
                    int editText3Integer = Integer.parseInt(editText3.getText().toString());

                    String settxt = "";
                    try {
                        if (switch1.isChecked())
                            settxt = String.valueOf(cenypaliw.getCost4Person(editTextDouble, editText2Double, editText3Integer, cenypaliw.getCenaPaliwa(spinner.getSelectedItem().toString())));
                        else {
                            double editText4Double = Double.parseDouble(editText4.getText().toString());
                            settxt = String.valueOf(cenypaliw.getCost4Person(editTextDouble, editText2Double, editText3Integer, editText4Double));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    textView7.setText(settxt);
                }
                else {
                    new AlertDialog.Builder( MainActivity.this )
                            .setTitle( "Błąd" )
                            .setMessage( "Nie wypełniono wszystkich pól w formularzu!" )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d( "AlertDialog", "Positive" );
                                }
                            })
                            .show();
                }
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
    }
}
