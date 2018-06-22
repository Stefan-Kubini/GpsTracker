package sk.upjs.kubini.gps2;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import sk.upjs.kubini.gps2.provider.DatabaseOpenHelper;

public class CestyActivity extends AppCompatActivity {
    TextView nadpisView;
    ListView listViewCesty;
    ArrayList<Integer>   itemsID;
    ArrayList<String>    items;
    ArrayList<ItemCesta> itemsCesty;
    String m_Nadpis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cesty);
        nadpisView    = (TextView)findViewById(R.id.textViewCesty);
        listViewCesty = (ListView) findViewById(R.id.list_view_items);
        itemsID    = new ArrayList<Integer>();
        itemsCesty = new ArrayList<ItemCesta>();

        loadGPS1();

        CestyListAdapter cestyListAdapter = new CestyListAdapter(getApplicationContext(), itemsCesty);
        listViewCesty.setAdapter(cestyListAdapter);
        listViewCesty.setOnItemClickListener(myListViewOnItemClickListener);

        nadpisView.setText(m_Nadpis);
    }

    private class ItemCesta {
        private String itemID;
        private String itemNazov;
        private String itemDateTime;

        public ItemCesta(String id, String nazov, String dateTime) {
            this.itemID = id;
            this.itemNazov = nazov;
            this.itemDateTime = dateTime;
        }
        public String getItemID() { return this.itemID; }
        public String getItemNazov() { return itemNazov; }
        public String getItemDateTime() { return itemDateTime; }
    }

    private class CestyListAdapter extends BaseAdapter {
        private Context context; //context
        private ArrayList<ItemCesta> items; //data source of the list adapter

        public CestyListAdapter(Context context, ArrayList<ItemCesta> items) {
            this.context = context;
            this.items = items;
        }
        @Override
        public int getCount() {
            return items.size(); //returns total of items in the list
        }
        @Override
        public Object getItem(int position) {
            return items.get(position); //returns list item at the specified position
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // inflate the layout for each list row
            if (convertView == null) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.listview_row_cesty, parent, false);
            }
            ItemCesta currentItem = (ItemCesta) getItem(position);
            TextView textViewItemId       = (TextView) convertView.findViewById(R.id.text_view_item_id);
            TextView textViewItemNazov    = (TextView) convertView.findViewById(R.id.text_view_item_nazov);
            TextView textViewItemDateTime = (TextView) convertView.findViewById(R.id.text_view_item_dateTime);
            textViewItemId.      setText(currentItem.getItemID());
            textViewItemNazov.   setText(currentItem.getItemNazov());
            textViewItemDateTime.setText(currentItem.getItemDateTime());
            // returns the view for the current row
            return convertView;
        }
    }
    private String convertDateTime(long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp);
        String strDateTime = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return strDateTime;
    }
    public void loadGPS1() {
// Prerobit na CursorLoader podla tohoto vzoru:
// https://stackoverflow.com/questions/18326954/how-to-read-an-sqlite-db-in-android-with-a-cursorloader
        DatabaseOpenHelper dbHelper;
        String strRiadok;
        int id;
        String nazov;
        long lTimeStamp;
        String strDateTime;
        int pocetCiest = 0;

//        items.clear();
        itemsID.clear();
        itemsCesty.clear();
        dbHelper = new DatabaseOpenHelper(getApplicationContext());;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null) {
            Cursor cur = db.rawQuery("select _ID, Nazov, TimeStamp from GPS1 order by _ID DESC", null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        id          = cur.getInt   (0);
                        nazov       = cur.getString(1);
                        lTimeStamp  = cur.getLong  (2);
                        strDateTime = convertDateTime(lTimeStamp);
                        itemsID.add(id);
                        itemsCesty.add(new ItemCesta(id+" ",nazov, "   " + strDateTime));
                        pocetCiest++;
                    } while (cur.moveToNext());
                }
                cur.close();
            }
            db.close();
        }
        m_Nadpis = String.format("Zaznamenané cesty (%d)", pocetCiest);
    }

    AdapterView.OnItemClickListener myListViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int _ID = itemsID.get(position);

            AlertDialog alertDialog = new AlertDialog.Builder(CestyActivity.this).create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Kliknute ID cesty: " + _ID);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    };
}
