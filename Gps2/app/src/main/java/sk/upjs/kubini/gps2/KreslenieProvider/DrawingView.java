package sk.upjs.kubini.gps2.KreslenieProvider;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import sk.upjs.kubini.gps2.StruLocation;

//https://stackoverflow.com/questions/15704205/how-to-draw-line-on-imageview-along-with-finger-in-android

public class DrawingView extends View {
    Paint mPaint;
    Bitmap mBitmap;
    Canvas mCanvas;
    Path mPath;
    Paint mBitmapPaint;
    ArrayList<StruLocation> arrLoc = new ArrayList<StruLocation>();;
    int drawTyp = 0;
    double m_vzdialenost = 0;
    double m_stupanie = 0;
    double m_klesanie = 0;
    float m_X = 0;
    float m_Y = 0;
    float m_Xprev = 0;
    float m_Yprev = 0;
    double lat_min = 0;
    double lat_max = 0;
    double lon_min = 0;
    double lon_max = 0;

    private int sirka, vyska; //Vyska je 400dp
    private double pomerVysky = 1;

    public DrawingView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0x00FF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);

        mPath = new Path();
        mBitmapPaint = new Paint();
//        mBitmapPaint.setColor(0x00FF00FF); // Green
        mBitmapPaint.setStrokeWidth(5);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.sirka = displayMetrics.heightPixels;
        this.vyska = displayMetrics.widthPixels;
        //if(vyska*pomerVysky > sirka) vyska = (int) (vyska * 0.5);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //if(h*pomerVysky > w) h = (int) (h * 0.5);

        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
