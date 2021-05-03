package com.example.perfectsleep;
//sources: https://medium.com/@iamtjah/how-to-create-a-simple-graph-in-android-6c484324a4c1
import android.content.Intent;
import android.os.Bundle;

import com.example.perfectsleep.firestoreDB.Firestore;
import com.example.perfectsleep.firestoreDB.SleepData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //TESTING ONLY
    //private final Map<String, Integer> map1 = new HashMap<>();
    //private final Map<String, Integer> map2 = new HashMap<>();
    //TESTING ONLY


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

        //sets sleep score under the moon
        //works, but there is a bug: upon fresh start the score is 0
        setLatestSleepScore();

        //FOR TESTING PURPOSES
        /*final FirebaseAuth auth = FirebaseAuth.getInstance();
        final String usr = auth.getCurrentUser().getUid();


        map1.put(String.valueOf(1619220000), 63);
        map1.put(String.valueOf(1619221000), 75);
        map1.put(String.valueOf(1619222000), 89);
        map1.put(String.valueOf(1619223000), 100);
        map1.put(String.valueOf(1619224000), 100);
        map1.put(String.valueOf(1619225000), 100);
        map1.put(String.valueOf(1619226000), 42);
        map1.put(String.valueOf(1619227000), 55);
        map1.put(String.valueOf(1619228000), 20);
        map1.put(String.valueOf(1619229000), 10);
        map1.put(String.valueOf(1619230000), 0);


        map2.put(String.valueOf(1619320000), 34);
        map2.put(String.valueOf(1619321000), 88);
        map2.put(String.valueOf(1619322000), 44);
        map2.put(String.valueOf(1619323000), 100);
        map2.put(String.valueOf(1619324000), 100);
        map2.put(String.valueOf(1619325000), 100);
        map2.put(String.valueOf(1619326000), 15);
        map2.put(String.valueOf(1619327000), 0);
        map2.put(String.valueOf(1619328000), 10);
        map2.put(String.valueOf(1619329000), 20);
        map2.put(String.valueOf(1619330000), 0);



        SleepData sd = new SleepData(1619220000,1619230000, map1);
        SleepData sd2 = new SleepData(1619320000,1619330000, map2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection(usr).document(sd.getStartTime() + "").set(sd)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TEST", "DocumentSnapshot successfully written!");
            } })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TEST", "Error writing document", e);
                    } });

        db.collection(usr).document(sd2.getStartTime() + "") .set(sd2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TEST", "DocumentSnapshot successfully written!");
                    } })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TEST", "Error writing document", e);
                    } });
*/
        //FOR TESTING PURPOSES


        //Button code to bring user to active sleep tracker section
        Button sleepTrackerButton = (Button)findViewById(R.id.buttonSleepTracker);

        sleepTrackerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SleepTracker.class));
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
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setLatestSleepScore(){
        //Setting sleep score that appears at top of the activity
        String score = Firestore.getInstance().getLastSleepScore();

        //give score to user
        final TextView textViewToChange = (TextView) findViewById(R.id.sleep_score);
        textViewToChange.setText(score);
    }
}