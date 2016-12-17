package com.example.administrator.printkakao_retry;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.hannanum.WorkflowFactory;

public class ResultShowActivity extends AppCompatActivity {

    int WordCntLowerBound = 3;
    int rank = 1;
    Integer total_char_cnt = 0 ;
    Integer word_num;
    Integer total_user_char_cnt;

    String skip_pos_regex = "(^MAJ$|^MM$|^NP$|^EC$|^NNB$|^VCP$|^JKB$)";
    String meaningless_word_rank_regex = "(^ㅋ+$|^ㅎ+$|^\\?+$|^;+$|^\\!+$|^오늘$|^어제$|^내일$|^이모티콘$|^사실$|^그렇게$|^사진$|^나$|^난$|^나는$|^너$|^넌$|^너는$|^사람$|^는데$|^나서$)" ;

    HashSet<String> verb_list = new HashSet<String>();
    HashMap<String, Integer> hmap_wordCnt;
    Kakao_txt_analysis kakao_txt_analysis = new Kakao_txt_analysis();
    user_info u_info ;
    Iterator it_hmap;
    TextView textView_analysis_result ;

    public ArrayList<user_info> userInfo_list;
    public HashMap<String, Integer> common_freq_words;
    public Map<String, Integer> sortedMap;
    public Integer total_hours_cnt;
    public Integer[] time_freq_cnt;
    public ArrayList<String> user_name_list;

    Workflow workflow;

    InputStream is_yokList;
    InputStream is_korean_verb ;
    InputStream is_word1 ;
    InputStream is_word2 ;
    InputStream is_word3 ;
    InputStream is_word4 ;
    InputStream is_word5 ;
    InputStream is_word6 ;
    InputStream is_word7 ;

    HashMap<String, InputStream> hmap_resources;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_result);
        textView_analysis_result = (TextView)findViewById(R.id.txtView_writeResult);

