package sk.upjs.kubini.gps2;

import sk.upjs.kubini.gps2.pocasieProvider.ZiskavaniePolohy;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.text.DecimalFormat;

public class PoziciaActivity extends AppCompatActivity {

    //private ZiskavaniePolohy gpsTracker;
    private TextView tvLatitude,tvLongitude, suradnice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktualna_poloha);

        //suradnice = (TextView)findViewById(R.id.aktualnaPoloha);
        tvLatitude = (TextView)findViewById(R.id.latitude);
        tvLongitude = (TextView)findViewById(R.id.longitude);

        /*
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        */

        //this.getLocation();
    }

    public void getLocation(/*View view*/){
        ZiskavaniePolohy gpsTracker;
        gpsTracker = new ZiskavaniePolohy(PoziciaActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            DecimalFormat df = new DecimalFormat("#.###"); //nastavenie postu desatinych miest

            tvLatitude.setText(df.format(latitude));
            tvLongitude.setText(df.format(longitude));
        }else{
            gpsTracker.showSettingsAlert();
        }
    }
}




