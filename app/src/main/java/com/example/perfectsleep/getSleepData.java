package com.example.perfectsleep;

import com.example.perfectsleep.firestoreDB.Firestore;
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
        Log.d("Where", "onHandleIntent");
        if (intent != null) {
            if(SleepClassifyEvent.hasEvents(intent)){
                long startTime = intent.getLongExtra("starttime", -1); //start time of current sleep
                //intent.getStringExtra("id"); //UID  id gotten from firebase
                List<SleepClassifyEvent> data = SleepClassifyEvent.extractEvents(intent);
                for(int i = 0; i < data.size(); i++){
                    SleepClassifyEvent event = data.get(i);

                    //.getConfidence : 1-100, high is better sleep
                    //.getTimeStampMillis : time in milliseconds since 1/1/1970

                    //calls firestore instance to log sleep data
                    Firestore.getInstance().logSleepData(event.getConfidence(),startTime, event.getTimestampMillis());

                    Log.d("sleepData", "sleep confidence:" + event.getConfidence()
                            + "     time stamp:" + event.getTimestampMillis() + "   motion:" + event.getMotion());
                }
                //destroy service after done
                stopSelf();
            }
        }
    }
}