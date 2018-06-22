package sk.upjs.kubini.gps2.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;

import java.security.Provider;
import java.util.UUID;

import static sk.upjs.kubini.gps2.provider.Defaults.DEFAULT_CURSOR_FACTORY;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "gps2db";
    public static final int DATABASE_VERSION = 5;
    private double lastLat, lastLon, lastAlt;
    private long lastPosunSec;
    private long CASOVY_POSUN_MS = 10800000; // 3 hodiny posun od greenwichskeho casu

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, DEFAULT_CURSOR_FACTORY, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CreateTables(db);
    }

    public void CreateTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS GPS1");
        db.execSQL("DROP TABLE IF EXISTS GPS2");
        db.execSQL("DROP TABLE IF EXISTS GPSPARAM");
        db.execSQL("CREATE TABLE GPS1 (_ID INTEGER PRIMARY KEY, Nazov TEXT, TimeStamp DATETIME, UUIDHelp TEXT)");
        db.execSQL("CREATE TABLE GPS2 (_ID INTEGER PRIMARY KEY, _ID_GPS1 INTEGER, Lat REAL, Lon REAL, Alt REAL, TimeStamp DATETIME)");
        db.execSQL("CREATE TABLE GPSPARAM (MinTime INTEGER, MinDist INTEGER)");
// Pozn.: Kazda tabulda v SQLite ma automaticke implicitne pole rowid, ktore ma vlastnost "autoincrement".
//        V pripade tabuliek GPS1, GPS2 databaza automaticky namiesto rowid pouzije _ID (lebo je "integer primary key") -
//        vid dokumentacia SQLite na webe
        InsertPrimaryData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void DeteteFromTables(SQLiteDatabase db) {
        db.execSQL("delete from GPS1");
        db.execSQL("delete from GPS2");
    }

    public int InsertGps1(SQLiteDatabase db, String nazov) {
        String strId;
        int id = 0;
        String uuid = UUID.randomUUID().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Nazov", nazov);
        contentValues.put("TimeStamp", System.currentTimeMillis() + CASOVY_POSUN_MS); // / 1000);
        contentValues.put("UUIDHelp", uuid);
        db.insert("GPS1", Defaults.NO_NULL_COLUMN_HACK, contentValues);
        String sql =  "select _ID from GPS1 where UUIDHelp = '" + uuid + "'";
        Cursor cur = db.rawQuery(sql, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                id = cur.getInt(0);
            }
            cur.close();
        }
        return id;
    }

    public void InsertGps2(SQLiteDatabase db, int id, double lat, double lon, double alt, double posunSec) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_ID_GPS1", id);
        contentValues.put("Lat", lat);
        contentValues.put("Lon", lon);
        contentValues.put("Alt", alt);
        contentValues.put("TimeStamp", System.currentTimeMillis() + (posunSec * 1000) + CASOVY_POSUN_MS); // / 1000);
        db.insert("GPS2", Defaults.NO_NULL_COLUMN_HACK, contentValues);
    }

    public void InsertGpsParams(SQLiteDatabase db, int minTime, int minDist) {
        db.execSQL("delete from GPSPARAM");
        ContentValues contentValues = new ContentValues();
        contentValues.put("MinTime", minTime);
        contentValues.put("MinDist", minDist);
        db.insert("GPSPARAM", Defaults.NO_NULL_COLUMN_HACK, contentValues);
    }

    public void InsertPrimaryData(SQLiteDatabase db) {
        int id;

        InsertGpsParams(db, 6000, 0);

        lastAlt = 200;
        lastLat = 20.400;
        lastLon = 40.200;
        lastPosunSec = 0;
        id = InsertGps1(db, "Prvotna cesta1");
        if (id > 0) {
            InsertGPS2_Interval(db, id, 0.001, 0.002, 2, 2, 10);
            InsertGPS2_Interval(db, id, 0.002, -0.001, 1, 3, 8);
            InsertGPS2_Interval(db, id, -0.001, 0.004, -2, 1, 9);
            InsertGPS2_Interval(db, id, -0.001, 0.004, -2, 4, 5);
            InsertGPS2_Interval(db, id, 0.001, 0.001, 2, 1, 1);
            InsertGPS2_Interval(db, id, -0.001, 0.007, 1, 2, 5);
            InsertGPS2_Interval(db, id, 0.001, 0.002, -2, 1, 3);
        }
        id = InsertGps1(db, "Prvotna cesta2");
        if (id > 0) {
            InsertGPS2_Interval(db, id, 0.004, 0.002, 2, 2, 7);
            InsertGPS2_Interval(db, id, 0.002, -0.001, 1, 3, 8);
            InsertGPS2_Interval(db, id, -0.001, 0.004, -2, 1, 9);
            InsertGPS2_Interval(db, id, 0.001, 0.002, -3, 2, 7);
            InsertGPS2_Interval(db, id, 0.001, -0.002, 1, 3, 1);
            InsertGPS2_Interval(db, id, -0.002, 0.004, -2, 4, 2);
            InsertGPS2_Interval(db, id, 0.003, -0.002, 2, 2, 4);
            InsertGPS2_Interval(db, id, 0.001, -0.001, 1, 3, 5);
            InsertGPS2_Interval(db, id, -0.009, 0.010, -2, 1, 4);
        }
    }

    private void InsertGPS2_Interval(SQLiteDatabase db, int id, double dLat, double dLon, double dAlt, long sec, int cnt) {
        for (int i = 0; i < cnt; i++) {
            InsertGps2(db, id, lastLat,  lastLon, lastAlt, lastPosunSec);
            lastLat += dLat;
            lastLon += dLon;
            lastAlt += dAlt;
            lastPosunSec += sec;
        }

    }
}
