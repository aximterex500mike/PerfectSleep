package com.example.perfectsleep.firestoreDB;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.perfectsleep.DateText;
import com.example.perfectsleep.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeMap;

public class Firestore {

    String TAG = "firestore";
    private static final DecimalFormat decimalFormat = new DecimalFormat( "0.0");

    //SharedPreferences sp = getSharedPreferences("Setting", getApplicationContext().MODE_PRIVATE);
    private static Firestore INSTANCE;
    private FirebaseUser user;
    private FirebaseFirestore database;
    private DocumentReference date;
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    //final String usr = Objects.requireNonNull(auth.getCurrentUser()).getUid();

    SleepData sleepData, sdForSetup;
    private String  averageSleepScoreResult = "0";

    private Map<String, Integer> timeConfidence = new HashMap<>();

    public interface OnDataSetListener {
        void onScoreAndDataSet(boolean success, String score, ArrayList<String> results);
        void onListSet(boolean success, ArrayList<DateText> dates );
        //void onResultsSet(boolean success, ArrayList<String> results);
    }

    private OnDataSetListener listener;

    public void OnDataSetListener(OnDataSetListener listener){this.listener = listener;}

    public Firestore() {
    }

    //listener for authentication in OnClick
    public interface OnAuthenticatedListener {
        void onAuthenticated(boolean success);
    }

    public static synchronized Firestore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Firestore();
        }
        return INSTANCE;
    }

    public void authenticate(Activity activity, final OnAuthenticatedListener listener) {
        if (user == null) {
            database = FirebaseFirestore.getInstance();

            auth.signInAnonymously()
                    .addOnCompleteListener(activity, task -> {
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            //Log.d("UID", "UID: " + auth.getCurrentUser().getUid());
                            listener.onAuthenticated(true);
                        } else {
                            listener.onAuthenticated(false);
                        }
                    });
        }
        else {
            listener.onAuthenticated(true);
        }
    }

    public void startCollecting(Long startTime) {  //this start time is in milliseconds
        //Log.d(TAG, "collecting sleep data has started");

        //send start time
        sleepData = new SleepData();

        database.collection(user.getUid())
                .document(startTime + "")
                .set(sleepData)
                .addOnSuccessListener(aVoid -> {
                    //Log.d(TAG, "Collection initialised");
                })
                .addOnFailureListener(e -> {
                    //Log.d(TAG, "ERROR: " + e);
                });
        //set document reference "date"
        date = database.collection(user.getUid()).document(startTime + "");  //in millis
    }


    public void logSleepData(int confidence, long timestamp, long startTime){ //puts data gotten from getSleepData.java
        //confidence is 0-100 and timestamp is in milliseconds
        //updating map

        timeConfidence.put(String.valueOf(timestamp), confidence);// * sensitivityMultiplier);
        sleepData.setTimeConfidence(timeConfidence);

        //transaction to put updated map in the database
        database.runTransaction((Transaction.Function<Void>) transaction -> {
            transaction.update(date, "timeConfidence", sleepData.getTimeConfidence()); //sleepData.getTimeConfidence() should be the latest Map

            // Success
            return null;
        }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success!"))
                .addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e));
    }


    public void getFireData(boolean wantSpecific, String date, int sensitivity) throws ParseException {

        sdForSetup = new SleepData();

        if(wantSpecific){
            // Makes date into unix in milliseconds
            long uTime = dateToUnix(date);
            Log.d(TAG, "date: "+ date+ "  uTime: " +uTime);

            // Queries firestore for the specific data for the date selected
            long oneDayInMillis = 86400000;
            database.collection(user.getUid())
                    .whereGreaterThan("startTime", uTime)
                    .whereLessThan("startTime", uTime + oneDayInMillis) //find the sleep data for that day
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot qSnapshot = task.getResult();
                            if (!qSnapshot.isEmpty()) {
                                sdForSetup = qSnapshot.toObjects(SleepData.class).get(0);

                                //getting sleep score
                                List<Integer> targetList = new ArrayList<>(sdForSetup.getTimeConfidence().values());

                                int size = targetList.size();
                                if (size != 0) {
                                    int sum = 0;
                                    for (Integer i : targetList) sum += i;

                                    averageSleepScoreResult = decimalFormat.format((double) sum / size);

                                    ArrayList<String> resultsList = new ArrayList<>();
                                    timeConfidence = sdForSetup.getTimeConfidence();  //times mapped to confidences
                                    timeConfidence = new TreeMap<>(timeConfidence);
                                    for (String time : timeConfidence.keySet()) {
                                        resultsList.add(unixToHour(Long.parseLong(time)) + "    Score " + (timeConfidence.get(time)*sensitivity));
                                    }
                                    //Log.d(TAG, "ave sleepscore: " + averageSleepScoreResult);
                                    listener.onScoreAndDataSet(true, averageSleepScoreResult, resultsList);
                                }
                            }else listener.onScoreAndDataSet(true, "0",new ArrayList<>());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });


        }else {
            //gets the latest sleep score
            database.collection(user.getUid())
                    .orderBy("startTime", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot qSnapshot = task.getResult();
                            if (!qSnapshot.isEmpty()) {
                                sdForSetup = qSnapshot.toObjects(SleepData.class).get(0); //data for latest night

                                //getting sleep score
                                List<Integer> targetList = new ArrayList<>(sdForSetup.getTimeConfidence().values());

                                int size = targetList.size();

                                if (size != 0) {
                                    int sum = 0;
                                    for (Integer i : targetList)
                                        sum += i;

                                    averageSleepScoreResult = decimalFormat.format((double) sum / size);

                                    //building listView
                                    ArrayList<String> resultsList = new ArrayList<>();
                                    timeConfidence = sdForSetup.getTimeConfidence();  //times mapped to confidences
                                    timeConfidence = new TreeMap<>(timeConfidence);
                                    for (String time: timeConfidence.keySet()){
                                        resultsList.add(unixToHour(Long.parseLong(time)) + "    Score " + timeConfidence.get(time)*sensitivity);
                                    }
                                    //Log.d(TAG, "ave sleepscore: " + averageSleepScoreResult);
                                    listener.onScoreAndDataSet(true, averageSleepScoreResult, resultsList);
                                }
                            }else listener.onScoreAndDataSet(true, "0",new ArrayList<>());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    public void buildRecords(){
        //  What this does is build an arrayList of dateTexts that will fill the recycler view

        ArrayList<DateText> res = new ArrayList<>();
        database.collection(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String unix = document.getId();
                            Log.d(TAG, unix + "");
                            res.add(unixToDate(Long.parseLong(unix))); //unix in millis
                        }
                        listener.onListSet(true, res);
                        Log.d("rescheck", res + "");
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public DateText unixToDate(Long unix){

        Date date = new Date(unix);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat setDf = new SimpleDateFormat("MM/dd/yyyy");
        TimeZone setTimeZone = TimeZone.getTimeZone("America/New_York");
        setDf.setTimeZone( setTimeZone );
        String newDate = setDf.format(date);

        return new DateText(newDate);
    }

    public String unixToHour(Long unix){

        Date date = new Date(unix);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat setDf = new SimpleDateFormat("kk:mm");
        TimeZone setTimeZone = TimeZone.getTimeZone("America/New_York");
        setDf.setTimeZone( setTimeZone );
        String newDate = setDf.format(date);
        //newDate = newDate.replace(":","");


        return newDate;
    }

    public long dateToUnix(String dateToConvert) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat DateFor = new SimpleDateFormat("MM/dd/yyyy");
        Date date = DateFor.parse(dateToConvert);
        assert date != null;
        return date.getTime();
    }
}