package sk.upjs.kubini.gps2;

public class StruLocation {
    StruLocation(int id, double la, double lo, double al, long ti) {
        _ID = id; lat = la; lon = lo; alt=al; timeStamp = ti;
    }
    public int _ID;
    public double lat;
    public double lon;
    public double alt;
    public long timeStamp;
}
