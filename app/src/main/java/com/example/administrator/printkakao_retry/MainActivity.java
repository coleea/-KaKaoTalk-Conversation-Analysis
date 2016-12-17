package com.example.administrator.printkakao_retry;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.hannanum.WorkflowFactory;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() ;
    private Button mBrowse_btn ;
    int REQUEST_READWRITE_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBrowse_btn = (Button) findViewById(R.id.browse_btn );

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READWRITE_STORAGE );

        Context mContext = getApplicationContext();
        AssetManager assetMngr = mContext.getAssets();
        try {

            /*
            HashMap<String, InputStream> hmap = new HashMap<>();
            hmap.put("ChartMorphAnalyzer", mContext.getResources().openRawResource(R.raw.chartmorphanalyzer));
            hmap.put("HmmPosTagger", mContext.getResources().openRawResource(R.raw.hmmpostagger));
            hmap.put("InformalSentenceFilter", mContext.getResources().openRawResource(R.raw.informalsentencefilter));
            hmap.put("NounExtractor", mContext.getResources().openRawResource(R.raw.nounextractor));
            hmap.put("SentenceSegmentor", mContext.getResources().openRawResource(R.raw.sentencesegmentor));
            hmap.put("UnknownMorphProcessor", mContext.getResources().openRawResource(R.raw.unknownmorphprocessor));
            hmap.put("SimpleMAResult09", mContext.getResources().openRawResource(R.raw.simplemaresult09));
            hmap.put("SimpleMAResult22", mContext.getResources().openRawResource(R.raw.simplemaresult22));
            hmap.put("SimplePOSResult09", mContext.getResources().openRawResource(R.raw.simpleposresult09));
            hmap.put("SimplePOSResult22", mContext.getResources().openRawResource(R.raw.simpleposresult22));
            hmap.put("tag_set", mContext.getResources().openRawResource(R.raw.tag_set));
            hmap.put("connections", mContext.getResources().openRawResource(R.raw.connections));
            hmap.put("connections_not", mContext.getResources().openRawResource(R.raw.connections_not));
            hmap.put("dic_analyzed", mContext.getResources().openRawResource(R.raw.dic_analyzed));
            hmap.put("dic_system", mContext.getResources().openRawResource(R.raw.dic_system));
            hmap.put("dic_user", mContext.getResources().openRawResource(R.raw.dic_user));
            hmap.put("PTTpos", mContext.getResources().getAssets().open("pos/PTT.pos"));
            hmap.put("PTTwp", mContext.getResources().getAssets().open("pos/PTT.wp"));
            hmap.put("PWTpos", mContext.getResources().getAssets().open("pos/PWT.pos"));

            Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_HMM_POS_TAGGER, hmap);
            workflow.activateWorkflow(true, hmap);

            String document = "프로젝트 전체 회의.\n"
                    + "회의 일정은 다음과 같습니다.\n";

            workflow.analyze(document);

            String HMM_result ;
            String morpheme ;
            String pos ;
            HMM_result = workflow.getResultOfDocument();
            String [] s_splitByNewLine = HMM_result.split("\n\n");
            ArrayList<String> list_morpheme = new ArrayList<String>() ;
            ArrayList<String> list_pos = new ArrayList<String>() ;

            for(String s : s_splitByNewLine) {
                int slashIdx = s.indexOf("/");
                if(slashIdx != -1){
                    morpheme =  s.substring( 0 , slashIdx);
                    list_morpheme.add(morpheme);
                    pos = s.substring( slashIdx + 1 );
                    list_pos.add(pos);
                }
            }
            String ss = list_pos.get(0);
            System.out.println(ss);
             */
            } catch (Exception e) {
            e.printStackTrace();
        }

        mBrowse_btn.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v){
                                               Toast.makeText(MainActivity.this, sdPath , Toast.LENGTH_SHORT).show();
                                               Intent i = new Intent(MainActivity.this, PathFinder.class);
                                               startActivity(i);
                                           }
                                       }
        );
    }
}