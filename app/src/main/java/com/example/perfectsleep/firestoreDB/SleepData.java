package com.example.perfectsleep.firestoreDB;

import java.util.HashMap;
import java.util.Map;

public class SleepData {
    private Long startTime;
    //private long endTime;
    //private Map<Long, Integer> timeConfidence = new HashMap<>(); ---------------------
    private Map<String, Integer> timeConfidence = new HashMap<>();

    public SleepData(){}

    //public SleepData(long startTime, long endTime, Map<Long, Integer> timeConfidence){ -----------------
    public SleepData(Long startTime, Map<String, Integer> timeConfidence){
        this.startTime = startTime;
        //this.endTime = endTime;
        this.timeConfidence = timeConfidence;
    }


    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /*public long getEndTime() {
        return endTime;
    }*/

    /*public void setEndTime(long endTime) {
        this.endTime = endTime;
    }*/

    //public Map<Long, Integer> getTimeConfidence() {-----------------
    public Map<String, Integer> getTimeConfidence() {
        return timeConfidence;
    }

    //public void setTimeConfidence(Map<Long, Integer> timeConfidence) {-----------------
    public void setTimeConfidence(Map<String, Integer> timeConfidence) {
        this.timeConfidence = timeConfidence;
    }
}