package com.zscseh93.accentizerkeyboard;

import android.util.Log;
import android.view.inputmethod.InputConnection;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.zscseh93.accentizerkeyboard.rules.AccentizerRule;
import com.zscseh93.accentizerkeyboard.rules.EmailRule;
import com.zscseh93.accentizerkeyboard.rules.HashtagRule;
import com.zscseh93.accentizerkeyboard.rules.URLRule;

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

    private List<AccentizerRule> rules;

    private boolean isSendingEnabled = true;

    public KeyHandler(InputConnection inputConnection, Accentizer accentizer, Firebase firebase) {
        this.inputConnection = inputConnection;
        this.accentizer = accentizer;

        state = State.WRITING;
        Log.d(STATE_TAG, "WRITING");

        this.firebase = firebase;

        initializeRules();
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public void handleSpace(String currentWord) {
        Log.d(LOG_TAG, "handleSpace");
        switch (state) {
            // TODO: ezt lehetne szebben
            case WRITING:

                // TODO: ezmi?
                if (currentWord.length() == 0) {
                    break;
                }

                /* Checking the rules */
                boolean isAccentizable = true;
                for (AccentizerRule rule :
                        rules) {
                    if (!rule.isAccentizable(currentWord)) {
                        isAccentizable = false;
                        Log.d(LOG_TAG, "_" + currentWord + "_");
                        Log.d(LOG_TAG, "!rule");
                    }
                }

                /* If the last word has accented characters (which means that the user has
                modified it), it must not be accentized. */
                if (isAccentizable && currentWord.equals(accentizer.deaccentize(currentWord))) {
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

                tryToSaveWord(currentWord);

                break;
            case ACCENTIZING_OFF:
                state = State.WRITING;
                Log.d(STATE_TAG, "WRITING");
                break;
        }
    }

    public void handleBackspace(final String currentWord) {
        Log.d(LOG_TAG, "handleBackspace");

        // If there is no characters before the cursor, or the last character is space, the new
        // word has to be accentized on space
//        CharSequence previousCharacter = inputConnection.getTextBeforeCursor(1, 0);
//        if(previousCharacter == null || previousCharacter.equals(" ")) {
//            state = State.WRITING;
//            Log.d(STATE_TAG, "WRITING");
//            return;
//        }
        String previousCharacter = inputConnection.getTextBeforeCursor(1, 0).toString();

        switch (state) {
            case WRITING:
                setState(State.WRITING);
                break;
            case ACCENTIZED:
                setState(State.BAD_SUGGESTION);

                Log.d(LOG_TAG, "_" + currentWord + "_");
                // itt kell menteni a rossz javaslatot

                tryToSaveWord(currentWord);

                inputConnection.beginBatchEdit();
                inputConnection.deleteSurroundingText(currentWord.length(), 0);
                inputConnection.commitText(accentizer.deaccentize(currentWord), 0);
                inputConnection.endBatchEdit();

                break;
            case BAD_SUGGESTION:

                if (previousCharacter.matches("\\s+") || previousCharacter.equals("")) {
                    setState(State.WRITING);
                } else {
                    setState(State.BAD_SUGGESTION);
                }

                break;
            case ACCENTIZING_OFF:

                if (previousCharacter.matches("\\s+") || previousCharacter.equals("")) {
                    state = State.WRITING;
                    Log.d(STATE_TAG, "WRITING");
                } else {
                    Log.d(STATE_TAG, "ACCENTIZING_OFF");
                }
                break;
        }
    }

    public void handleCursorChange(String currentWord, boolean isWordChanged) {
        Log.d(LOG_TAG, "handleCursorChange");

        /**
         * If there is no characters before the cursor, or the last character is space, the new
         * word has to be accentized on space
         */
        CharSequence previousCharacter = inputConnection.getTextBeforeCursor(1, 0);
        if (previousCharacter == null || previousCharacter.equals(" ")) {
            setState(State.WRITING);
            tryToSaveWord(currentWord);
            return;
        }

        if (isWordChanged) {
            handleCursorChangedToOtherWord(currentWord);
        } else {
            handleCursorChangedInWord();
        }
    }

    public void handleCharacter(char character, boolean isCapitalized) {
        Log.d(LOG_TAG, "handleCharacter");
        switch (state) {
            case WRITING:
                setState(State.WRITING);
                break;
            case ACCENTIZED:
                setState(State.WRITING);
                break;
            case BAD_SUGGESTION:
                setState(State.BAD_SUGGESTION);
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

    private void handleCursorChangedInWord() {
        Log.d(LOG_TAG, "handleCursorChangedInWord");

        switch (state) {
            case WRITING:
                setState(State.WRITING);
                break;
            case ACCENTIZED:
                // After accentizing the current word is an empty string
                break;
            case BAD_SUGGESTION:
                setState(State.BAD_SUGGESTION);
                break;
            case ACCENTIZING_OFF:
                Log.d(STATE_TAG, "ACCENTIZING_OFF");
                break;
        }

    }

    private void handleCursorChangedToOtherWord(String currentWord) {
        Log.d(LOG_TAG, "handleCursorChangedToOtherWord");

        switch (state) {
            case WRITING:
                setState(State.BAD_SUGGESTION);
                break;
            case ACCENTIZED:
                setState(State.BAD_SUGGESTION);
                break;
            case BAD_SUGGESTION:
                setState(State.BAD_SUGGESTION);
                tryToSaveWord(currentWord);
                break;
            case ACCENTIZING_OFF:
                Log.d(STATE_TAG, "ACCENTIZING_OFF");
                break;
        }

    }

    // TODO: értelmes név...
    private void tryToSaveWord(String currentWord) {
        Log.d(LOG_TAG, "tryToSaveWord");

        // The word originally suggested by the accentizer
        String suggestedWord = accentizer.accentize(accentizer.deaccentize(currentWord));

        // If the current word and the suggested word are equal, the current word must not be saved
        boolean isModified = !currentWord.equals(suggestedWord);

        if (isSendingEnabled && isModified) {
            saveWord(currentWord);
        }

        Log.d(LOG_TAG, currentWord);
        Log.d(LOG_TAG, suggestedWord);
    }

    private void saveWord(String currentWord) {
        String suggestedWord = accentizer.accentize(accentizer.deaccentize(currentWord));
        firebase.child(currentWord + " - " + suggestedWord).runTransaction(new Transaction.Handler() {
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

    private void initializeRules() {
        rules = new ArrayList<>();
        rules.add(new HashtagRule());
        rules.add(new EmailRule());
        rules.add(new URLRule());
    }

    // TODO: ezt mindenhova
    private void setState(State newState) {
        state = newState;
        Log.d(STATE_TAG, state.name());
    }
}
