package com.example.perfectsleep;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.SleepSegmentRequest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import java.util.Calendar;

public class SleepTracker extends AppCompatActivity {
    private Chronometer chronometer;
    boolean start = true;
    private long starttime;
    ActivityRecognitionClient actrec = null;
    ActivityResultLauncher arl;
    Intent intent;
    SharedPreferences sharedpreferences;
    PendingIntent getData;
    Button endSleep;
    boolean lock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        arl =  registerForActivityResult(new ActivityResultContracts.RequestPermission(), yes -> {});
        sharedpreferences = getSharedPreferences("Setting", getApplicationContext().MODE_PRIVATE);
        //Button code to end sleep tracker and bring the user to the main screen
        endSleep = (Button)findViewById(R.id.buttonEndSleepTracker);
        endSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!start) {

                    endCollectingData();
                    startActivity(new Intent(SleepTracker.this, MainActivity.class));
                }else{
                    if(ContextCompat.checkSelfPermission(SleepTracker.this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                        startCollectingData();
                    }else{
                        arl.launch(Manifest.permission.ACTIVITY_RECOGNITION);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
        if(!isIgnoringBatteryOptimizations){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

    }


    public void startCollectingData(){  //pull id from firebase and start time
        Log.e("Where", "you are at startCollectingData SleepTrackerActive.java");

        //update preferences and swap button
        endSleep.setText("End Sleep");
        start = false;
        starttime = Calendar.getInstance().getTimeInMillis();
        SharedPreferences.Editor e = sharedpreferences.edit();
        e.putBoolean("button", false);
        e.putLong("starttime",starttime);
        e.commit();

        actrec = new ActivityRecognitionClient(SleepTracker.this);
        intent = new Intent(getApplicationContext(), getSleepData.class);
        intent.putExtra("starttime", starttime);
        getData = PendingIntent.getService(SleepTracker.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        actrec.requestSleepSegmentUpdates(getData, new SleepSegmentRequest(SleepSegmentRequest.CLASSIFY_EVENTS_ONLY));

        if(lock){
            //force screen or cpu to stay on here, researching best way atm not implemented
        }
    }

    public void endCollectingData(){ //add endtime
        //used to disable sleep recording, must recreate intents/pending intent incase user has navigated away from page
        actrec = new ActivityRecognitionClient(SleepTracker.this);
        intent = new Intent(getApplicationContext(), getSleepData.class);
        intent.putExtra("start", starttime);
        getData = PendingIntent.getService(SleepTracker.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        actrec.removeSleepSegmentUpdates(getData);

        //update ui info
        SharedPreferences.Editor e = sharedpreferences.edit();
        e.putBoolean("button", true);
        e.putLong("starttime", -1);
        e.commit();
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sharedpreferences = getSharedPreferences("Setting", getApplicationContext().MODE_PRIVATE);
        start = sharedpreferences.getBoolean("button", true);
        if(start == false){
            endSleep.setText("End Sleep");
        }
        lock = sharedpreferences.getBoolean("dontalwaysrun",false);
        starttime = sharedpreferences.getLong("starttime", -1);

        if(start){
            Log.e("ResumeTest: ", "start true");
        }else{
            Log.e("ResumeTest: ", "start false");
        }
        if(lock){
            Log.e("lockTest: ", "lock true");
        }else{
            Log.e("lockTest: ", "lock false");
        }
        Log.e("ResumeTest: starttime = ", String.valueOf(starttime));

        if(starttime != -1){
            //display start time here (replaces chronomete when implemeneted)
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //IF the option is enabled, disable recording when activity leaves focus
        if(sharedpreferences.getBoolean("dontalwaysrun", false)){
            Log.e("endtest: ","data recording ended automatically");
            endCollectingData();
        }
    }
}
