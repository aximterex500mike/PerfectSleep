package com.example.perfectsleep.firestoreDB;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.perfectsleep.DateText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class Firestore {


    public Firestore() {
    }

    //listener for authentication in OnClick
    public interface OnAuthenticatedListener {
        void onAuthenticated(boolean success);
    }

    String TAG = "firestore";
    private static final DecimalFormat decimalFormat = new DecimalFormat( "0.0");

    private static Firestore INSTANCE;
    private FirebaseUser user;
    private FirebaseFirestore database;
    private DocumentReference date;
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    final String usr = auth.getCurrentUser().getUid();



    SleepData sleepData, sdForSetup;
    private String  averageSleepScoreResult = "0";
    private long startTime;
    private long endTime;

    //private final Map<Long, Integer> timeConfidence = new HashMap<>();----------------------------------
    private Map<String, Integer> timeConfidence = new HashMap<>();

    //private Firestore() {}

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

    public void startCollecting(Long startTime) {  //i dont need to store the start time. just use the first collected time as start time
        //Log.d(TAG, "collecting sleep data has started");

        //send start time
        sleepData = new SleepData();

        //Calendar operations
        Calendar calendar = Calendar.getInstance();
        //////////////////////////////////////FIXX
        
        //sleepData.setStartTime(startTime);

        database.collection(usr)
                .document(startTime + "")
                .set(sleepData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "Collection initialised");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.d(TAG, "ERROR: " + e);
                    }
                });

        //set document reference "date" 
        date = database.collection(usr).document(startTime + "");


    }


    public void logSleepData(int confidence, long timestamp){ //puts data gotten from getSleepData.java
        //updating map

        timeConfidence.put(String.valueOf(timestamp), confidence);
        sleepData.setTimeConfidence(timeConfidence);

        //transaction to put updated map in the database
        database.runTransaction((Transaction.Function<Void>) transaction -> {
            //DocumentSnapshot snapshot = transaction.get(date);

            transaction.update(date, "timeConfidence", sleepData.getTimeConfidence()); //sleepData.getTimeConfidence() should be the latest Map

            // Success
            return null;
        }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success!"))
                .addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e));

    }


    public String getLastSleepScore(){

        sdForSetup = new SleepData();

        Log.d(TAG, usr);

        database.collection(usr)
                .orderBy("startTime", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot qSnapshot = task.getResult();
                        sdForSetup = qSnapshot.toObjects(SleepData.class).get(0); //data for latest night

                        //getting sleep score
                        List<Integer> targetList = new ArrayList<>(sdForSetup.getTimeConfidence().values());
                        //Log.d(TAG, "target list:" +targetList);


                        int size = targetList.size();

                        if(size != 0) {
                            int sum = 0;
                            for (Integer i : targetList)
                                sum += i;
                            //Log.d(TAG, "sum:" + sum);

                            averageSleepScoreResult = decimalFormat.format((double) sum / size);

                            //used for graph
                            timeConfidence = sdForSetup.getTimeConfidence();

                            //Log.d(TAG, "return:" + averageSleepScoreResult);
                        }
                        //

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        //Log.d(TAG, sdForSetup.getTimeConfidence().values() + "");
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


        return averageSleepScoreResult;
    }

    public LineGraphSeries<DataPoint> setDefaultGraph() { //sets the default graph on calendar activity

        //sets map to graphMap sorted by key
        timeConfidence = new TreeMap<String, Integer>(timeConfidence); //Probably should not reassign timeConfidence
        Log.d(TAG,"graphMap: " + timeConfidence);

        //CALENDER
        int mapSize = timeConfidence.size();
        Log.d(TAG, "mapSize:" + mapSize);

        List<String> xValues = (new ArrayList<>(timeConfidence.keySet()));
        List<Integer> yValues = new ArrayList<>(timeConfidence.values());
        Log.d(TAG, "xValues: " + xValues);
        Log.d(TAG, "yValues: " + yValues);

        DataPoint[] points = new DataPoint[mapSize];
        //List<DataPoint> forGraph = new ArrayList<>();

        for(int i = 0; i < mapSize; i++){
            DataPoint p = new DataPoint(Integer.parseInt(xValues.get(i)), yValues.get(i));
            points[i] = p;
        }

        return new LineGraphSeries<>(points);
    }


   public ArrayList<DateText> buildRecords(){
        //  What this does is build an arrayList of dateTexts that will fill the recycler view

        //get names of all documents, make them
        //work on graph



        ArrayList<DateText> res = new ArrayList<>();
        //res.add(new DateText())
        //query that gets start dates of all sleep data  //get all documents that are start dates
       database.collection(usr)
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               String unix = document.getId();
                               Log.d(TAG, unix + "");
                               res.add(unixToDate(Long.parseLong(unix)));

                           }
                           Log.d("rescheck", res + "");
                       } else {
                           Log.d(TAG, "Error getting documents: ", task.getException());
                       }
                   }
               });




       return res;
    }

    public DateText unixToDate(Long unix){

        Date date = new Date(unix * 1000L);

        SimpleDateFormat setDf = new SimpleDateFormat("MM/dd/yyyy");

        TimeZone setTimeZone = TimeZone.getTimeZone("America/New_York");
        setDf.setTimeZone( setTimeZone );

        String newDate = setDf.format(date);

        return new DateText(newDate);
    }

}