//        mCanvas.drawColor(Color.LTGRAY);

        this.sirka = w;
        this.vyska = h;
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (drawTyp == 0) {
            drawPrevysenie(canvas);
        }
        else {
            if (drawTyp == 1) {
                drawPrevysenie(canvas);
            } else {
                super.draw(canvas);
                canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
//        canvas.drawPath(mPath, mPaint);

                float h = vyska - 10;
                float w = sirka - 10;

                //Miesto na vykreslenie cesty
                canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
                canvas.drawLine(0, 0, w, 0, mBitmapPaint);
                canvas.drawLine(0, 0, 0, h, mBitmapPaint);
                canvas.drawLine(w, h, w, 0, mBitmapPaint);
                canvas.drawLine(w, h, 0, h, mBitmapPaint);

                canvas.drawLine(0, 0, w / 3, h, mBitmapPaint);
            }
        }
    }

    private void drawPrevysenie(Canvas canvas) {
        m_vzdialenost = 0;
        m_stupanie = 0;
        m_klesanie = 0;
        long startCas = 0;
        long casMiliSec = 0;
        double distanceInMeters = 0;
        int pocetGPS = 0;
        Location L0 = new Location("");
        Location L1 = new Location("");
        float h = vyska - 10;
        float w = sirka - 10;

        float x0, x1, y0, y1, cx, cy;

        pocetGPS = arrLoc.size();
        if (pocetGPS > 0) {
            StruLocation loc0 = arrLoc.get(0);
            lat_min = loc0.lat;
            lat_max = loc0.lat;
            lon_min = loc0.lon;
            lon_max = loc0.lon;
            StruLocation loc1;
            for (int i = 1; i < pocetGPS; i++) {
                loc1 = arrLoc.get(i);
                if (loc1.lat < lat_min) { lat_min = loc1.lat; }
                if (loc1.lat > lat_max) { lat_max = loc1.lat; }
                if (loc1.lon < lon_min) { lon_min = loc1.lon; }
                if (loc1.lon > lon_max) { lon_max = loc1.lon; }
//===================================================================== cas
                casMiliSec += (loc1.timeStamp - loc0.timeStamp);
//===================================================================== prevysenia
                if (loc0.alt < loc1.alt) {
                    m_stupanie += loc1.alt - loc0.alt;
                } else {
                    m_klesanie += loc0.alt - loc1.alt;
                }
//===================================================================== vzdialenost
                L0.setLatitude(loc0.lat);
                L0.setLongitude(loc0.lon);
                L1.setLatitude(loc1.lat);
                L1.setLongitude(loc1.lon);
                distanceInMeters = L0.distanceTo(L1);
                m_vzdialenost += distanceInMeters;
                loc0 = loc1;
            }
            super.draw(canvas);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawLine(0, 0, w, 0, mBitmapPaint);
            canvas.drawLine(0, 0, 0, h, mBitmapPaint);
            canvas.drawLine(w, h, w, 0, mBitmapPaint);
            canvas.drawLine(w, h, 0, h, mBitmapPaint);

//            canvas.drawLine(0, 0, w / 3, h, mBitmapPaint);
            m_X = 0;
            m_Y = 0;
            double vzd = 0;
            double vys = 0;
            double pocVyska = 0;
            loc0 = arrLoc.get(0);
            for (int i = 1; i < arrLoc.size(); i++) {
                loc1 = arrLoc.get(i);
                L0.setLatitude(loc0.lat);
                L0.setLongitude(loc0.lon);
                L1.setLatitude(loc1.lat);
                L1.setLongitude(loc1.lon);
                distanceInMeters = L0.distanceTo(L1);
                vzd += distanceInMeters;
                vys += loc1.alt - loc0.alt;
                loc0 = loc1;
                if (drawTyp == 0) {
                    convertPrevysenie(vzd, vys);
                    cx = sirka/2;
                    cy = vyska/2;
                    x0 = (float) (cx + (m_Xprev - cx) * 0.7);
                    y0 = (float) (cy + (m_Yprev - cy) * 0.7);
                    x1 = (float) (cx + (m_X - cx) * 0.7);
                    y1 = (float) (cy + (m_Y - cy) * 0.7);
//                    canvas.drawLine(x0, y0, x1, y1, mBitmapPaint);
                    canvas.drawLine(x1, vyska - vyska/4, x1, vyska - y1, mBitmapPaint);
//                    canvas.drawLine(m_X,vyska , m_X, vyska - m_Y, mBitmapPaint);
                }
                if (drawTyp == 1) {
                    convertPolohu(loc1.lat, loc1.lon);
                    cx = sirka/2;
                    cy = vyska/2;
                    x0 = (float) (cx + (m_Xprev - cx) * 0.7);
                    y0 = (float) (cy + (m_Yprev - cy) * 0.7);
                    x1 = (float) (cx + (m_X - cx) * 0.7);
                    y1 = (float) (cy + (m_Y - cy) * 0.7);
                    canvas.drawLine(x0, y0, x1, y1, mBitmapPaint);
                    //                    canvas.drawLine(m_Xprev, m_Yprev, m_X, m_Y, mBitmapPaint);
                }
            }
        }
    }

    private void convertPrevysenie(double vzdial, double vys) {
        m_Xprev = m_X;
        m_Yprev = m_Y;
        double pocVys = m_stupanie;
        double maxV = m_stupanie + m_klesanie;
        m_X = (float) ((vzdial / m_vzdialenost) * sirka);
        m_Y = (float) (vyska - (((vys / maxV) * vyska) + vyska / 2));
    }

    private void convertPolohu(double lat, double lon) {
        m_Xprev = m_X;
        m_Yprev = m_Y;

        m_X = (float) ((lon - lon_min) / (lon_max - lon_min) * sirka);
        m_Y = (float) ((lat - lat_min) / (lat_max - lat_min) * sirka);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        //mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
//        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
//        mCanvas.drawPath(mPath, mPaint);
        //mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        // kill this so we don't double draw
//        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void setStruLocations(ArrayList<StruLocation> arrLocation, int typ) {
        arrLoc.clear();
        for (int i = 0; i < arrLocation.size(); i++) {
            arrLoc.add(arrLocation.get(i));
        }
        drawTyp = typ;
    }

}









