package com.example.perfectsleep;
//sources: https://medium.com/@iamtjah/how-to-create-a-simple-graph-in-android-6c484324a4c1
import android.content.Intent;
import android.os.Bundle;

import com.example.perfectsleep.firestoreDB.Firestore;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Firestore.OnDataSetListener {

    public String StartSleepScore;


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
        Button calendarButton = (Button) findViewById(R.id.buttonCalendar);
        calendarButton.setOnClickListener(new View.OnClickListener() { // need to look at
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            }
        });

        Firestore.getInstance().authenticate(this, (success) -> {
            if (success) Log.d("FireDB", "Authenticated");
            else Toast.makeText(MainActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            ;
        });

        Firestore.getInstance().OnDataSetListener(this);

        //sets sleep score under the moon
        //works, but there is a bug: upon fresh start the score is 0
        try {
            setLatestSleepScore();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*//FOR TESTING PURPOSES
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final String usr = auth.getCurrentUser().getUid();


        map1.put(String.valueOf(1620052615103L), 63);
        map1.put(String.valueOf(1620053216103L), 75);
        map1.put(String.valueOf(1620053817103L), 89);
        map1.put(String.valueOf(1620054418103L), 100);
        map1.put(String.valueOf(1620055019103L), 100);
        map1.put(String.valueOf(1620055620103L), 100);
        map1.put(String.valueOf(1620056221103L), 42);
        map1.put(String.valueOf(1620056822103L), 55);
        map1.put(String.valueOf(1620057423103L), 20);
        map1.put(String.valueOf(1620058024103L), 10);
        map1.put(String.valueOf(1620058625103L), 0);


        map2.put(String.valueOf(1620136615103L), 34);
        map2.put(String.valueOf(1620137215103L), 88);
        map2.put(String.valueOf(1620137815103L), 44);
        map2.put(String.valueOf(1620138415103L), 100);
        map2.put(String.valueOf(1620139015103L), 100);
        map2.put(String.valueOf(1620139615103L), 100);
        map2.put(String.valueOf(1620140215103L), 15);
        map2.put(String.valueOf(1620140815103L), 0);
        map2.put(String.valueOf(1620141415103L), 10);
        map2.put(String.valueOf(1620142015103L), 20);
        map2.put(String.valueOf(1620142615103L), 0);



        SleepData sd = new SleepData(1620052615103L,map1);
        SleepData sd2 = new SleepData(1620136615103L,map2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection(usr).document(sd.getStartTime() + "").set(sd)
            .addOnSuccessListener(aVoid -> Log.d("TEST", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.d("TEST", "Error writing document", e));

        db.collection(usr).document(sd2.getStartTime() + "") .set(sd2)
                .addOnSuccessListener(aVoid -> Log.d("TEST", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.d("TEST", "Error writing document", e));

        //FOR TESTING PURPOSES*/


        //Button code to bring user to active sleep tracker section
        Button sleepTrackerButton = (Button) findViewById(R.id.buttonSleepTracker);

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

    public void setLatestSleepScore() throws ParseException {
        Firestore.getInstance().getFireData(false, null);
    }

    @Override
    public void onScoreAndDataSet(boolean success, String score, ArrayList<String> resultList) {
        if (success) {
            StartSleepScore = score;
            final TextView textViewToChange = (TextView) findViewById(R.id.sleep_score);
            textViewToChange.setText(score);
        } else {
            Log.d("Data", "could not get data");
        }
    }

    @Override
    public void onListSet(boolean success, ArrayList<DateText> dates) {

    }

}