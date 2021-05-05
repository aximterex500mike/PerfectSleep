package com.example.perfectsleep;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.perfectsleep.firestoreDB.Firestore;
import com.example.perfectsleep.firestoreDB.SleepData;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SleepTracker extends AppCompatActivity {


    private Chronometer chronometer;
    boolean start = true;
    private long startTime;
    ActivityRecognitionClient actrec = null;
    ActivityResultLauncher arl;
    Intent intent;
    SharedPreferences sharedpreferences;
    PendingIntent getData;
    Button endSleep;
    boolean lock;
    TextView showTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Perfect Sleep");
        arl =  registerForActivityResult(new ActivityResultContracts.RequestPermission(), yes -> {});
        sharedpreferences = getSharedPreferences("Setting", getApplicationContext().MODE_PRIVATE);
        //startCollectingData(); /////////REMOVE THIS. THIS LINE WAS FOR TESTING

        showTime = findViewById(R.id.textView6);
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
                        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                        boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
                        if(!isIgnoringBatteryOptimizations){
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        }else {
                            startCollectingData();
                        }
                    }else{
                        arl.launch(Manifest.permission.ACTIVITY_RECOGNITION);
                    }
                }
            }
        });
    }

    public void startCollectingData(){  //pull id from firebase and start time
        //update preferences and swap button
        endSleep.setText("End Sleep");
        start = false;
        startTime = Calendar.getInstance().getTimeInMillis();
        SimpleDateFormat simpledatef = new SimpleDateFormat("HH:mm:ss");
        simpledatef.setTimeZone(TimeZone.getDefault());
        String strStart = simpledatef.format(new Date(startTime));
        showTime.setText(strStart);

        //call to firestore to add start time to database
        Firestore.getInstance().startCollecting(startTime);

        SharedPreferences.Editor e = sharedpreferences.edit();
        e.putBoolean("button", false);
        e.putLong("starttime", startTime);
        e.commit();


        actrec = new ActivityRecognitionClient(SleepTracker.this);
        intent = new Intent(getApplicationContext(), getSleepData.class);
        intent.putExtra("start", startTime);
        getData = PendingIntent.getService(SleepTracker.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        actrec.requestSleepSegmentUpdates(getData, new SleepSegmentRequest(SleepSegmentRequest.CLASSIFY_EVENTS_ONLY));
    }

    public void endCollectingData(){ //add endtime

        //added endtime for database purposes
        //long endTime = Calendar.getInstance().getTimeInMillis(); //// get end time from phone, store in db
        //Firestore.getInstance().endCollecting(endTime);

        //used to disable sleep recording, must recreate intents/pending intent incase user has navigated away from page
        actrec = new ActivityRecognitionClient(SleepTracker.this);
        intent = new Intent(getApplicationContext(), getSleepData.class);
        intent.putExtra("start", startTime);
        getData = PendingIntent.getService(SleepTracker.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        actrec.removeSleepSegmentUpdates(getData);

        //update ui info
        SharedPreferences.Editor e = sharedpreferences.edit();
        e.putBoolean("button", true);
        e.putLong("starttime", -1);
        e.commit();

        Toast.makeText(SleepTracker.this, "Sleep data logged.",
                Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(SleepTracker.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTime = (TextView)findViewById(R.id.textView6);
        start = sharedpreferences.getBoolean("button", true);
        if(start == false){
            endSleep.setText("End Sleep");
        }
        lock = sharedpreferences.getBoolean("dontkeeprecording",false);
        startTime = sharedpreferences.getLong("starttime", -1);

        if(startTime != -1){
            SimpleDateFormat simpledatef = new SimpleDateFormat("HH:mm:ss");
            String strStart = simpledatef.format(new Date(startTime));
            showTime.setText(strStart);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(lock){
            endCollectingData();
        }
    }
}