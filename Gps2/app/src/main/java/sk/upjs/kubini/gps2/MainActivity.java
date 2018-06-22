package sk.upjs.kubini.gps2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static int g_Cesta_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //========== Action bar =======

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_list_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.btnGPS) {
            Intent intent = new Intent(this, GpsActivity.class);
            startActivity(intent);
        }
        if (itemId == R.id.btnCesty) {
            Intent intent = new Intent(this, CestyActivity.class);
            startActivity(intent);
        }
        if (itemId == R.id.btnPozicia) {
            Intent intent = new Intent(this, PoziciaActivity.class);
            startActivity(intent);
        }
        if (itemId == R.id.btnNastavenia) {
            Intent intent = new Intent(this, ParamActivity.class);
            startActivity(intent);
        }
        if (itemId == R.id.btnDatabase) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Heslo");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setMessage("Zadajte prosím heslo root");
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String pwd = input.getText().toString();
                    if (pwd.equals("root")) {
                        gotoDatabaza();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    input.setText("");
                    dialog.cancel();
                }
            });
            builder.show();
            //        Intent intent = new Intent(this, DatabaseActivity.class);
            //        startActivity(intent);
        }
        if (itemId == R.id.btnKoniec) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

//========== Menu s tlacidlami ==========
        public void onClickBtnGPS(View view) {
            Intent intent = new Intent(this, GpsActivity.class);
            startActivity(intent);
        }
        public void onClickBtnCesty(View view) {
            Intent intent = new Intent(this, CestyActivity.class);
            startActivity(intent);
        }

        public void onClickBtnStiahnutMapu(View view) {
        }

        public void onClickBtnNastavenia(View view) {
            Intent intent = new Intent(this, ParamActivity.class);
            startActivity(intent);
        }

        public void onClickBtnDatabase(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Heslo");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setMessage("Zadajte prosím heslo root");
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String pwd = input.getText().toString();
                    if (pwd.equals("root")) {
                        gotoDatabaza();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    input.setText("");
                    dialog.cancel();
                }
            });
            builder.show();
    //        Intent intent = new Intent(this, DatabaseActivity.class);
    //        startActivity(intent);
        }

        public void gotoDatabaza() {
            Intent intent = new Intent(this, DatabaseActivity.class);
            startActivity(intent);
        }

        public void onClickBtnPozicia(View view) {
            Intent intent = new Intent(this, PoziciaActivity.class);
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
