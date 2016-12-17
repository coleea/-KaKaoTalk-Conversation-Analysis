package com.example.administrator.printkakao_retry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.path;
import static android.media.CamcorderProfile.get;

public class PathFinder extends AppCompatActivity {

    private String mFileName;
    private String mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();

    private List<String> lItem = null;
    private List<String> lPath = null;

    private ListView lvFileControl;
    private Context mContext = this;
    private TextView mPath;
    private TextView kakao_log;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);
        mPath = (TextView) findViewById(R.id.tvPath);
        lvFileControl = (ListView)findViewById(R.id.lvFileControl);
        getDir(mRoot);

        lvFileControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            
            // 4 params
            public void onItemClick(AdapterView<?> parent, View v, int param_position, long id) {
                File file = new File(lPath.get(param_position));

                if (file.isDirectory()) {
                    if (file.canRead())
                        getDir(lPath.get(param_position));
                    else {
                        Toast.makeText(mContext, "No files in this folder.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mFileName = file.getName();
                    Log.i("Test", "ext:" + mFileName.substring(mFileName.lastIndexOf('.') + 1, mFileName.length()));

                    Intent i = new Intent(PathFinder.this, ResultShowActivity.class);
                    String fileName = file.toString();
                    System.out.println(fileName);


                    i.putExtra("file", file);
                    i.putExtra("fileDir", lPath.toString());
//                    i.putExtra("fileName", get(param_position))
                    i.putExtra("fileName", fileName);
                    startActivity(i);
                }
            }
        });
    }

    private void getDir(String dirPath) {
        mPath.setText("Location: " + dirPath);

        lItem = new ArrayList<String>();
        lPath = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(mRoot)) {
            //item.add(root); //to root.
            //path.add(root);
            lItem.add("../"); //to parent folder
            lPath.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            lPath.add(file.getAbsolutePath());

            if (file.isDirectory())
                lItem.add(file.getName() + "/");
            else
                lItem.add(file.getName());
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lItem);
        lvFileControl.setAdapter(fileList);
    }
}