package com.example.perfectsleep;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.preference.PreferenceFragmentCompat;  IMPORT NOT WORKING OVER HERE

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        //ActionBar actionBar = getSupportActionBar();
        sp = getSharedPreferences("Setting", getApplicationContext().MODE_PRIVATE);
        CheckBox screenLock = findViewById(R.id.screenLockCheck);
        SeekBar sensitivity = findViewById(R.id.senseBar);
        sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //progress from 0 to 100,
                sensitivity.setProgress(progress);
                SharedPreferences.Editor addsp = sp.edit();
                addsp.putInt("sensitivity",progress);
                addsp.commit();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        screenLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor addsp = sp.edit();
                addsp.putBoolean("lockscreen",isChecked);
            }
        });
        sensitivity.setProgress(sp.getInt("sensitivity", 50));
        screenLock.setChecked(sp.getBoolean("lockscreen",false));
    }
}