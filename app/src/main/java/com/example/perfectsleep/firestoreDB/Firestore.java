package com.example.perfectsleep.firestoreDB;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Firestore {

    //listener for authentication in OnClick
    public interface OnAuthenticatedListener {
        void onAuthenticated(boolean success);
    }

    private static Firestore INSTANCE;
    private FirebaseUser user;
    private FirebaseFirestore database;

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

            final FirebaseAuth auth = FirebaseAuth.getInstance();
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

    public void logSleepData(int confidence, long startTime, long timestamp){ //puts data gotten from getSleepData.java
        //Map to add to document
        Map<String, Object> sleepData = new HashMap<>();
        sleepData.put("Sleep Confidence", confidence);
        sleepData.put("Start Time", startTime);
        sleepData.put("Timestamp", timestamp);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        //collection is UID, document is start time, and it sets Map
        database.collection(auth.getCurrentUser().getUid()).document(startTime + "")
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
                });

    }

}
