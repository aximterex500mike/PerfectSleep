package com.example.perfectsleep;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.SleepClassifyEvent;

import java.util.List;

public class getSleepData extends IntentService {


    public getSleepData() {
        super("getSleepData");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if(SleepClassifyEvent.hasEvents(intent)){
                intent.getLongExtra("starttime", -1); //start time of current sleep
                intent.getStringExtra("id"); //userid
                List<SleepClassifyEvent> data = SleepClassifyEvent.extractEvents(intent);
                for(int i = 0; i < data.size(); i++){
                    SleepClassifyEvent event = data.get(i);
                    event.getConfidence(); //1-100, high is better sleep
                    event.getTimestampMillis(); //time in miliseconds since 1/1/1970
                    event.getMotion(); //amount of motion from 1 to 6, higher being worse ( i think)

                    //put a log here
                    // insert into database here, id should be unique to each person, starttime can identify whats data us in the same night
                    //start times and end times
                    //time stamp
                    //
                }
                //destroy service after done
                stopSelf();
            }
        }
    }
}