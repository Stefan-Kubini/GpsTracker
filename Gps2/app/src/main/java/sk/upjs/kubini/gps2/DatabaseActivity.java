package sk.upjs.kubini.gps2;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.UUID;

import sk.upjs.kubini.gps2.provider.DatabaseOpenHelper;
import sk.upjs.kubini.gps2.provider.Defaults;
import sk.upjs.kubini.gps2.provider.MyGPS1Contract;

public class DatabaseActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int GPS1_LOADER = 3;
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
        getLoaderManager().initLoader(GPS1_LOADER, Bundle.EMPTY, this);
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
        builder.setMessage("Vymazať všetky cesty?");

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
        builder.setMessage("Chcete vložiť primárne dáta(t.j. 2 cesty)?");

        builder.setPositiveButton("Áno", new DialogInterface.OnClickListener() {
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
        builder.setMessage("Chcete nanovo vytvoriť databázové tabuľky (všetky dáta zmiznú)?");

        builder.setPositiveButton("Áno", new DialogInterface.OnClickListener() {
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

    public void onBack(View view) {
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id != GPS1_LOADER) {
            throw new IllegalStateException("Invalid Loader with ID: " + id);
        }
        CursorLoader loader = new CursorLoader(this);
        loader.setUri(MyGPS1Contract.GPS1.CONTENT_URI);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cur) {
        String strText;
        textView.setText("");
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
            textView.setText(strText);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
    //========== Action bar =======

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sprava_databazy_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.btnBack) {
            onBack(null);
        }
        if (itemId == R.id.insertPrimaryData) {
            onClickInsertPrimaryData(null);
        }
        if (itemId == R.id.deleteAllGPS) {
            onClickDeleteAllGPS(null);
        }
        if (itemId == R.id.selectGPSParam) {
            onClickSelectGPSParam(null);
        }
        if (itemId == R.id.selectGPS2) {
            onClickSelectGPS2(null);
        }
        if (itemId == R.id.selectGPS1) {
            onClickSelectGPS1(null);

        }

        return super.onOptionsItemSelected(item);
    }
}
