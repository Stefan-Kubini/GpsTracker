package sk.upjs.kubini.gps2.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public interface MyGPS1Contract {

    String AUTHORITY = "sk.upjs.kubini.gps2";

    interface GPS1 extends BaseColumns {
        String TABLE_NAME = "GPS1";

        String _ID = "_ID";
        String NAZOV = "Nazov";
        String TIMESTAMP = "TimeStamp";

        Uri CONTENT_URI = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();
    }
}
