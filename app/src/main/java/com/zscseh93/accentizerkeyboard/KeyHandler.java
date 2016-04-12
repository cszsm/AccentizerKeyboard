package com.zscseh93.accentizerkeyboard;

import android.util.Log;
import android.view.inputmethod.InputConnection;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import java.util.ArrayList;
import java.util.List;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 31..
 */
public class KeyHandler {

    private enum State {
        WRITING,
        ACCENTIZED,
        BAD_SUGGESTION,
        ACCENTIZING_OFF
    }

    private InputConnection inputConnection;
    private Accentizer accentizer;
    private State state;

    private Firebase firebase;

    private final String LOG_TAG = "KeyHandler";
    private final String STATE_TAG = "KeyHandlerState";

    public KeyHandler(InputConnection inputConnection, Accentizer accentizer, Firebase firebase) {
        this.inputConnection = inputConnection;
        this.accentizer = accentizer;

        state = State.WRITING;
        Log.d(STATE_TAG, "WRITING");

        this.firebase = firebase;
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public void handleDelete(final String currentWord) {
        Log.d(LOG_TAG, "handleDelete");

        // If there is no characters before the cursor, or the last character is space, the new word has to be accentized on space
//        CharSequence previousCharacter = inputConnection.getTextBeforeCursor(1, 0);
//        if(previousCharacter == null || previousCharacter.equals(" ")) {
//            state = State.WRITING;
//            Log.d(STATE_TAG, "WRITING");
//            return;
//        }

        switch (state) {
            case WRITING:
                Log.d(STATE_TAG, "WRITING");
                break;
            case ACCENTIZED:
                state = State.BAD_SUGGESTION;
                Log.d(STATE_TAG, "BAD_SUGGESTION");

                Log.d(LOG_TAG, "_" + currentWord + "_");
                // itt kell menteni a rossz javaslatot
                firebase.child(currentWord).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Log.d(LOG_TAG, "Transaction");
                        if(mutableData.getValue() == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue((Long) mutableData.getValue() + 1);
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(LOG_TAG, "TRANSACTION COMPLETE");
                        if (firebaseError != null) {
                            Log.d(LOG_TAG, firebaseError.getMessage());
                        }
                    }
                });

                inputConnection.beginBatchEdit();
                inputConnection.deleteSurroundingText(currentWord.length(), 0);
                inputConnection.commitText(accentizer.deaccentize(currentWord), 0);
                inputConnection.endBatchEdit();

                break;
            case BAD_SUGGESTION:
                state = State.ACCENTIZING_OFF;
                Log.d(STATE_TAG, "ACCENTIZING_OFF");
                break;
            case ACCENTIZING_OFF:

                String previousCharacter = inputConnection.getTextBeforeCursor(1, 0).toString();
                if (previousCharacter.matches("\\s+") || previousCharacter.equals("")) {
                    state = State.WRITING;
                    Log.d(STATE_TAG, "WRITING");
                } else {
                    Log.d(STATE_TAG, "ACCENTIZING_OFF");
                }
                break;
        }
    }

    public void handleSpace(String currentWord) {
        Log.d(LOG_TAG, "handleSpace");
        switch (state) {
            case WRITING:

                /* If the last word has accented characters (which means that the user has
                modified it), it must not be accentized. */
                if (currentWord.equals(accentizer.deaccentize(currentWord))) {
                    state = State.ACCENTIZED;
                    Log.d(STATE_TAG, "ACCENTIZED");

                    String suggestion = accentizer.accentize(currentWord);

                    inputConnection.beginBatchEdit();
                    inputConnection.deleteSurroundingText(suggestion.length(), 0);
                    inputConnection.commitText(suggestion, 0);
                    inputConnection.endBatchEdit();
                } else {
                    Log.d(STATE_TAG, "WRITING");
                }

                break;
            case ACCENTIZED:
                state = State.WRITING;
                Log.d(STATE_TAG, "WRITING");
                break;
            case BAD_SUGGESTION:
                state = State.WRITING;
                Log.d(STATE_TAG, "WRITING");
                break;
            case ACCENTIZING_OFF:
                state = State.WRITING;
                Log.d(STATE_TAG, "WRITING");
                break;
        }
    }

    public void handleCharacter(char character, boolean isCapitalized) {
        Log.d(LOG_TAG, "handleCharacter");
        switch (state) {
            case WRITING:
                Log.d(STATE_TAG, "WRITING");
                break;
            case ACCENTIZED:
                state = State.WRITING;
                Log.d(STATE_TAG, "WRITING");
                break;
            case BAD_SUGGESTION:
                state = State.ACCENTIZING_OFF;
                Log.d(STATE_TAG, "ACCENTIZING_OFF");
                break;
            case ACCENTIZING_OFF:
                Log.d(STATE_TAG, "ACCENTIZING_OFF");
                break;
        }

        if (Character.isLetter(character) && isCapitalized) {
            character = Character.toUpperCase(character);
        }

        inputConnection.commitText(String.valueOf(character), 0);
    }

    public void handleCursorChange(boolean isWordChanged) {
        Log.d(LOG_TAG, "handleCursorChange");

        // If there is no characters before the cursor, or the last character is space, the new word has to be accentized on space
        CharSequence previousCharacter = inputConnection.getTextBeforeCursor(1, 0);
        if(previousCharacter == null || previousCharacter.equals(" ")) {
            state = State.WRITING;
            Log.d(STATE_TAG, "WRITING");
            return;
        }

        switch (state) {
            case WRITING:
                if (isWordChanged) {
                    state = State.ACCENTIZING_OFF;
                    Log.d(STATE_TAG, "ACCENTIZING_OFF");
                } else {
                    Log.d(STATE_TAG, "WRITING");
                }
                break;
            case ACCENTIZED:
                if (isWordChanged) {
                    state = State.ACCENTIZING_OFF;
                    Log.d(STATE_TAG, "ACCENTIZING_OFF");
                } else {
                    Log.d(STATE_TAG, "ACCENTIZED");
                }
                break;
            case BAD_SUGGESTION:
                state = State.ACCENTIZING_OFF;
                Log.d(STATE_TAG, "ACCENTIZING_OFF");
                break;
            case ACCENTIZING_OFF:
                Log.d(STATE_TAG, "ACCENTIZING_OFF");
                break;
        }
    }
}
