package sk.upjs.kubini.gps2.pocasieProvider;

import android.content.Context;
import android.util.Log;

import java.util.List;

public class PocasieLoader  extends AbstractObjectLoader<List<String>> {
    private final WebPocasie webPocasie;
    private double m_sirka;
    private double m_dlzka;

    public PocasieLoader(Context context, double sirka, double dlzka) {
        super(context);
        this.webPocasie = new WebPocasie(sirka, dlzka);
    }

    @Override
    public List<String> loadInBackground() {
        List<String> listPocasie;

        //Log.d("MyApp","Vratilo poziciu v loaderi " + m_sirka + "[|" + m_dlzka);
        listPocasie = this.webPocasie.loadWeather();
        //Log.d("MyApp","Vratilo pocasie v loaderi " + listPocasie.size());
        return listPocasie;
    }
}
