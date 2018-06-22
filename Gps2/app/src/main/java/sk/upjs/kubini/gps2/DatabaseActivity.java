package sk.upjs.kubini.gps2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.UUID;

import sk.upjs.kubini.gps2.provider.DatabaseOpenHelper;
import sk.upjs.kubini.gps2.provider.Defaults;

public class DatabaseActivity extends AppCompatActivity {
    private TextView textView;
    private ScrollView scrollView;
    private DatabaseOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        textView = (TextView) findViewById(R.id.textView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        textView.setMovementMethod(new ScrollingMovementMethod());
        scrollView.setVerticalScrollBarEnabled(true);
        dbHelper = new DatabaseOpenHelper(getApplicationContext());;
    }
    public void onClickSelectGPS1(View view) {
        String strText;
        textView.setText("");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            Cursor cur = db.rawQuery("select _ID, Nazov, TimeStamp, UUIDHelp from GPS1 order by _ID", null);
            if (cur != null) {
                strText = "";
                if (cur.moveToFirst()) {
                    do {
                        int id     = cur.getInt(0);
                        String nazov = cur.getString(1);
                        String strTimeStamp = cur.getString(2);
                        String uuidHelp = cur.getString(3);
                        String strRiadok = String.format("%d %s %s %s\n", id, nazov, strTimeStamp, uuidHelp);
                        strText += strRiadok;
                    } while (cur.moveToNext());
                }
                cur.close();
                textView.setText(strText);
            }
            db.close();
        }
        else {
            textView.append("db is null\n");
        }
    }

    public void onClickSelectGPS2(View view) {
        String strText;
        textView.setText("");
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cur = db.rawQuery("select _ID_GPS1, _ID, lat, lon, alt, TimeStamp from GPS2 order by _ID_GPS1, _ID", null);
            if (cur != null) {
                strText = "";
                if (cur.moveToFirst()) {
                    do {
                        int idGps1 = cur.getInt(0);
                        int id     = cur.getInt(1);
                        double lat = cur.getDouble(2);
                        double lon = cur.getDouble(3);
                        double alt = cur.getDouble(4);
                        String strPos = String.format("%d %d %.2f %.2f %.0f\n", idGps1, id, lat, lon, alt);
                        strText += strPos; // riadok po riadku
                    } while (cur.moveToNext());
                }
                cur.close();
                textView.setText(strText);
            }
            db.close();
        } catch (SQLiteException se) {
            textView.append("Exception: " + se.getMessage() + "\n");
        }
    }

    public void onClickSelectGPSParam(View view) {
        String strText;
        textView.setText("");
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cur = db.rawQuery("select MinTime, MinDist from GPSPARAM", null);
            if (cur != null) {
                strText = "";
                if (cur.moveToFirst()) {
                    do {
                        int minTime = cur.getInt(0);
                        int minDist = cur.getInt(1);
                        String strRiadok = String.format("%d %d\n", minTime, minDist);
                        strText += strRiadok;
                    } while (cur.moveToNext()); // mal by byt iba 1 zaznam, ale pre istotu...
                }
                cur.close();
                textView.setText(strText);
            }
            db.close();
        } catch (SQLiteException se) {
            textView.append("Exception: " + se.getMessage() + "\n");
        }
    }

    public void onClickDeleteAllGPS(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("VYMAZAT");
        builder.setMessage("Vymazat vsetky cesty?");

        builder.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                db.execSQL("delete from GPS1");
                db.execSQL("delete from GPS2");
                db.close();
            }
            dialog.dismiss();
            db.close();
            }
        });

        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onClickInsertPrimaryData(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Primarne data");
        builder.setMessage("Chcete vlozit primarne data(t.j. 2 cesty)?");

        builder.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.InsertPrimaryData(db);
            db.close();
            dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onClickCreateTables(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo-vytvorenie tabuliek");
        builder.setMessage("Chcete nanovo vytvorit databazove tabulky (vsetky data zmiznu)?");

        builder.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.CreateTables(db);
            db.close();
            dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
