package sk.upjs.kubini.gps2.pocasieProvider;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class WebPocasie {

//    public static String url = ""; //priklad: http://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=a6008a92c95c0dfa186a75bd4cacb561
    public static String url = "http://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=a6008a92c95c0dfa186a75bd4cacb561";

    private URL serviceUrl;

    public WebPocasie(double sirka, double dlzka) {
        url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + sirka + "&lon=" + dlzka + "&appid=a6008a92c95c0dfa186a75bd4cacb561";
        Log.d("Pocasie ", url);
        try {
            this.serviceUrl = new URL(url);
        } catch (MalformedURLException e) {
            // URL is hardwired and well-formed
        }
    }

    public List<String> loadWeather() {
        InputStream in = null;
        try {
            in = this.serviceUrl.openStream();
            String json = toString(in);

            //Log.d("Pocasie ", "Dlzka vystupu " + json.length());

            JSONObject strana = new JSONObject(json);
            JSONArray casoveUseky = strana.getJSONArray("list");

            List<struPocasie> predpovedPocasia = new ArrayList<struPocasie>();
            List<String> predpovedPocasiaStr = new ArrayList<>();

            for (int i = 0; i < casoveUseky.length(); i++) {
                JSONObject polozka = casoveUseky.getJSONObject(i);
                struPocasie elem = new struPocasie();

                elem.casPocasia = polozka.getString("dt_txt");
                elem.oblacnost = polozka.getJSONObject("clouds").getInt("all");
                elem.rychlostVetra = polozka.getJSONObject("wind").getDouble("speed");
                elem.smerVetra = polozka.getJSONObject("wind").getDouble("deg");
                elem.slovnyPopis = polozka.getJSONArray("weather").getJSONObject(0).getString("description");

                if (polozka.has("rain"))
                    if (polozka.getJSONObject("rain").has("3h"))
                        elem.mnozstvoZrazok = polozka.getJSONObject("rain").getDouble("3h");


                double t1 = polozka.getJSONObject("main").getDouble("temp");

                //prevod z farenheitov na stupne celzia
                //t1 = (5 * (t1 + 459067)) / 9.0; //prevod na stupne celzia
                //elem.teplota = t1;
                elem.teplota = t1 -272.15; //prevod z kelvinov na stupne celzia

                elem.vlhkost = polozka.getJSONObject("main").getInt("humidity");
                elem.tlak = polozka.getJSONObject("main").getDouble("pressure");

                predpovedPocasia.add(elem);
                //System.out.println(elem.toString());
            }

            for(int i=0; i<predpovedPocasia.size(); i++) {
                if(i >= 10) break;
                predpovedPocasiaStr.add(predpovedPocasia.get(i).toString());
            }

            //Log.d("Pocasie ", "Pocet poloziek " + predpovedPocasiaStr.size());
            return predpovedPocasiaStr;

        } catch (IOException e) {
            Log.e(getClass().getName(), "I/O Exception while loading users", e);
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        } catch (JSONException e) {
            Log.e(getClass().getName(), "JSON parsing error while loading users", e);
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.w(getClass().getName(), "Unable to close input stream", e);
                    e.printStackTrace();
                }
            }
        }
    }

    private String toString(InputStream in) {
        Scanner scanner = new Scanner(in, "utf-8");
        StringBuilder sb = new StringBuilder();
        while(scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        return sb.toString();
    }
}
