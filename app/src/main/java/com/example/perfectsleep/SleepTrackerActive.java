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

public class SleepTrackerActive extends AppCompatActivity{


    private Chronometer chronometer;
    boolean start = true;
    private int starttime;
    private int endtime;
    ArrayList<Integer> times = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();
    ActivityRecognitionClient actrec;
    Snackbar snack;
    ActivityResultLauncher arl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SleepTracker", "line 22");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker_active);
        arl =  registerForActivityResult(new ActivityResultContracts.RequestPermission(), yes -> {});
        //this needed?
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //Start chronometer
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        snack = Snackbar.make(findViewById(R.id.sleeplayout), "test popup", Snackbar.LENGTH_LONG);
        //Button code to end sleep tracker and bring the user to the main screen
        Button endSleep = (Button)findViewById(R.id.buttonEndSleepTracker);
        endSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!start) {
                    endtime = 0; //// get end time from phone
                    //put starttime,endtime, arrays in database here
                    startActivity(new Intent(SleepTrackerActive.this, MainActivity.class));
                }else{
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                        arl.launch(Manifest.permission.ACTIVITY_RECOGNITION);
                    }
                    if(ContextCompat.checkSelfPermission(SleepTrackerActive.this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                        snack.show();
                        chronometer.start();
                        endSleep.setText("End Sleep");
                        starttime = 0;//////////get start time from phone
                        start = false;
                        actrec = new ActivityRecognitionClient(SleepTrackerActive.this);

                        //After this is what should be in background if possible
                        //actrec.requestSleepSegmentUpdates(getData, new SleepSegmentRequest(SleepSegmentRequest.CLASSIFY_EVENTS_ONLY));

                    }
                }
            }
        });

        Log.d("SleepTracker", "line 41");
    }
    //PendingIntent getData = PendingIntent.getActivity(SleepTrackerActive.this,1,)
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}



/*
links for future features
https://stackoverflow.com/questions/7197798/get-the-microphone-sound-level-decibel-level-in-android

*/