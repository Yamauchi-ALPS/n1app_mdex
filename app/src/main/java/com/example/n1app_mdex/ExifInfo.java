package com.example.n1app_mdex;

import android.media.ExifInterface;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ExifInfo {
    static private String TAG = "ExifInfo";

    static public Double[] getGpsInfo(String path) {
        Double[] ret = null;
        try {
            InputStream in = getInputStream(path);
            if (in != null) {
                ExifInterface exifInterface = new ExifInterface(getInputStream(path));
                String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                // 緯度経度の値を変換
                ret = new Double[]{ExifLatitudeToDegrees(latitudeRef, latitude),
                                    ExifLongitudeToDegrees(longitudeRef, longitude)};
//                TextView tv = (TextView)findViewById(R.id.text1);
//                tv.setText("緯度:" +lat + ", 経度:" + lng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    static private InputStream getInputStream(String sourcePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourcePath);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + sourcePath);
        }
        return inputStream;
    }

    // 60進数を10進数に変換
    static private double ExifHourMinSecToDegrees(String exifhourminsec) {
        String hourminsec[] = exifhourminsec.split(",",0);
        String hour[] = hourminsec[0].split("/",0);
        String min[] = hourminsec[1].split("/",0);
        String sec[] = hourminsec[2].split("/",0);

        double dhour = (double)Integer.parseInt(hour[0]) / (double)Integer.parseInt(hour[1]);
        double dmin = (double)Integer.parseInt(min[0]) / (double)Integer.parseInt(min[1]);
        double dsec = (double)Integer.parseInt(sec[0]) / (double)Integer.parseInt(sec[1]);
        double degrees = dhour + dmin / 60.0 + dsec / 3600.0;

        return degrees;
    }

    // 緯度の変換
    static private double ExifLatitudeToDegrees(String ref, String latitude) {
        return ref.equals("S") ? -1.0 : 1.0 * ExifHourMinSecToDegrees(latitude);
    }

    // 経度の変換
    static private double ExifLongitudeToDegrees(String ref, String longitude) {
        return ref.equals("W") ? -1.0 : 1.0 * ExifHourMinSecToDegrees(longitude);
    }
}
