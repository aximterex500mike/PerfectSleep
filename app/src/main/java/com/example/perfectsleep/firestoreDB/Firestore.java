package com.example.perfectsleep.firestoreDB;

import android.app.Activity;
import android.app.TaskInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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


    SleepData sleepData, sdForSeepScore;
    private String  averageSleepScoreResult = "0";
    private long startTime;
    private long endTime;

    //private final Map<Long, Integer> timeConfidence = new HashMap<>();----------------------------------
    private final Map<String, Integer> timeConfidence = new HashMap<>();

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
                            Log.d("UID", "UID: " + auth.getCurrentUser().getUid());
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

    public void startCollecting(Long startTime) {
        Log.d(TAG, "collecting sleep data has started");

        //send start time
        sleepData = new SleepData();
        sleepData.setStartTime(startTime);

        database.collection(usr)
                .document(startTime + "")
                .set(sleepData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Collection initialised");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "ERROR: " + e);
                    }
                });

        //set document reference "date" 
        date = database.collection(usr).document(startTime + "");
        /*database.collection(usr)
                .add(startTime)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "New night of sleep added to database.  ID: " + documentReference.getId());
                })
                .addOnFailureListener( (@NonNull Exception e) -> {
                    Log.d(TAG, "ERROR: " + e.getMessage());
                });*/

    }


    public void logSleepData(int confidence, long timestamp){ //puts data gotten from getSleepData.java
        /*//Map to add to document
        Map<String, Object> sleepData = new HashMap<>();
        sleepData.put("Sleep Confidence", confidence);
        //sleepData.put("Start Time", startTime);  don't need anymore
        sleepData.put("Timestamp", timestamp);*/

        //updating map

        //timeConfidence.put(timestamp, confidence);----------------------------------
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
                
        /*SleepData sData = new SleepData();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        //try to use "where" queries

        //collection is UID, document is startTime, sub-collection is sleepData,
        // sub-document is timestamps and it sets Map
        database.collection(auth.getCurrentUser().getUid()).document(startTime + "")
                .collection("sleepData").document(timestamp + "")
                .set(sleepData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FDB", "Sleep data written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FDB", "Error", e);
                    }
                });*/

    }

    public void endCollecting(long endTime){
        sleepData.setEndTime(endTime);
        database.runTransaction((Transaction.Function<Void>) transaction -> {
            //DocumentSnapshot snapshot = transaction.get(date);

            transaction.update(date, "endTime", sleepData.getEndTime()); //sleepData.getTimeConfidence() should be the latest Map

            // Success
            return null;
        }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success!"))
                .addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e));

        //At this point, a nights worth of sleep data should be in the database
        Log.d(TAG,"endCollecting: At this point, a nights worth of sleep data should be in the database");
    }

    public String getLastSleepScore(){

        sdForSeepScore = new SleepData();

        database.collection(usr)
                .orderBy("startTime", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot qSnapshot = task.getResult();
                        sdForSeepScore = qSnapshot.toObjects(SleepData.class).get(0);

                        //getting sleep score
                        List<Integer> targetList = new ArrayList<>(sdForSeepScore.getTimeConfidence().values());
                        Log.d(TAG, "target list:" +targetList);

                        int size = targetList.size();
                        Log.d(TAG, "size(should be 11):" +size);
                        int sum = 0;
                        for (Integer i : targetList)
                            sum += i;
                        Log.d(TAG, "sum:" + sum);

                        averageSleepScoreResult = decimalFormat.format((double) sum / size);;
                        Log.d(TAG, "return:" + averageSleepScoreResult);
                        //

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        Log.d(TAG, sdForSeepScore.getTimeConfidence().values() + "");
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


        return averageSleepScoreResult;
    }

    public int buildRecords(){
        Calendar.getInstance().getTimeInMillis();
        return 0;
    }

}
