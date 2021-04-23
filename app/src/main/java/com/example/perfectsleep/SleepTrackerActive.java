package com.example.perfectsleep;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.location.SleepSegmentRequest;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;
import java.util.Calendar;

public class SleepTrackerActive extends AppCompatActivity{


    private Chronometer chronometer;
    boolean start = true;
    private long starttime;
    private long endtime;
    String id = "test";
    ArrayList<Integer> times = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();
    ActivityRecognitionClient actrec = null;
    ActivityResultLauncher arl;
    Intent intent;
    SharedPreferences sharedpreferences;
    PendingIntent getData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this needed?
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setContentView(R.layout.activity_sleep_tracker_active);
        arl =  registerForActivityResult(new ActivityResultContracts.RequestPermission(), yes -> {});

        //Button code to end sleep tracker and bring the user to the main screen
        Button endSleep = (Button)findViewById(R.id.buttonEndSleepTracker);
        endSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!start) {
                    SharedPreferences.Editor e = sharedpreferences.edit();
                    e.putBoolean("button", true);
                    e.commit();
                    endCollectingData();
                    startActivity(new Intent(SleepTrackerActive.this, MainActivity.class));
                }else{
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                        arl.launch(Manifest.permission.ACTIVITY_RECOGNITION);
                    }
                    if(ContextCompat.checkSelfPermission(SleepTrackerActive.this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {

                        chronometer.start();
                        endSleep.setText("End Sleep");

                        //this must be stored
                        start = false;

                        SharedPreferences.Editor e = sharedpreferences.edit();
                        e.putBoolean("button", false);
                        e.commit();

                        startCollectingData();
                    }
                }
            }
        });
    }

    public void startCollectingData(){  //pull id from firebase and start time
        Log.d("Where", "you are at startCollectingData SleepTrackerActive.java");
        starttime = 0;//////////get start time from phone
        actrec = new ActivityRecognitionClient(SleepTrackerActive.this);
        intent = new Intent(getApplicationContext(), getSleepData.class);
        intent.putExtra("id", id);
        intent.putExtra("start", starttime);
        getData = PendingIntent.getService(SleepTrackerActive.this,1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        actrec.requestSleepSegmentUpdates(getData, new SleepSegmentRequest(SleepSegmentRequest.CLASSIFY_EVENTS_ONLY));
    }

    public void endCollectingData(){ //add endtime
        endtime = Calendar.getInstance().getTimeInMillis(); //// get end time from phone, store in db
        actrec.removeSleepSegmentUpdates(getData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        sharedpreferences = getSharedPreferences("buttonSetting", SleepTrackerActive.this.MODE_PRIVATE);
        start = sharedpreferences.getBoolean("button", true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor e = sharedpreferences.edit();
        e.putBoolean("button", true);
        e.commit();
    }
}



/*
links for future features
https://stackoverflow.com/questions/7197798/get-the-microphone-sound-level-decibel-level-in-android
*/