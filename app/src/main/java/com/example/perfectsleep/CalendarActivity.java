package com.example.perfectsleep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.perfectsleep.firestoreDB.Firestore;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private CalendarView calendarView;
    private LinearLayout linearLayout;
    private String TAG = "CalAct";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setLatestSleepScore();

        //Build the Recyclerview
        //call Firestore build records

       ArrayList<DateText> dates= Firestore.getInstance().buildRecords(); //fill with start dates from database

        recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true); Maybe
        layoutManager = new LinearLayoutManager(this);
        adapter = new DateAdapter(dates);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Button backButton = (Button)findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalendarActivity.this, MainActivity.class));
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



    public void setLatestSleepScore(){
        ////////Fix labels, time and set max y value



        //setting sleep score that appears at top of the activity
        String score = Firestore.getInstance().getLastSleepScore();

        //give score to user
        final TextView textViewToChange = (TextView) findViewById(R.id.sleepScoreValue);
        textViewToChange.setText(score);

        //after setting the sleepScore, set the graph
        setLatestGraph();
    }

    public void setLatestGraph(){
        ///////Fix background color
        GraphView lineGraph = (GraphView) findViewById(R.id.graph);
        //lineGraph.setThi
        LineGraphSeries<DataPoint> defaultGraph= Firestore.getInstance().setDefaultGraph();

        lineGraph.addSeries(defaultGraph);
    }
}