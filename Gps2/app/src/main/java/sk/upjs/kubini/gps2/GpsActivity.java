package sk.upjs.kubini.gps2;

import android.Manifest;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import sk.upjs.kubini.gps2.provider.DatabaseOpenHelper;

public class GpsActivity extends AppCompatActivity {
    public static Context gGpsContext;

    private Button btnStartGPS, btnStopGPS;
    private TextView textView;
    private ScrollView scrollView;
    private LocationManager locationManager;
    private LocationListener listener;
    private DatabaseOpenHelper dbHelper;
    private int iLockDB;
    private String strAllSuradnice;
    private String m_NazovCesty = "Cesta";
    private int m_minTime = 6000;
    private int m_minDist = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        MainActivity.g_Cesta_ID = 0;

        textView = (TextView) findViewById(R.id.textView);
        btnStartGPS = (Button) findViewById(R.id.startGPS);
        btnStopGPS  = (Button) findViewById(R.id.stopGPS);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        textView.setMovementMethod(new ScrollingMovementMethod());
        scrollView.setVerticalScrollBarEnabled(true);
        strAllSuradnice = "";
        iLockDB = 0;
        dbHelper = new DatabaseOpenHelper(getApplicationContext());;

        listener = new LocationListener() {
            private int cislovac = 0;
            private long startTime = 0;
            private long diff;
            @Override
            public void onLocationChanged(Location location) {
                if (cislovac == 0) {
                    textView.setText("");
                }
                diff = System.currentTimeMillis() - startTime;
                if (diff > m_minTime) { // napr. starsi smartphone ZTE Blade III nepodporuje minTime, preto to poistime rucne
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(". hh:mm:ss: ");
                    String hhmmss = simpleDateFormat.format(new Date());
                    strAllSuradnice += cislovac + hhmmss + String.format("%.4f %.4f %.0f\n", location.getLongitude(), location.getLatitude(), location.getAltitude());
                    textView.setText(strAllSuradnice);
                    saveLocation(location, cislovac);
                    cislovac++;
                    startTime = System.currentTimeMillis();
                    scrollView.fullScroll(View.FOCUS_DOWN); // scroll na poslednu polozku
                }
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
        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    public void onClickStartGPS(View view) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year, month, day, hour, min, sec;
        year = calendar.get(Calendar.YEAR) - 2000;
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        min = calendar.get(Calendar.MINUTE);
        sec = calendar.get(Calendar.SECOND);
        m_NazovCesty = String.format("Cesta_%02d%02d%02d_%02d%02d%02d", year, month, day, hour, min, sec);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates("gps", m_minTime, m_minDist, listener);
        }
        else {
            configure_button();
        }
    }

    public void onClickStopGPS(View view) {
        locationManager.removeUpdates(listener);
    }

    private void saveLocation(Location l, int cislovac) {
        int id_Gps1 = MainActivity.g_Cesta_ID;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (iLockDB == 0) {
            iLockDB = 1;
            if (cislovac == 0) {
                id_Gps1 = dbHelper.InsertGps1(db, m_NazovCesty);
                MainActivity.g_Cesta_ID = id_Gps1;
            }
            dbHelper.InsertGps2(db, id_Gps1, l.getLatitude(), l.getLongitude(), l.getAltitude(), 0);
            iLockDB = 0;
        }
        db.close();
    }

    private void getParameters() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.rawQuery("select MinTime, MinDist from GPSPARAM", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                int minT = cur.getInt(0);
                int minD = cur.getInt(1);
                if (minT < 0) { minT = 0; }
                if (minT > 60000) { minT = 60000; }
                if (minD < 0) { minD = 0; }
                if (minD > 100) { minD = 100; }
                m_minTime = minT;
                m_minDist = minD;
            }
            cur.close();
        }
        db.close();
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

    public void onBack(View view) {
        locationManager.removeUpdates(listener);
        finish();
    }

    //========== Action bar =======

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gps_cesta_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.startGPS) {
            onClickStartGPS(null);
        }

        if (itemId == R.id.stopGPS) {
            onClickStopGPS(null);
        }

        if (itemId == R.id.btnBack) {
            onBack(null);
        }

        return super.onOptionsItemSelected(item);
    }
}
