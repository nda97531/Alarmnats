package com.e15.alarmnats.Model;

import android.net.Uri;

import java.io.Serializable;

public class Alarm implements Serializable {

    private int _id;
    private String alarmTime;
    private long alarmTimeInMillis;
    private boolean alarmStatus;
    private String ringtoneName;
    private String ringtoneUri;
    private String label;
    private Integer flag;

    private String question, answer;

    public Alarm() {}

    public Alarm(int _id, String alarmTime, long alarmTimeInMillis, boolean alarmStatus,
                 String ringtoneName, String ringtoneUri, String label, int flag, String question, String answer) {
        this._id = _id; //
        this.alarmTime = alarmTime; //
        this.alarmTimeInMillis = alarmTimeInMillis;
        this.alarmStatus = alarmStatus; //
        this.ringtoneName = ringtoneName; //
        this.ringtoneUri = ringtoneUri; //
        this.label = label; //
        this.flag = flag; //
        this.question = question; //
        this.answer = answer; //
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public long getAlarmTimeInMillis() {
        return alarmTimeInMillis;
    }

    public void setAlarmTimeInMillis(long alarmTimeInMillis) {
        this.alarmTimeInMillis = alarmTimeInMillis;
    }

    public boolean isAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(boolean alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public String getRingtoneName() {
        return ringtoneName;
    }

    public void setRingtoneName(String ringtoneName) {
        this.ringtoneName = ringtoneName;
    }

    public String getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
