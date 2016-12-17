package com.example.administrator.printkakao_retry;

import java.util.HashMap;

/**
 * Created by Administrator on 2016-11-21.
 */
public class user_info {
    int question_cnt = 0;
    int alarm_cnt = 0;
    int sweat_cnt = 0;
    int dotdot_cnt = 0;
    int kk_cnt = 0;
    int hh_cnt = 0;
    int positive_cnt = 0;
    int negative_cnt = 0;
    int total_char_cnt = 0;
    int suntok_times = 0;
    int line_cnt = 0 ;

    public int getLine_cnt() {
        return line_cnt;
    }

    public void increase_Line_cnt() {
        line_cnt++ ;
    }

    public int getYok_cnt() {
        return yok_cnt;
    }

    public HashMap<String, Integer> getYok_stat() {
        return yok_stat;
    }

    int yok_cnt = 0 ;
    String user_name ;
    HashMap<String, Integer> yok_stat = new HashMap<String, Integer>();
    HashMap<String, Integer> word_cnt_list = new HashMap<String, Integer>();

    public HashMap<String, Integer> getWord_cnt_list() {
        return word_cnt_list;
    }

    public int getSuntok_times() {
        return suntok_times;
    }

    public void increase_suntok_times(){
        suntok_times++;
    }

    public int getQuestion_cnt() {
        return question_cnt;
    }

    public int getAlarm_cnt() {
        return alarm_cnt;
    }

    public int getSweat_cnt() {
        return sweat_cnt;
    }

    public int getDotdot_cnt() {
        return dotdot_cnt;
    }

    public int getKk_cnt() {
        return kk_cnt;
    }

    public int getHh_cnt() {
        return hh_cnt;
    }

    public int getPositive_cnt() {
        return positive_cnt;
    }

    public int getNegative_cnt() {
        return negative_cnt;
    }

    public int getTotal_char_cnt() {
        return total_char_cnt;
    }

    public String getUser_name() {
        return user_name;
    }

    void increase_total_char_cnt(Integer increment){
        total_char_cnt += (int)increment ;
    }

    void increase_Question_cnt(){
        question_cnt++;
    }
    void increase_Alarm_cnt(){
        alarm_cnt++;
    }
    void increase_Sweat_cnt(){
        sweat_cnt++;
    }
    void increase_dotdot_cnt(){
        dotdot_cnt++;
    }
    void increase_kk_cnt(){
        kk_cnt++;
    }
    void increase_hh_cnt(){
        hh_cnt++;
    }
    void increase_positive_cnt(){
        positive_cnt++;
    }
    void increase_negative_cnt(){
        negative_cnt++;
    }
    void increase_yok_cnt(){
        yok_cnt++;
    }

    void increaseYok(String yok){
        Integer cnt ;
        increase_yok_cnt();
        cnt =yok_stat.get(yok);

        if(cnt == null){
            yok_stat.put(yok, 1);
        }
        else {
            yok_stat.put(yok, cnt + 1);
        }
    }
}
