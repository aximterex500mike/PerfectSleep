package com.example.perfectsleep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perfectsleep.firestoreDB.Firestore;

import java.text.ParseException;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements Firestore.OnDataSetListener {

    private DateAdapter adapter;
    SharedPreferences sp;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Perfect Sleep");
        Firestore.getInstance().OnDataSetListener(this);

        sp = getSharedPreferences("Setting", getApplicationContext().MODE_PRIVATE);

        try {
            setNewData(false, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Firestore.getInstance().buildRecords(); //fill recycler with start dates from database

        Button backButton = (Button) findViewById(R.id.buttonBack);
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


    public void setNewData(boolean wantSpecific, String date) throws ParseException {
        Firestore.getInstance().getFireData(wantSpecific, date, sp.getInt("sensitivity", 50));

    }

    @Override
    public void onScoreAndDataSet(boolean success, String score, ArrayList<String> resultList) {
        if (success) {
            score = String.valueOf(Float.parseFloat(score) * sp.getInt("sensitivity", 50));
            //change score
            TextView textViewToChange = findViewById(R.id.sleepScoreValue);
            textViewToChange.setText(score);

            //update results
            ListView listView = findViewById(R.id.sleepResults);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, resultList);
            listView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();


        } else {
            Log.d("Data", "could not get data");
        }
    }

    @Override
    public void onListSet(boolean success, ArrayList<DateText> dates) {
        if (success) {
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            adapter = new DateAdapter(dates);


            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            adapter.setOnDateClickListener(pos -> {
                DateText date = dates.get(pos);
                String dateClicked = date.getDate();  //this is a string of the date shown in recyclerview
                setNewData(true, dateClicked);
                adapter.notifyItemChanged(pos);
            });

        } else {
            Log.d("Data", "could not get data");
        }
    }
}