//        String fileName =(String) extras.get("fileName");
//        System.out.println(fileName);
//        File file2 = new File(fileName);
        try {
            setFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        runAnalysis();
        handler.sendEmptyMessage(0);
    }

    void setFile() throws Exception {

        hmap_resources = new HashMap<>();
        hmap_resources.put("ChartMorphAnalyzer",  getResources().openRawResource(R.raw.chartmorphanalyzer));
        hmap_resources.put("HmmPosTagger",  getResources().openRawResource(R.raw.hmmpostagger));
        hmap_resources.put("InformalSentenceFilter",  getResources().openRawResource(R.raw.informalsentencefilter));
        hmap_resources.put("NounExtractor",  getResources().openRawResource(R.raw.nounextractor));
        hmap_resources.put("SentenceSegmentor",  getResources().openRawResource(R.raw.sentencesegmentor));
        hmap_resources.put("UnknownMorphProcessor",  getResources().openRawResource(R.raw.unknownmorphprocessor));
        hmap_resources.put("SimpleMAResult09",  getResources().openRawResource(R.raw.simplemaresult09));
        hmap_resources.put("SimpleMAResult22",  getResources().openRawResource(R.raw.simplemaresult22));
        hmap_resources.put("SimplePOSResult09",  getResources().openRawResource(R.raw.simpleposresult09));
        hmap_resources.put("SimplePOSResult22",  getResources().openRawResource(R.raw.simpleposresult22));
        hmap_resources.put("tag_set",  getResources().openRawResource(R.raw.tag_set));
        hmap_resources.put("connections",  getResources().openRawResource(R.raw.connections));
        hmap_resources.put("connections_not",  getResources().openRawResource(R.raw.connections_not));
        hmap_resources.put("dic_analyzed",  getResources().openRawResource(R.raw.dic_analyzed));
        hmap_resources.put("dic_system",  getResources().openRawResource(R.raw.dic_system));
        hmap_resources.put("dic_user",  getResources().openRawResource(R.raw.dic_user));
        hmap_resources.put("PTTpos",  getResources().getAssets().open("pos/PTT.pos"));
        hmap_resources.put("PTTwp",  getResources().getAssets().open("pos/PTT.wp"));
        hmap_resources.put("PWTpos",  getResources().getAssets().open("pos/PWT.pos"));

        workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_HMM_POS_TAGGER, hmap_resources);
        workflow.activateWorkflow(true, hmap_resources);

        is_yokList = getResources().openRawResource(R.raw.yok);
        is_korean_verb = getResources().openRawResource(R.raw.korean_verb);
        is_word1 = getResources().openRawResource(R.raw.word1);
        is_word2 = getResources().openRawResource(R.raw.word2);
        is_word3 = getResources().openRawResource(R.raw.word3);
        is_word4 = getResources().openRawResource(R.raw.word4);
        is_word5 = getResources().openRawResource(R.raw.word5);
        is_word6 = getResources().openRawResource(R.raw.word6);
        is_word7 = getResources().openRawResource(R.raw.word7);
    }

    void runAnalysis(){
        Bundle extras = getIntent().getExtras();
        File file = (File)extras.get("file");

        kakao_txt_analysis.startAnalysis(file, is_yokList,is_korean_verb,is_word1,is_word2,is_word3,is_word4,is_word5,is_word6,is_word7 );
        common_freq_words =  kakao_txt_analysis.getCommon_freq_words();
        sortedMap =  kakao_txt_analysis.getSortedMap();
        time_freq_cnt =  kakao_txt_analysis.getTime_freq_cnt();
        total_hours_cnt =  kakao_txt_analysis.getTotal_hours_cnt();
        user_name_list =  kakao_txt_analysis.getUser_name_list();
        userInfo_list =  kakao_txt_analysis.getUserInfo_list();
    }

    int write_wordInfo_to_file(String u_word, String type, int rank) throws Exception {

        String HMM_result ;
        String morpheme ;
        String pos ;
        String eomi;

        int verb_len;
        int u_word_len;
        int length_diff ;

        Boolean IsfindVerb;

        if(type.equals("total count")){
            word_num = common_freq_words.get(u_word);
        }
        else if(type.equals("user")){
            word_num = hmap_wordCnt.get(u_word);
        }

        if (word_num >= WordCntLowerBound) {

            workflow.analyze(u_word);
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
            pos = list_pos.get(0);
            eomi = list_morpheme.get(0);
            System.out.println("POS : " + pos );

            if(! pos.matches(skip_pos_regex)){
                if(! u_word.matches(meaningless_word_rank_regex)){

                    if(pos.equals("VV") || pos.equals("VA")){
                        if(!eomi.equals("하")){
                            textView_analysis_result.append(rank++ +"위 : ");
                            textView_analysis_result.append(u_word + "(" + eomi + "다)|      " + word_num  + "회 \n");
                        }
                    }
                    else if(pos.equals("VCN") || pos.equals("XR")){
                        textView_analysis_result.append(rank++ +"위 : ");
                        textView_analysis_result.append(u_word + "(" + eomi + "하다)|      " + word_num  + "회 \n");
                    }
                    else{
                        IsfindVerb =false;

                        for(String verb_ :verb_list){
                            u_word_len =  u_word.length();
                            verb_len = verb_.length();

                            length_diff = verb_len - u_word_len ;
                            if(length_diff > 0 ){
                                String verb_extract = verb_.substring(0, length_diff);
                                if( u_word.equals(verb_extract) ) {
                                    textView_analysis_result.append(rank++ +"위 : ");
                                    textView_analysis_result.append(u_word + "(" + verb_ + ")|      " + word_num  + "회 \n");
                                    IsfindVerb = true ;
                                    break;
                                }
                            }
                        }
                        if(IsfindVerb == false){
                            textView_analysis_result.append(rank++ +"위 : ");
                            textView_analysis_result.append(u_word + " |      " + word_num + "회 \n");
                        }
                    }
                }
                else{
                    System.out.println("유해단어 발견 : " + u_word + "\n");
                }
            }
            else{
                System.out.println("유해단어 발견 : " + u_word + "\n");
            }
        }
        return rank;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String proportion_str;
            String u_word;
            String yok;
            String hour_ratio_str;

            int i = 0 ;
            int yok_cnt ;
            int question_cnt ;
            int alarm_cnt ;
            int sweat_cnt ;
            int dotdot_cnt ;
            int kk_cnt ;
            int hh_cnt ;
            int suntok_cnt ;
            int max_total_word_cnt = 30 ;
            double yok_ratio ;
            Double hour_ratio ;
            Double proportion;

            HashMap<String,Integer> yok_stat ;
            Map<String,Integer> yok_stat_map ;

            Iterator it_yok;
            Iterator it_total_word_cnt ;

            Iterator it_name ;
            Iterator it_userinfo ;

            it_name =  user_name_list.iterator();
            it_userinfo =  userInfo_list.iterator();

            for(Integer t : time_freq_cnt){
                hour_ratio = (t.doubleValue() /  total_hours_cnt) * 100 ;
                hour_ratio_str = String.format("%.2f", hour_ratio);
                textView_analysis_result.append(i + "시 : " + hour_ratio_str + "% \n");
                i++;
            }
            textView_analysis_result.append("\n\n");

            common_freq_words = (HashMap<String, Integer>)( Kakao_txt_analysis.sortByValue( common_freq_words ) );
            it_total_word_cnt =  common_freq_words.keySet().iterator();

            textView_analysis_result.append("__채팅방에서 가장 많이 사용된 어휘 순위__\n");
            rank = 1;

            while(it_total_word_cnt.hasNext()){

                u_word = (String)it_total_word_cnt.next();
                try {
                    rank = write_wordInfo_to_file(u_word, "total count", rank);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                max_total_word_cnt--;
                if(max_total_word_cnt == 0){
                    break;
                }
            }

            textView_analysis_result.append("\n\n");

            while (it_name.hasNext()) {
                rank = 1;
                u_info = (user_info) it_userinfo.next();
                textView_analysis_result.append("Username : " + (String) it_name.next() + "\n");

                hmap_wordCnt = (HashMap<String, Integer>)  u_info.getWord_cnt_list() ;
                sortedMap =  Kakao_txt_analysis.sortByValue(hmap_wordCnt);
                total_user_char_cnt = (Integer) u_info.getTotal_char_cnt() ;

                proportion = ( total_user_char_cnt.doubleValue() /  total_char_cnt.doubleValue())*100 ;
                proportion_str = String.format("%.2f", proportion);
                textView_analysis_result.append( "문자 점유율 : " + proportion_str  +"%" + "\n"  );

                yok_cnt =  u_info.getYok_cnt();
                question_cnt =  u_info.getQuestion_cnt();
                alarm_cnt =  u_info.getAlarm_cnt();
                sweat_cnt =  u_info.getSweat_cnt();
                dotdot_cnt =  u_info.getDotdot_cnt();
                kk_cnt =  u_info.getKk_cnt();
                hh_cnt =  u_info.getHh_cnt();
                suntok_cnt =  u_info.getSuntok_times();
                textView_analysis_result.append( "?(의문문, 질문) 횟수 : " + question_cnt + "\n"  );
                textView_analysis_result.append( "!(느낌표, 놀람, 업템포) 횟수 : " + alarm_cnt + "\n"  );
                textView_analysis_result.append( ";(땀, 당황) 횟수 : " + sweat_cnt + "\n"  );
                textView_analysis_result.append( "..(말흘림) 횟수 : " + dotdot_cnt + "\n"  );
                textView_analysis_result.append( "ㅋㅋ, 크크(웃음 1유형) 횟수 : " + kk_cnt + "\n"  );
                textView_analysis_result.append( "ㅎㅎ, 흐흐(웃음 2유형) 횟수 : " + hh_cnt + "\n\n"  );
                textView_analysis_result.append( "선톡 횟수 : " + suntok_cnt + "\n\n\n"  );

                if(yok_cnt != 0){
                    textView_analysis_result.append( "욕설 횟수 : " + yok_cnt + "회 \n"  );

                    yok_ratio =  u_info.getLine_cnt() / yok_cnt;
                    textView_analysis_result.append( yok_ratio  + "번 중 1번은 욕설 \n"  );

                    yok_stat  =  u_info.getYok_stat();
                    yok_stat_map =  Kakao_txt_analysis.sortByValue(  yok_stat);

                    it_yok = yok_stat_map.keySet().iterator();
                    while (it_yok .hasNext()){
                        yok = (String)it_yok.next();
                        yok_cnt = yok_stat_map.get(yok);
                        textView_analysis_result.append( yok + "|     " + yok_cnt + "회 \n"  );
                    }
                    textView_analysis_result.append("\n\n");
                }

                textView_analysis_result.append( "___자주 사용한 어휘 순위___" + "\n\n");
                it_hmap =  sortedMap.keySet().iterator();
                while ( it_hmap.hasNext()) {
                    u_word = (String)  it_hmap.next();
                    try {
                        rank = write_wordInfo_to_file(u_word, "user", rank);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                textView_analysis_result.append( "\n\n______________________________\n");
            }
        }
    } ;

    Thread readyThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // 이 부분에 준비 작업

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String proportion_str;
                    String u_word;
                    String yok;
                    String hour_ratio_str;

                    int i = 0 ;
                    int yok_cnt ;
                    int question_cnt ;
                    int alarm_cnt ;
                    int sweat_cnt ;
                    int dotdot_cnt ;
                    int kk_cnt ;
                    int hh_cnt ;
                    int suntok_cnt ;
                    int max_total_word_cnt = 30 ;
                    double yok_ratio ;
                    Double hour_ratio ;
                    Double proportion;

                    HashMap<String,Integer> yok_stat ;
                    Map<String,Integer> yok_stat_map ;

                    Iterator it_yok;
                    Iterator it_total_word_cnt ;

                    Iterator it_name ;
                    Iterator it_userinfo ;

                    it_name =  user_name_list.iterator();
                    it_userinfo =  userInfo_list.iterator();

                    for(Integer t : time_freq_cnt){
                        hour_ratio = (t.doubleValue() /  total_hours_cnt) * 100 ;
                        hour_ratio_str = String.format("%.2f", hour_ratio);
                        textView_analysis_result.append(i + "시 : " + hour_ratio_str + "% \n");
                        i++;
                    }
                    textView_analysis_result.append("\n\n");

                    common_freq_words = (HashMap<String, Integer>)( Kakao_txt_analysis.sortByValue( common_freq_words ) );
                    it_total_word_cnt =  common_freq_words.keySet().iterator();

                    textView_analysis_result.append("__채팅방에서 가장 많이 사용된 어휘 순위__\n");
                    rank = 1;

                    while(it_total_word_cnt.hasNext()){

                        u_word = (String)it_total_word_cnt.next();
                        try {
                            rank = write_wordInfo_to_file(u_word, "total count", rank);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        max_total_word_cnt--;
                        if(max_total_word_cnt == 0){
                            break;
                        }
                    }

                    textView_analysis_result.append("\n\n");

                    while (it_name.hasNext()) {
                        rank = 1;
                        u_info = (user_info) it_userinfo.next();
                        textView_analysis_result.append("Username : " + (String) it_name.next() + "\n");

                        hmap_wordCnt = (HashMap<String, Integer>)  u_info.getWord_cnt_list() ;
                        sortedMap =  Kakao_txt_analysis.sortByValue(hmap_wordCnt);
                        total_user_char_cnt = (Integer) u_info.getTotal_char_cnt() ;

                        proportion = ( total_user_char_cnt.doubleValue() /  total_char_cnt.doubleValue())*100 ;
                        proportion_str = String.format("%.2f", proportion);
                        textView_analysis_result.append( "문자 점유율 : " + proportion_str  +"%" + "\n"  );

                        yok_cnt =  u_info.getYok_cnt();
                        question_cnt =  u_info.getQuestion_cnt();
                        alarm_cnt =  u_info.getAlarm_cnt();
                        sweat_cnt =  u_info.getSweat_cnt();
                        dotdot_cnt =  u_info.getDotdot_cnt();
                        kk_cnt =  u_info.getKk_cnt();
                        hh_cnt =  u_info.getHh_cnt();
                        suntok_cnt =  u_info.getSuntok_times();
                        textView_analysis_result.append( "?(의문문, 질문) 횟수 : " + question_cnt + "\n"  );
                        textView_analysis_result.append( "!(느낌표, 놀람, 업템포) 횟수 : " + alarm_cnt + "\n"  );
                        textView_analysis_result.append( ";(땀, 당황) 횟수 : " + sweat_cnt + "\n"  );
                        textView_analysis_result.append( "..(말흘림) 횟수 : " + dotdot_cnt + "\n"  );
                        textView_analysis_result.append( "ㅋㅋ, 크크(웃음 1유형) 횟수 : " + kk_cnt + "\n"  );
                        textView_analysis_result.append( "ㅎㅎ, 흐흐(웃음 2유형) 횟수 : " + hh_cnt + "\n\n"  );
                        textView_analysis_result.append( "선톡 횟수 : " + suntok_cnt + "\n\n\n"  );

                        if(yok_cnt != 0){
                            textView_analysis_result.append( "욕설 횟수 : " + yok_cnt + "회 \n"  );

                            yok_ratio =  u_info.getLine_cnt() / yok_cnt;
                            textView_analysis_result.append( yok_ratio  + "번 중 1번은 욕설 \n"  );

                            yok_stat  =  u_info.getYok_stat();
                            yok_stat_map =  Kakao_txt_analysis.sortByValue(  yok_stat);

                            it_yok = yok_stat_map.keySet().iterator();
                            while (it_yok .hasNext()){
                                yok = (String)it_yok.next();
                                yok_cnt = yok_stat_map.get(yok);
                                textView_analysis_result.append( yok + "|     " + yok_cnt + "회 \n"  );
                            }
                            textView_analysis_result.append("\n\n");
                        }

                        textView_analysis_result.append( "___자주 사용한 어휘 순위___" + "\n\n");
                        it_hmap =  sortedMap.keySet().iterator();
                        while ( it_hmap.hasNext()) {
                            u_word = (String)  it_hmap.next();
                            try {
                                rank = write_wordInfo_to_file(u_word, "user", rank);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        textView_analysis_result.append( "\n\n______________________________\n");
                    }
                }
            });
        }
    });
}
