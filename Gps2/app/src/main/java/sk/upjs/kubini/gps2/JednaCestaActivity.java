package sk.upjs.kubini.gps2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import sk.upjs.kubini.gps2.KreslenieProvider.DrawingView;
import sk.upjs.kubini.gps2.provider.DatabaseOpenHelper;

public class JednaCestaActivity extends AppCompatActivity {
    int ID_cesty;
    TextView textViewNazovCesty;
    TextView textViewDatum;
    TextView textViewDlzka;
    TextView textViewStupanie;
    TextView textViewKlesanie;
    TextView textViewCas;
    TextView textViewPocetGPS;
    int pocetGPS = 0;
    ImageView imageView;
    boolean bDrawing = false;

//    private class StruLocation {
//        StruLocation(int id, double la, double lo, double al, long ti) {
//            _ID = id; lat = la; lon = lo; alt=al; timeStamp = ti;
//        }
//        public int _ID;
//        public double lat;
//        public double lon;
//        public double alt;
//        public long timeStamp;
//    }
    ArrayList<StruLocation> arrLoc = new ArrayList<StruLocation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jedna_cesta);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ID_cesty = extras.getInt("_ID");
        }
        textViewNazovCesty = (TextView)findViewById(R.id.textViewNazovCesty);
        textViewDatum = (TextView)findViewById(R.id.textViewDatum);
        textViewDlzka = (TextView)findViewById(R.id.textViewDlzka);
        textViewStupanie = (TextView)findViewById(R.id.textViewStupanie);
        textViewKlesanie = (TextView)findViewById(R.id.textViewKlesanie);
        textViewCas = (TextView)findViewById(R.id.textViewCas);
        textViewPocetGPS = (TextView)findViewById(R.id.textViewPocetGPS);

        textViewNazovCesty.setText(ID_cesty + "");

        loadGPS();
    }

    public void loadGPS() {
// Prerobit na CursorLoader podla Opielu, Novotneho alebo tohoto vzoru:
// https://stackoverflow.com/questions/18326954/how-to-read-an-sqlite-db-in-android-with-a-cursorloader
        DatabaseOpenHelper dbHelper;
        String strRiadok;
        int id;
        String nazov = "";
        long lTimeStamp = 0;
        String strDateTime = "";
        double lat;
        double lon;
        double alt;
        long timeStamp;
        StruLocation loc;
        double vzdialenost;
        double stupanie, klesanie;
        long startCas;
        long casMiliSec;
        double distanceInMeters;
        Location L0 = new Location("");
        Location L1 = new Location("");

        arrLoc.clear();
        dbHelper = new DatabaseOpenHelper(getApplicationContext());;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            Cursor cur = db.rawQuery("select Nazov, TimeStamp from GPS1 where _ID = " + ID_cesty, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    nazov       = cur.getString(0);
                    lTimeStamp  = cur.getLong  (1);
                    strDateTime = convertDateTime(lTimeStamp);
                }
                cur.close();
            }
            Cursor cur2 = db.rawQuery("select _ID, Lat, Lon, Alt, TimeStamp from GPS2 where _ID_GPS1 = " + ID_cesty + " order by _ID", null);
            if (cur2 != null) {
                if (cur2.moveToFirst()) {
                    do {
                        id  = cur2.getInt(0);
                        lat = cur2.getDouble(1);
                        lon = cur2.getDouble(2);
                        alt = cur2.getDouble(3);
                        timeStamp = cur2.getLong(4);
                        loc = new StruLocation(id, lat, lon, alt, timeStamp);
                        arrLoc.add(loc);
                    } while (cur2.moveToNext());
                }
                cur2.close();
            }
            db.close();
        }
        pocetGPS = arrLoc.size();
        textViewNazovCesty.setText(String.format("%d: %s", ID_cesty, nazov));
        textViewDatum.setText(strDateTime);
        textViewPocetGPS.setText(pocetGPS + "");

        vzdialenost = 0;
        stupanie = 0;
        klesanie = 0;
        casMiliSec = 0;
        startCas = 0;
        if (pocetGPS > 0) {
            StruLocation loc0 = arrLoc.get(0);
            StruLocation loc1;
            for (int i = 1; i < pocetGPS; i++) {
                loc1 = arrLoc.get(i);
//===================================================================== cas
                casMiliSec += (loc1.timeStamp - loc0.timeStamp);
//===================================================================== prevysenia
                if (loc0.alt < loc1.alt) {
                    stupanie += loc1.alt - loc0.alt;
                }
                else {
                    klesanie += loc0.alt - loc1.alt;
                }
//===================================================================== vzdialenost
                L0.setLatitude(loc0.lat);
                L0.setLongitude(loc0.lon);
                L1.setLatitude(loc1.lat);
                L1.setLongitude(loc1.lon);
                distanceInMeters = L0.distanceTo(L1);
                vzdialenost += distanceInMeters;
                loc0 = loc1;
            }
            textViewDlzka.setText(String.format("%.0f", vzdialenost));
            textViewStupanie.setText(stupanie + "");
            textViewKlesanie.setText(klesanie + "");
            textViewCas.setText(casMiliSec + "");
        }
        long sec = casMiliSec / 1000;
        long min = sec / 60;
        sec = sec - (min * 60);
        long hod = min / 60;
        min = min - (hod * 60);
        String strCas = String.format("%02d:%02d:%02d", hod, min, sec);
        textViewCas.setText(strCas);
    }

    private String convertDateTime(long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.GERMANY);
        cal.setTimeInMillis(timeStamp);
        String strDateTime = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return strDateTime;
    }

    public void onZobrazPrevysenie(View view) {
        DrawingView mDrawingView = new DrawingView(this);
        mDrawingView.setStruLocations(arrLoc, 0);
        LinearLayout mDrawingPad = (LinearLayout) findViewById(R.id.view_drawing_pad);
        mDrawingPad.removeAllViews();
        mDrawingPad.addView(mDrawingView);
    }
    public void onZobrazTrasu(View view) {
        DrawingView mDrawingView = new DrawingView(this);
        mDrawingView.setStruLocations(arrLoc, 1);
        LinearLayout mDrawingPad = (LinearLayout) findViewById(R.id.view_drawing_pad);
        mDrawingPad.removeAllViews();
        bDrawing = true;
        mDrawingPad.addView(mDrawingView);
    }

    public void onBack(View view) {
        finish();
    }

    //========== Action bar =======

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.jedna_cesta_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.btnPrevysenie) {
            onZobrazPrevysenie(null);
        }

        if (itemId == R.id.btnTrasa) {
            onZobrazTrasu(null);
        }

        if (itemId == R.id.btnBack) {
            onBack(null);
        }

        return super.onOptionsItemSelected(item);
    }

}

