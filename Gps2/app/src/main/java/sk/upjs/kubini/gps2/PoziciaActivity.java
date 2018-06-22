package sk.upjs.kubini.gps2;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import sk.upjs.kubini.gps2.pocasieProvider.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PoziciaActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>> {
    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private TextView textView;
    private ScrollView scrollView;
    private LocationManager locationManager;
    private LocationListener listener;
    private double m_latitude = 0;
    private double m_longitude = 0;
    private boolean bSearchingLocation = false;
    private ArrayAdapter<String> adapter;
    private ListView pocasieListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pozicia);

        //Toto umozni spustit internet download v hlavnom vlakne - treba to prerobit, aby to islo na pozadi
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textViewLatitude  = (TextView) findViewById(R.id.latitude);
        textViewLongitude = (TextView) findViewById(R.id.longitude);
        textViewLatitude. setText(m_latitude + "");
        textViewLongitude.setText(m_longitude + "");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Stara verzia
        //textView = (TextView) findViewById(R.id.textView);
        //scrollView = (ScrollView) findViewById(R.id.scrollView);
        //textView.setMovementMethod(new ScrollingMovementMethod());
        //scrollView.setVerticalScrollBarEnabled(true);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                m_latitude = location.getLatitude();
                m_longitude = location.getLongitude();
                textViewLatitude. setText(m_latitude + "");
                textViewLongitude.setText(m_longitude + "");
                locationManager.removeUpdates(listener); // zastavit hladanie pozicie
                bSearchingLocation = false;
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        pocasieListView = (ListView) findViewById(R.id.pocasieListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        pocasieListView.setAdapter(adapter);

    }

    public void onHladajPoziciu(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            m_latitude = 0;
            m_longitude = 0;
            textViewLatitude. setText(m_latitude + "");
            textViewLongitude.setText(m_longitude + "");
            locationManager.requestLocationUpdates("gps", 6000, 0, listener);
            bSearchingLocation = true;
        }
        else {
            configure_button();
        }

        //Log.d("MyApp","Vratilo poziciu " + m_latitude + "|" + m_longitude);
    }

    public void onCancelActivity(View view) {
        if (bSearchingLocation) {
            locationManager.removeUpdates(listener);
        }
        super.onBackPressed();
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
    }

    public void onZistitPocasie(View view) {
        this.adapter.clear();
        getLoaderManager().restartLoader(0, Bundle.EMPTY, this);

        /*
        //Zisti pocasie
        String strPocasie;
        textView.setText("");
        WebPocasie webPocasie = new WebPocasie();
        List<String> listPocasie;
        listPocasie = webPocasie.loadWeather(m_latitude, m_longitude);
        int size = listPocasie.size();
        strPocasie = "Počet záznamov: " + size + "\n";
        for (int i=0; i < size; i++) {
            strPocasie += listPocasie.get(i) + "\n";
        }
        textView.setText(strPocasie);
        */
    }

    @Override
    public Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
        return new PocasieLoader(this, this.m_latitude, this.m_longitude);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> strings) {
        this.adapter.clear();
        this.adapter.addAll(strings);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        this.adapter.clear();
    }

//    public void infoDialog(String msg) {
//        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
//        dlgAlert.setMessage(msg);
//        dlgAlert.setTitle("Moje info");
//        dlgAlert.setPositiveButton("OK", null);
//        dlgAlert.setCancelable(true);
//        dlgAlert.create().show();
//    }


    //========== Action bar =======

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pozicia_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.hladajPoziciu) {
            onHladajPoziciu(null);
        }
        if (itemId == R.id.zistitPocasie) {
            onZistitPocasie(null);
        }
        if (itemId == R.id.cancelActivity) {
            onCancelActivity(null);
        }

        return super.onOptionsItemSelected(item);
    }

}
