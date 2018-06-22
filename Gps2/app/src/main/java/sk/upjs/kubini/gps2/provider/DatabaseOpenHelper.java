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
        contentValues.put("TimeStamp", System.currentTimeMillis()); // / 1000);
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

    public void InsertGps2(SQLiteDatabase db, int id, double lat, double lon, double alt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_ID_GPS1", id);
        contentValues.put("Lat", lat);
        contentValues.put("Lon", lon);
        contentValues.put("Alt", alt);
        contentValues.put("TimeStamp", System.currentTimeMillis()); // / 1000);
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

        id = InsertGps1(db, "Prvotna cesta1");
        if (id > 0) {
            InsertGps2(db, id, 20.392, 49.234, 247);
            InsertGps2(db, id, 20.391, 49.235, 251);
            InsertGps2(db, id, 20.390, 49.236, 256);
            InsertGps2(db, id, 20.390, 49.238, 252);
        }
        id = InsertGps1(db, "Prvotna cesta2");
        if (id > 0) {
            InsertGps2(db, id, 20.492, 49.134, 347);
            InsertGps2(db, id, 20.491, 49.135, 351);
            InsertGps2(db, id, 20.490, 49.136, 356);
            InsertGps2(db, id, 20.490, 49.138, 352);
        }
        InsertGpsParams(db, 6000, 0);
    }
}
