package com.zscseh93.accentizerkeyboard;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

/**
 * Created by zscse on 2016. 05. 10..
 */
public class FirebaseManager {

    private static final String LOG_TAG = "FirebaseManager";
    private Firebase firebase;

    public FirebaseManager(Firebase firebase) {
        this.firebase = firebase;
    }

    public void saveSuggestion(String userSuggestion, String accentizerSuggestion) {

        firebase.child(userSuggestion + " - " + accentizerSuggestion).runTransaction(new Transaction
                .Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.d(LOG_TAG, "Transaction");
                if (mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot
                    dataSnapshot) {
                Log.d(LOG_TAG, "TRANSACTION COMPLETE");
                if (firebaseError != null) {
                    Log.d(LOG_TAG, firebaseError.getMessage());
                }
            }
        });
    }
}
