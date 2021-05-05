package com.example.perfectsleep;
//Source: https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sp;
    CheckBox screenLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        sp = getSharedPreferences("Setting", getApplicationContext().MODE_PRIVATE);
        screenLock = findViewById(R.id.screenLockCheck);
        SeekBar sensitivity = findViewById(R.id.senseBar);
        AlertDialog.Builder alertd = new AlertDialog.Builder(SettingsActivity.this);
        alertd.setTitle("Background Data")
                .setMessage("Collecting sleep data in background can impact battery life.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        confirmUncheckBox();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        recheckBox();
                    }
                });
        AlertDialog dial = alertd.create();

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
                //if turning on warn that battery life will could be impacted
                if(!isChecked){
                    dial.show();
                }
                SharedPreferences.Editor addsp = sp.edit();
                addsp.putBoolean("dontkeeprecording",isChecked);
                addsp.commit();

            }
        });
        sensitivity.setProgress(sp.getInt("sensitivity", 50));
        screenLock.setChecked(sp.getBoolean("dontkeeprecording",false));
    }
    public void recheckBox(){
        screenLock.setChecked(true);
    }
    public void confirmUncheckBox(){
        screenLock.setChecked(false);
    }
}