package sk.upjs.kubini.gps2.pocasieProvider;

import android.content.Context;

import java.util.List;

public class PocasieLoader  extends AbstractObjectLoader<List<String>> {
    private final WebPocasie webPocasie;

    public PocasieLoader(Context context) {
        super(context);
        this.webPocasie = new WebPocasie();
    }

    @Override
    public List<String> loadInBackground() {
        /*
        ZiskavaniePolohy zp = new ZiskavaniePolohy();
        double sirka =zp.getLatitude();
        double dlzka =zp.getLongitude();
        */
        //return this.webPocasie.loadWeather(sirka, dlzka);
        return null;
    }
}

