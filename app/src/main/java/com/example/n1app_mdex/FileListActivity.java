package com.example.n1app_mdex;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class FileListActivity extends ListActivity {
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelistview);

        ArrayList<String> fileArray = file_search(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileArray);
        setListAdapter(arrayAdapter);

        ListView lv =  (ListView)findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FileListActivity.this, PreviewActivity.class);
                intent.putExtra("file_path", ((TextView)view.findViewById(android.R.id.text1)).getText().toString());
                startActivity(intent);
            }
        });
    }

    public ArrayList<String> file_search(String targetFolderPath) {
        ArrayList<String> retArray = new ArrayList<String>();
        String b_extension = ".JPG";
        String s_extension = ".jpg";

        File dir = new File(targetFolderPath);
        File files[] = dir.listFiles();

        if(files != null) {
            Log.d("debug",String.valueOf(files.length));
        } else {
            Log.d("debug","null");
        }

        for(int i=0; i<files.length; i++){
            String file_name = files[i].getName();
            if(files[i].isDirectory()){  //ディレクトリなら再帰を行う
                //今回は行わずに()
                retArray.addAll(file_search(targetFolderPath + file_name+"/"));
            }else{
                if(file_name.endsWith(b_extension) || file_name.endsWith(s_extension)){
                    retArray.add(files[i].getAbsolutePath());
                }
            }
        }
        return retArray;
    }
}
