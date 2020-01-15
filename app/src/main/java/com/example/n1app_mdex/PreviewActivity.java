package com.example.n1app_mdex;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;

public class PreviewActivity extends Activity {
    private final String TAG = "PreviewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        String path = getIntent().getStringExtra("file_path");
        if (path != null && path.length() > 0) {
            ImageView iv = (ImageView)findViewById(R.id.image1);
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            Double[] gpsInfo = ExifInfo.getGpsInfo(path);
            if (gpsInfo != null) {
                TextView tv = (TextView)findViewById(R.id.text1);
                tv.setText("緯度:" +gpsInfo[0] + ", 経度:" + gpsInfo[1]);
            }
        }
    }
}
