package com.example.perfectsleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class SleepTrackerActive extends AppCompatActivity {


    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SleepTracker", "line 22");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker_active);
        //this needed?
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //Start chronometer
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.start();

        //Button code to end sleep tracker and bring the user to the main screen
        Button endSleep = (Button)findViewById(R.id.buttonEndSleepTracker);
        endSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SleepTrackerActive.this, MainActivity.class));
            }
        });
        Log.d("SleepTracker", "line 41");
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
}


/*
links for future features
https://stackoverflow.com/questions/7197798/get-the-microphone-sound-level-decibel-level-in-android

*/