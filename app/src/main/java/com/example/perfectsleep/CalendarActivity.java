package com.example.perfectsleep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.perfectsleep.firestoreDB.Firestore;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalendarActivity extends AppCompatActivity {

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


        //Firestore.getInstance().buildRecords();
        //get current day sleep score
        //need average of sleep confidence scores of a sleep timeframe

        //give it to the user


        linearLayout = (LinearLayout) findViewById(R.id.scrollLinearLayout);

        //linearLayout.se

        /*calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int dayOfMonth) {
                String date = year + "/" + month + "/"+ dayOfMonth ;
                //Intent intent = new Intent(CalendarActivity.this,MainActivity.class);
                //intent.putExtra("date",date);
                //startActivity(intent);
                //Snackbar snack = Snackbar.make(findViewById(R.id.calendarlayout), "Date: " + date + "\nScore: no data" , Snackbar.LENGTH_LONG);
                //snack.show();

                //Create and draw graph

                GraphView graph = (GraphView) findViewById(R.id.graph);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(0, 1),
                        new DataPoint(1, 5),
                        new DataPoint(2, 3),
                        new DataPoint(3, 2),
                        new DataPoint(4, 6)
                });
                graph.addSeries(series);


            }
        });*/
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
        //setting sleep score that appears at top of the activity
        String score = Firestore.getInstance().getLastSleepScore();
        BigDecimal bd = new BigDecimal(score);
        BigDecimal rounded = bd.setScale(1, RoundingMode.FLOOR);

        //give score to user
        final TextView textViewToChange = (TextView) findViewById(R.id.sleepScoreValue);
        textViewToChange.setText(
                rounded + "");
    }
}