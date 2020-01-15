package com.example.n1app_mdex;

import android.Manifest;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MenuActivity extends AppCompatActivity{
    EditText et;

    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_menu);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);


        Button btn = new Button(this);
        btn.setText("ファイル読込");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RuntimePermissionUtils.hasSelfPermissions(MenuActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 権限がある場合は、そのまま通常処理を行う
                    Intent intent = new Intent(MenuActivity.this, LoadActivity.class );
                    startActivity(intent);
                } else {
                    // 権限がない場合は、パーミッション確認アラートを表示する
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        });
        ll.addView(btn);

        Button btn1 = new Button(this);
        btn1.setText("ファイル一覧");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RuntimePermissionUtils.hasSelfPermissions(MenuActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 権限がある場合は、そのまま通常処理を行う
                    Intent intent = new Intent(MenuActivity.this, FileListActivity.class);
                    startActivity(intent);
                } else {
                    // 権限がない場合は、パーミッション確認アラートを表示する
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        });
        ll.addView(btn1);

        Button btn2 = new Button(this);
        btn2.setText("タグ付与（準備中）");
        //btn2.setOnClickListener( new  );
        ll.addView(btn2);
        Button mapViewBtn = new Button(this);
        mapViewBtn.setText("地図を表示");
        mapViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MapsActivity.class );
                startActivity(intent);
            }
        });
        ll.addView(mapViewBtn);


        ////////////////////////////////////
        //以下開発向け要素
        et = new EditText(this);

        Button btn3 = new Button(this);
        btn3.setText("ファイル内容確認（開発向）");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileInputStream in = openFileInput("gps.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String tmp;
                    et.setText("");
                    while( (tmp = reader.readLine()) != null ){
                        et.append(tmp + "\n");
                    }
                    reader.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        ll.addView(btn3);

        Button btn4 = new Button(this);
        btn4.setText("ファイル内容消去（開発向）");
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                try{
                    FileOutputStream out = openFileOutput( "gps.txt", MODE_PRIVATE );
                    out.write( str.getBytes() );
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        ll.addView(btn4);

        ll.addView(et);



    }


}
