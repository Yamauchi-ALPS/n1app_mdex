package com.example.n1app_mdex;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//ファイル読込
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;


//構造体代替物　
class fileDataStruct {
    String file_path;
    String file_name;
    double lat;
    double lng;
    String tag1;
    String tag2;
    String tag3;
    String tag4;
    String tag5;
}

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     *
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public static final int READ_MAX = 30;//ファイル読込上限(定数)
    // 今回はファイル読込上限を定数として決め打ちする
    // 将来的には、ファイルや配列を使わずSQLから直接読めるようにしたい

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int fileNum=0;
        fileDataStruct[] allFileData = new fileDataStruct[READ_MAX];

        /////ファイル生成（実際にはここでは行わない処理）

        /* 同プログラム内でこういったファイルを作った、と言う想定でファイルを生成
         * 「"パス","ファイル名","緯度","経度","タグ1","タグ2","タグ3","タグ4","タグ5"」 を想定
         *
         * タグが5つに満たない場合でも、句点は1行に8つ入るつくりとしておく
         * 「"パス","ファイル名","緯度","経度","タグ1",,,,」
         */


        /////ここまでファイル生成（実際にはここでは行わない処理）

        /////ファイル読み込み


        try{
            FileInputStream in = openFileInput( "gps.txt" );
            BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );
            String tmp;


            //読み込み処理
            for (int i = 0; i < READ_MAX; i++) {
                if( (tmp = reader.readLine()) != null ) {
                    String[] getTmpData = tmp.split(",", -1);
                    allFileData[i] = new fileDataStruct();//要素ごとにもインスタンス化が必要
                    allFileData[i].file_path = getTmpData[0];
                    allFileData[i].file_name = getTmpData[1];
                    allFileData[i].lat = Double.parseDouble(getTmpData[2]);
                    allFileData[i].lng = Double.parseDouble(getTmpData[3]);
                    allFileData[i].tag1 = getTmpData[4];//なければnull
                    allFileData[i].tag2 = getTmpData[5];
                    allFileData[i].tag3 = getTmpData[6];
                    allFileData[i].tag4 = getTmpData[7];
                    allFileData[i].tag5 = getTmpData[8];
                    fileNum++;
                }else{
                    break;
                }
            }

            reader.close();
        }catch( IOException e ){
            e.printStackTrace();
        }

        // 取り出した座標の位置にマーカー追加
        for(int i = 0; i < fileNum; i++) {
            LatLng crPin = new LatLng((allFileData[i].lat), (allFileData[i].lng));
            MarkerOptions options = new MarkerOptions().position(crPin).title(allFileData[i].file_name);
            String path = allFileData[i].file_path + allFileData[i].file_name;
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(getBitmap(path));
            options.icon(icon);
            Marker marker = mMap.addMarker(options);
            marker.setTag(path);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Intent intent = new Intent(MapsActivity.this, PreviewActivity.class);
                    intent.putExtra("file_path", (String)marker.getTag());
                    startActivity(intent);
                    return true;
                }
            });
        }
        //カメラ初期位置設定　先頭の画像近くに
        if(fileNum>0) {
            CameraUpdate cUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(allFileData[0].lat, allFileData[0].lng), 9);
            mMap.moveCamera(cUpdate);
        }
    }

    private Bitmap getBitmap(String path) {
        BitmapFactory.Options imageOptions = new BitmapFactory.Options();
        imageOptions.inSampleSize = 32;
        return BitmapFactory.decodeFile(path, imageOptions);
    }

    private BitmapFactory.Options getBmpOptions(String path) {
        BitmapFactory.Options imageOptions = new BitmapFactory.Options();
        imageOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, imageOptions);
        Log.v("image", "Original Image Size: " + imageOptions.outWidth + " x " + imageOptions.outHeight);
        return imageOptions;
    }
}
