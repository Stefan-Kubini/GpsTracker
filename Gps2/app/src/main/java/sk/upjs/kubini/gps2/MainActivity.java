package sk.upjs.kubini.gps2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static int g_Cesta_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickBtnGPS(View view) {
        Intent intent = new Intent(this, GpsActivity.class);
        startActivity(intent);
    }

    public void onClickBtnCesty(View view) {
        Intent intent = new Intent(this, CestyActivity.class);
        startActivity(intent);
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Číslo cesty");
//        alert.setMessage("Zadajte číslo cesty :");
//
//        // Set an EditText view to get user input
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER);
//        alert.setView(input);
//
//        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                String value = input.getText().toString();
//                return;
//            }
//        });
//
//        alert.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        return;
//                    }
//                });
//        alert.show();
    }

    public void onClickBtnAktualnaPozicia(View view) {
        Intent intent = new Intent(this, PoziciaActivity.class);
        startActivity(intent);
    }

    public void onClickBtnStiahnutMapu(View view) {
    }

    public void onClickBtnNastavenia(View view) {
        Intent intent = new Intent(this, ParamActivity.class);
        startActivity(intent);
    }

    public void onClickBtnDatabase(View view) {
        Intent intent = new Intent(this, DatabaseActivity.class);
        startActivity(intent);
    }

    public void onClickBtnKoniec(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        // closing Entire Application
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }
}
