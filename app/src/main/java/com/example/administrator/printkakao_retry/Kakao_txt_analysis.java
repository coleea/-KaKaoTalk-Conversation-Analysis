package com.example.administrator.printkakao_retry;

import kr.co.shineware.util.common.model.Pair;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.joda.time.DateTimeUtils;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Kakao_txt_analysis extends DateTimeUtils{

    private int ngram_num = 3 ;
    private int max_word_len = 3;
    private int min_word_len = 2;
    int user_idx;
    int total_line_cnt = 0 ;
    Integer total_hours_cnt = 0 ;
    Integer total_char_cnt = 0 ;
    Integer [] time_freq_cnt = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};

    String [] yok_list_beforeTrim;
    private String _1line;
    String meaningless_line_regex = "(^http://.+/$ | \\p{Alnum}+.(jpg|jpeg|gif|png|bmp|pptx|txt)$ )";
    private final String talk_regex = "\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}:\\d{1,2}, .* : .*";
    String intro_regex = ".+ 카카오톡 대화$";
    String saved_date_regex = "^저장한 날짜 : \\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}:\\d{1,2}$";
    String day_notify_regex = "^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}:\\d{1,2}$";
    String invite_regex = "^\\d{4}년 \\d{1,2}월 \\d{1,2}일 (오전|오후) \\d{1,2}:\\d{1,2}, .+님이 .+님을 초대했습니다.$";
    String beforeUser ;
    String beforeTimeFormat = null ;

    ArrayList<user_info> userInfo_list = new ArrayList<user_info>();
    ArrayList<String> user_name_list = new ArrayList<String>();
    ArrayList<String> yok_list = new ArrayList<String>();
    HashMap<String, Integer> common_freq_words = new HashMap<String, Integer>();
    HashMap<String, Integer> h_map_ref;
    HashSet<String> verb_list = new HashSet<String>();
    Map<String,Integer> sortedMap ;
    BufferedReader korean_verb_list_br;
    BufferedReader yok_br ;
    user_info u_info ;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
    Date nowDate;
    Date prevDate;
    Interval interval;
    Period period ;
    ArrayList<Trie> trie = new ArrayList<Trie>();
    Collection<Emit> emits;

    ////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<user_info> getUserInfo_list() {
        return userInfo_list;
    }
    public HashMap<String, Integer> getCommon_freq_words() {
        return common_freq_words;
    }
    public Map<String, Integer> getSortedMap() {
        return sortedMap;
    }
    public Integer getTotal_hours_cnt() {
        return total_hours_cnt;
    }
    public Integer[] getTime_freq_cnt() {
        return time_freq_cnt;
    }
    public ArrayList<String> getUser_name_list() {
        return user_name_list;
    }

