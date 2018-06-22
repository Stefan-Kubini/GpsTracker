package sk.upjs.kubini.gps2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import sk.upjs.kubini.gps2.provider.DatabaseOpenHelper;

public class ParamActivity extends AppCompatActivity {
    private DatabaseOpenHelper dbHelper;
    private int m_minTime = 0;
    private int m_minDist = 0;
    private EditText editMinTime;
    private EditText editMinDist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);
        dbHelper = new DatabaseOpenHelper(getApplicationContext());
        getParameters();
        editMinTime = (EditText) findViewById(R.id.editMinTime);
        editMinDist = (EditText) findViewById(R.id.editMinDist);
        editMinTime.setText(m_minTime + "");
        editMinDist.setText(m_minDist + "");
    }

    private void getParameters() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = db.rawQuery("select MinTime, MinDist from GPSPARAM", null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                m_minTime = cur.getInt(0);
                m_minDist = cur.getInt(1);
            }
            cur.close();
        }
        db.close();
    }

    public void onSaveParams(View view) {
        String strMinTime = editMinTime.getText().toString();
        m_minTime = Integer.parseInt(strMinTime);

        String strMinDist = editMinDist.getText().toString();
        m_minDist = Integer.parseInt(strMinDist);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.InsertGpsParams(db, m_minTime, m_minDist);

        db.close();
        super.onBackPressed();
    }

    public void onCancelParams(View view) {
        super.onBackPressed();
    }
}
