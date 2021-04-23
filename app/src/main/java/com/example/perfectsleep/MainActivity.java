package com.example.perfectsleep;
//sources: https://medium.com/@iamtjah/how-to-create-a-simple-graph-in-android-6c484324a4c1
import android.content.Intent;
import android.os.Bundle;

import com.example.perfectsleep.firestoreDB.Firestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button calendarButton = (Button)findViewById(R.id.buttonCalendar);
        calendarButton.setOnClickListener(new View.OnClickListener() { // need to look at
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            }
        });

        Firestore.getInstance().authenticate(this, (success) -> {
            if (success) Log.d("FireDB", "Authenticated");
            else Toast.makeText(MainActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();;
        });
       /* FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

       // Code to test Firebase

        //set
        Map<String, Object> person = new HashMap<>();
        person.put("firstName", "sarah");
        person.put("lastName", "n");
        person.put("favColor", "yellow");

        db.collection("test").document("sarah")
                .set(person)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DBase", "DocumentSnapshot successfully written!");
                    } })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DBase", "Error writing document", e);
                    } });

        //get
        DocumentReference docRef = db.collection("test").document("sarah");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult(); if (document.exists()) {
                    Log.d("DBase", "DocumentSnapshot data: " + document.getData()); } else {
                    Log.d("DBase", "No such document"); }
            } else {
                Log.d("DBase", "get failed with ", task.getException());
            } });*/



        //Button code to bring user to active sleep tracker section
        Button sleepTrackerButton = (Button)findViewById(R.id.buttonSleepTracker);

        sleepTrackerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SleepTrackerActive.class));
            }
        });
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