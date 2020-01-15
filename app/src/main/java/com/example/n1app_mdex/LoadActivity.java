package com.example.n1app_mdex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.graphics.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

import static java.lang.System.out;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);

        String DCIM_path = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";;

        fileLoad get = new fileLoad();
        get.make_gpstxt();//初期化

        get.file_search(DCIM_path);

        TextView textView = new TextView(this);
        textView.setText("GPS座標を読み込みました");
        textView.setTextSize(24);
        textView.setTextColor(Color.rgb(0x0, 0x0, 0x0));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 60, 0, 0);

        ll.addView(textView);

        Button ok_btn = new Button(this);
        ok_btn.setText("OK");
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        ll.addView(ok_btn);

    }

    public class fileLoad {

        //Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
        //targetFolderPathは、多くの場合これ

        public void file_search(String targetFolderPath) {
            String b_extension = ".JPG";
            String s_extension = ".jpg";

            File dir = new File(targetFolderPath);
            File files[] = dir.listFiles();

            if(files != null) {
                Log.d("debug",String.valueOf(files.length));
            } else {
                Log.d("debug","null");
            }

            Log.d("debug",targetFolderPath);

            for(int i=0; i<files.length; i++){
                String file_name = files[i].getName();
                if(files[i].isDirectory()){  //ディレクトリなら再帰を行う
                    //今回は行わずに()
                    file_search(targetFolderPath+file_name+"/");
                }else{
                    if(file_name.endsWith(b_extension) || file_name.endsWith(s_extension)){  	//file_nameの最後尾(拡張子)が指定のものならば出力
                        exifLoad getExif = new exifLoad();
                        getExif.Exif_load(targetFolderPath,file_name);
                    }
                }
            }
        }

        //押下時の既存内容のリセット
        public void make_gpstxt(){

            String str = "";

            try{
                FileOutputStream out = openFileOutput( "gps.txt", MODE_PRIVATE );
                out.write( str.getBytes() );
            }catch( IOException e ){
                e.printStackTrace();
            }
        }

        public void export_gpstxt(String img_path, String img_name, float lat, float lng){

            String str = img_path+ "," +img_name+ "," +lat+","+lng+",tag1,tag2,tag3,tag4,tag5\n";
            try{
                FileOutputStream out = openFileOutput( "gps.txt", MODE_PRIVATE|Context.MODE_APPEND);
                out.write( str.getBytes() );
            }catch( IOException e ){
                e.printStackTrace();
            }
        }

    }

    class exifLoad {
        String imgfile = "sample1.jpg";

        public void Exif_load(String img_path,String img_name) {
            try{
                if( img_path != null ){
                    //パスは現在windows向けの書き方になっている
                    String fullpath = img_path +"/"+img_name;
                    imgfile = fullpath;
                }
                out.println( "imgfile=" + imgfile );

                byte[] buff = new byte[1024*1024];
                FileInputStream fis = new FileInputStream( imgfile );
                int size = fis.read( buff, 0, buff.length );
                fis.close();
                if( size > -1 ){
                    byte[] buf = new byte[size];
                    int[] bytedata = new int[size];
                    for( int i = 0; i < size; i ++ ){
                        buf[i] = buff[i];
                        bytedata[i] = ( 0xff & buf[i] );
                    }

                    boolean b = true;
                    int prev = 0x00;
                    for( int i = 2; i < size && b; i ++ ){
                        if( prev == 0xff ){
                            int sz = 256 * bytedata[i+1] + bytedata[i+2];
                            if( bytedata[i] == 0xe1 ){
                                //. Exif 情報
                                boolean isMot = true;
                                int offset = 0;
                                i ++;   //. APP1マーカ
                                i += 2; //. APP1サイズ情報
                                i += 6; //. Exif識別コード

                                //. TIFF Header
                                if( bytedata[i] == 0x4d ){
                                    //. モトローラ方式
                                    isMot = true;
                                }else if( bytedata[i] == 0x49 ){
                                    //. インテル方式
                                    isMot = false;
                                }
                                out.println( "i = " + i + ", bytedata[" + i + "] = " + bytedata[i] + ", isMot = " + isMot );

                                //. IDF0
                                offset = isMot ?
                                        16777216 * bytedata[i+4] + 65536 * bytedata[i+5] + 256 * bytedata[i+6] + bytedata[i+7] :
                                        16777216 * bytedata[i+7] + 65536 * bytedata[i+6] + 256 * bytedata[i+5] + bytedata[i+4];

                                //. IFD
                                int num_dir = isMot ?
                                        256 * bytedata[i+offset] + bytedata[i+offset+1] :
                                        256 * bytedata[i+offset+1] + bytedata[i+offset];
                                offset += 2;
                                for( int j = 0; j < num_dir && b; j ++ ){
                                    int tag = isMot ?
                                            256 * bytedata[i+offset] + bytedata[i+offset+1] :
                                            256 * bytedata[i+offset+1] + bytedata[i+offset];

    //								int type = isMot ?
    //										256 * bytedata[i+offset+2] + bytedata[i+offset+3] :
    //										256 * bytedata[i+offset+3] + bytedata[i+offset+2];
    //								int num_data = isMot ?
    //										16777216 * bytedata[i+offset+4] + 65536 * bytedata[i+offset+5] + 256 * bytedata[i+offset+6] + bytedata[i+offset+7] :
    //										16777216 * bytedata[i+offset+7] + 65536 * bytedata[i+offset+6] + 256 * bytedata[i+offset+5] + bytedata[i+offset+4];
    //								int data = isMot ?
    //										16777216 * bytedata[i+offset+8] + 65536 * bytedata[i+offset+9] + 256 * bytedata[i+offset+10] + bytedata[i+offset+11] :
    //										16777216 * bytedata[i+offset+11] + 65536 * bytedata[i+offset+10] + 256 * bytedata[i+offset+9] + bytedata[i+offset+8];

                                    if( tag == 34853 ){
                                        out.println( "j = " + j + ", tag = " + tag );
                                        //. GPSInfoIFDPointer
                                        float lat = 0.0f, lng = 0.0f;
                                        int latn = 1, lngn = 1;
                                        int data = isMot ?
                                                16777216 * bytedata[i+offset+8] + 65536 * bytedata[i+offset+9] + 256 * bytedata[i+offset+10] + bytedata[i+offset+11] :
                                                16777216 * bytedata[i+offset+11] + 65536 * bytedata[i+offset+10] + 256 * bytedata[i+offset+9] + bytedata[i+offset+8];
                                        offset = data;
                                        b = false;

                                        //. GPS IDF
                                        int gps_num_dir = isMot ?
                                                256 * bytedata[i+offset] + bytedata[i+offset+1] :
                                                256 * bytedata[i+offset+1] + bytedata[i+offset];
                                        out.println( "gps_num_dir = " + gps_num_dir );
                                        offset += 2;
                                        for( int k = 0; k < gps_num_dir; k ++ ){
                                            int gps_tag = isMot ?
                                                    256 * bytedata[i+offset] + bytedata[i+offset+1] :
                                                    256 * bytedata[i+offset+1] + bytedata[i+offset];

                                            int gps_type = isMot ?
                                                    256 * bytedata[i+offset+2] + bytedata[i+offset+3] :
                                                    256 * bytedata[i+offset+3] + bytedata[i+offset+2];
                                            int gps_num_data = isMot ?
                                                    16777216 * bytedata[i+offset+4] + 65536 * bytedata[i+offset+5] + 256 * bytedata[i+offset+6] + bytedata[i+offset+7] :
                                                    16777216 * bytedata[i+offset+7] + 65536 * bytedata[i+offset+6] + 256 * bytedata[i+offset+5] + bytedata[i+offset+4];
                                            int gps_data = ( gps_type == 5 && isMot ) ? //isMot ? (K.Kimura 2010/Sep/26)
                                                    16777216 * bytedata[i+offset+8] + 65536 * bytedata[i+offset+9] + 256 * bytedata[i+offset+10] + bytedata[i+offset+11] :
                                                    16777216 * bytedata[i+offset+11] + 65536 * bytedata[i+offset+10] + 256 * bytedata[i+offset+9] + bytedata[i+offset+8];
                                            out.println( " k = " + k + ", gps_tag = " + gps_tag + ", gps_type = " + gps_type + ", gps_num_data = " + gps_num_data + ", gps_data = " + gps_data );

                                            String gps_data_s = "" + gps_data;
                                            if( gps_type == 2 ){
                                                gps_data_s = "" + ( char )gps_data;
                                            }else if( gps_type == 5 ){
                                                double f = 0.0;
                                                for( int m = 0; m < gps_num_data; m ++ ){
                                                    int f1 = isMot ?
                                                            16777216 * bytedata[i+8*m+gps_data] + 65536 * bytedata[i+8*m+gps_data+1] + 256 * bytedata[i+8*m+gps_data+2] + bytedata[i+8*m+gps_data+3] :
                                                            16777216 * bytedata[i+8*m+gps_data+3] + 65536 * bytedata[i+8*m+gps_data+2] + 256 * bytedata[i+8*m+gps_data+1] + bytedata[i+8*m+gps_data];
                                                    int f2 = isMot ?
                                                            16777216 * bytedata[i+8*m+gps_data+4] + 65536 * bytedata[i+8*m+gps_data+5] + 256 * bytedata[i+8*m+gps_data+6] + bytedata[i+8*m+gps_data+7] :
                                                            16777216 * bytedata[i+8*m+gps_data+7] + 65536 * bytedata[i+8*m+gps_data+6] + 256 * bytedata[i+8*m+gps_data+5] + bytedata[i+8*m+gps_data+4];
                                                    double d = ( double )f1 / ( double )f2;

                                                    if( m == 0 ){
                                                        f += d;
                                                    }else if( m == 1 ){
                                                        f += ( d / 60.0 );
                                                    }else if( m == 2 ){
                                                        f += ( d / 3600.0 );
                                                    }
                                                }
                                                gps_data_s = "" + f;
                                            }
                                            out.println( "  gps_data_s = " + gps_data_s );

                                            if( gps_tag == 1 ){
                                                //. 緯度の南北
                                                if( gps_data_s.equals( "N" ) ){
                                                    latn = 1;
                                                }else if( gps_data_s.equals( "S" ) ){
                                                    latn = -1;
                                                }
                                            }else if( gps_tag == 2 ){
                                                //. 緯度（度、分、秒）
                                                try{
                                                    lat = Float.parseFloat( gps_data_s );
                                                }catch( Exception e ){
                                                }
                                            }else if( gps_tag == 3 ){
                                                //. 緯度の南北
                                                if( gps_data_s.equals( "E" ) ){
                                                    lngn = 1;
                                                }else if( gps_data_s.equals( "W" ) ){
                                                    lngn = -1;
                                                }
                                                //. 経度の東西
                                            }else if( gps_tag == 4 ){
                                                //. 経度（度、分、秒）
                                                try{
                                                    lng = Float.parseFloat( gps_data_s );
                                                }catch( Exception e ){
                                                }
                                            }

                                            offset += 12;
                                        }

                                        //. 最終計算
                                        lat = latn * lat;
                                        lng = lngn * lng;
                                        out.println( "lat=" + lat + ",lng=" + lng );
                                        //このlatとlngをよそに渡す
                                        fileLoad huhu = new fileLoad();
                                        huhu.export_gpstxt(img_path,img_name, lat, lng);

                                    }

                                    offset += 12;
                                }

                                i += offset;
                            }else{
                                //. sz の分だけスキップ
                                i += sz;
                            }

                            prev = 0x00;
                        }else{
                            prev = bytedata[i];
                        }
                    }
                }
            }catch( Throwable t ){
                t.printStackTrace();
            }
        }
    }
}
