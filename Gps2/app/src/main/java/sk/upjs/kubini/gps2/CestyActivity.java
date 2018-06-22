package sk.upjs.kubini.gps2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import sk.upjs.kubini.gps2.provider.MyGPS1Contract;

public class CestyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int GPS1_LOADER = 3;
    TextView nadpisView;
    ListView listViewCesty;
    ArrayList<Integer>   itemsID;
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
        getLoaderManager().initLoader(GPS1_LOADER, Bundle.EMPTY, this);
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
        Calendar cal = Calendar.getInstance(Locale.GERMANY);
        cal.setTimeInMillis(timeStamp);
        String strDateTime = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        return strDateTime;
    }
    public void SpustJednuCestu(int _ID) {
        Intent intent = new Intent(this, JednaCestaActivity.class);
        intent.putExtra("_ID", _ID); // prenos parametra do dalsej aktivity
        startActivity(intent);
    }

    AdapterView.OnItemClickListener myListViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int _ID = itemsID.get(position);
            SpustJednuCestu(_ID);
        }
    };

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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        String strRiadok;
        int id;
        String nazov;
        long lTimeStamp;
        String strDateTime;
        int pocetCiest = 0;
        String strCesty = "";

        itemsID.clear();
        itemsCesty.clear();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    id          = cursor.getInt   (0);
                    nazov       = cursor.getString(1);
                    lTimeStamp  = cursor.getLong  (2);
                    strDateTime = convertDateTime(lTimeStamp);
                    strRiadok = String.format("%d, %s, %s\n", id, nazov, strDateTime);
                    strCesty += strRiadok;
                    pocetCiest++;
                    itemsID.add(id);
                    itemsCesty.add(new ItemCesta(id+" ",nazov, "   " + strDateTime));
                } while (cursor.moveToNext());
            }
        }
        m_Nadpis = String.format("Zaznamenané cesty (%d)", pocetCiest);
        CestyListAdapter cestyListAdapter = new CestyListAdapter(getApplicationContext(), itemsCesty);
        listViewCesty.setAdapter(cestyListAdapter);
        listViewCesty.setOnItemClickListener(myListViewOnItemClickListener);
        nadpisView.setText(m_Nadpis);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    //========== Action bar =======

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zaznamy_ciest_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.btnBack) {
            onBack(null);
        }

        return super.onOptionsItemSelected(item);
    }
}