////////////////////////////////////////////////////////////////////////////////////

    void startAnalysis(File txtFile,InputStream is_yokList ,InputStream is_koreanVerb, InputStream is_word1,InputStream is_word2,InputStream is_word3,InputStream is_word4,InputStream is_word5,InputStream is_word6,InputStream is_word7) {

        String [] time_split ;
        String [] hour_and_min ;
        String [] arr;
        String contents ;
        String username;

        Integer hour ;
        Integer min ;
        String dayFormat ;
        String strHour;
        String strMin;
        int lineCnt = 0 ;

        try {
            set_var(is_word1,is_word2,is_word3,is_word4,is_word5,is_word6,is_word7);
            setFilePath(is_yokList ,is_koreanVerb);
            write_verb_list() ;

            FileInputStream fis  = new FileInputStream(txtFile);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String _1line ;
            while(  (_1line = buffer.readLine()) != null)
            {
                if (_1line.matches(talk_regex)) {
                    arr = line_parse(_1line);

                    username = arr[1];
                    contents = arr[2];

                    user_idx = check_user_idx(username);
                    u_info = userInfo_list.get(user_idx);

                    time_split = arr[0].split("오");
                    dayFormat = time_split[0];

                    time_split = arr[0].split(" ");
                    hour_and_min = time_split[4].split(":");
                    hour =  Integer.parseInt(hour_and_min[0]) ;
                    min = Integer.parseInt(hour_and_min[1]);

                    if (time_split[3].equals("오후") && hour != 12){
                        hour+=12 ;
                    }
                    if (time_split[3].equals("오전") && hour == 12){
                        hour = 0 ;
                    }
                    strHour = hour.toString();
                    strMin = min.toString();

                    if(strHour.length() == 1){
                        strHour = "0" + strHour;
                    }
                    if(strMin.length() == 1){
                        strMin = "0" + strMin;
                    }
                    dayFormat = dayFormat + strHour + ":" + strMin ;
                    time_freq_cnt[hour]++;
                    total_hours_cnt++;

                    if(beforeTimeFormat != null){
                        if(!beforeUser.equals(username))
                        {
                            nowDate = simpleDateFormat.parse(dayFormat);
                            prevDate = simpleDateFormat.parse(beforeTimeFormat);

                            if(!nowDate.equals(prevDate)){
                                interval = new Interval(prevDate.getTime(), nowDate.getTime());
                                period = interval.toPeriod();

                                if(period.getDays() > 1 || period.getHours() > 12 ){
                                    u_info.increase_suntok_times();
                                }
                            }
                        }
                    }

                    beforeUser = username;
                    beforeTimeFormat= dayFormat;

                    increaseUserStat(contents);
                    contents_illegal_check(contents);
                }
                else if(!_1line.matches(intro_regex) ){
                    if(!_1line.matches(saved_date_regex) ){
                        if(!_1line.matches(day_notify_regex)){
                            if(!_1line.matches(invite_regex)){
                                if(!_1line.matches("")){
                                    total_line_cnt++;
                                    increaseUserStat(_1line);
                                    contents_illegal_check(_1line);
                                }
                            }
                        }
                    }
                }
                System.out.println("line : " + lineCnt++);
            }
            buffer.close();
        } catch (IOException e) {
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void set_var(InputStream is_word1,InputStream is_word2,InputStream is_word3,InputStream is_word4,InputStream is_word5,InputStream is_word6,InputStream is_word7) {

        try {
            trie.add(Trie.builder().build());
            BufferedReader br_word1 = new BufferedReader(new InputStreamReader(is_word1,"UTF-8"));
            BufferedReader br_word2 = new BufferedReader(new InputStreamReader(is_word2,"UTF-8"));
            BufferedReader br_word3 = new BufferedReader(new InputStreamReader(is_word3,"UTF-8"));
            BufferedReader br_word4 = new BufferedReader(new InputStreamReader(is_word4,"UTF-8"));
            BufferedReader br_word5 = new BufferedReader(new InputStreamReader(is_word5,"UTF-8"));
            BufferedReader br_word6 = new BufferedReader(new InputStreamReader(is_word6,"UTF-8"));
            BufferedReader br_word7 = new BufferedReader(new InputStreamReader(is_word7,"UTF-8"));

            Trie.TrieBuilder builder = Trie.builder();

            while ((_1line = br_word1.readLine()) != null) {
                builder.addKeyword(_1line);
            }
            br_word1.close();
            trie.add(builder.build());

            builder = Trie.builder();
            while ((_1line = br_word2.readLine()) != null) {
                builder.addKeyword(_1line);
            }
            br_word2.close();
            trie.add(builder.build());

            builder = Trie.builder();
            while ((_1line = br_word3.readLine()) != null) {
                builder.addKeyword(_1line);
            }
            br_word3.close();
            trie.add(builder.build());

            builder = Trie.builder();
            while ((_1line = br_word4.readLine()) != null) {
                builder.addKeyword(_1line);
            }
            br_word4.close();
            trie.add(builder.build());
/*
            while ((_1line = br_word5.readLine()) != null) {
                builder5.addKeyword(_1line);
                words_sort_by_size.get(5).add(_1line);
            }
            trie.add(builder5.build());

            while ((_1line = br_word6.readLine()) != null) {
                builder6.addKeyword(_1line);
                words_sort_by_size.get(6).add(_1line);
            }
            trie.add(builder6.build());

            while ((_1line = br_word7.readLine()) != null) {
                builder.addKeyword(_1line);
                words_sort_by_size.get(7).add(_1line);
            }
            trie.add(builder7.build());
*/
        } catch (IOException e) {
        }
    }

    void setFilePath(InputStream is_yokList ,InputStream is_koreanVerb) throws IOException {

        korean_verb_list_br = new BufferedReader(new InputStreamReader(is_koreanVerb,"UTF-8"));
        yok_br = new BufferedReader(new InputStreamReader(is_yokList,"UTF-8"));
        String  yok = yok_br.readLine();
        yok_list_beforeTrim = yok.split(",");

        for(String y : yok_list_beforeTrim){
            yok_list.add(y.trim());
        }
    }

    void write_verb_list() throws IOException {
        String verb;
        while ((verb = korean_verb_list_br.readLine()) != null){
            verb_list.add(verb);
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    String[] line_parse(String s) {
        int comma_loc = s.indexOf(",");
        int colon_idx = s.indexOf(" : ");
        String chat_time = s.substring(0, comma_loc);
        String name = s.substring(comma_loc + 2, colon_idx );
        String chat_content = s.substring(colon_idx + 3, s.length());
        String arr[] = {chat_time, name, chat_content};
        return arr;
    }

    int check_user_idx(String username){

        user_idx = user_name_list.indexOf(username);
        if (user_idx == -1) {
            user_name_list.add(username);
            userInfo_list.add(new user_info());
            user_idx = user_name_list.indexOf(username);
        }
        return user_idx;
    }

    void increaseUserStat(String contents) throws IOException {

        user_info u_info = userInfo_list.get(user_idx);

        u_info.increase_total_char_cnt( contents.length() );
        u_info.increase_Line_cnt();

        for(String y :yok_list){
            if(contents.contains(y)){
                u_info.increaseYok(y);
            }
        }

        if(contents.contains("?")){
            u_info.increase_Question_cnt();
        }
        if(contents.contains("!")){
            u_info.increase_Alarm_cnt();
        }
        if(contents.contains(";")){
            u_info.increase_Sweat_cnt();
        }
        if(contents.contains("..")){
            u_info.increase_dotdot_cnt();
        }
        if(contents.contains("ㅋㅋ") || contents.contains("크크")){
            u_info.increase_kk_cnt();
        }
        if(contents.contains("ㅎㅎ") || contents.contains("흐흐")){
            u_info.increase_hh_cnt();
        }
    }

    void contents_illegal_check(String contents) throws IOException {

        if(!contents.matches(meaningless_line_regex)){
            total_char_cnt+=contents.length();

            contents_word_match_check(contents);
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    void contents_word_match_check(String contents) {

        contents = contents.trim();
        int contents_len = contents.length();
        int ngram_num = contents_len;
        int start ;
        String s;
        String substr1;
        String substr2;

        h_map_ref = userInfo_list.get(user_idx).getWord_cnt_list();

        Trie trie_local ;
        Emit emit ;

        Iterator it_emit;

        while (ngram_num >= min_word_len) {
            if (ngram_num > max_word_len) {
                ngram_num = max_word_len;
            }

            trie_local = trie.get(ngram_num) ;
            emits = trie_local.parseText(contents);
            it_emit = emits.iterator();

            if(it_emit.hasNext()){

                emit =(Emit)it_emit.next();
                start = emit.getStart();
                s = emit.getKeyword();

                // found
                if(start != -1){

                    increase_users_wordcnt(s);

                    ////////////////////////////////////////////

                    if (start != 0) {
                        substr1 = contents.substring(0, start);
                        contents_word_match_check(substr1);
                    }
                    if ((start + min_word_len - 1) <= (contents_len - 1) ) {
                            substr2 = contents.substring(start + 1, contents_len);
                            contents_word_match_check(substr2);
                    }
                    return;
                }
            }
            ngram_num--;
        }
    }

    void increase_users_wordcnt(String listed_word){

        Integer cnt_in_hmap = h_map_ref.get(listed_word);
        Integer cnt = common_freq_words.get(listed_word);

        if (cnt_in_hmap != null) {
            h_map_ref.put(listed_word, cnt_in_hmap + 1);
        } else {
            h_map_ref.put(listed_word, 1);
        }

        if (cnt != null) {
            common_freq_words.put(listed_word, cnt + 1);
        } else {
            common_freq_words.put(listed_word, 1);
        }
    }


    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return ( o1.getValue() ).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}