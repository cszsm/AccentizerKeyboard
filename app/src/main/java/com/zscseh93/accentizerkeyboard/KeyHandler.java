package com.zscseh93.accentizerkeyboard;

import android.util.Log;
import android.view.inputmethod.InputConnection;

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

    private boolean doingThings;
    private int modifications;

    private final String LOG_TAG = "KeyHandler";

    public KeyHandler(InputConnection inputConnection, Accentizer accentizer) {
        this.inputConnection = inputConnection;
        this.accentizer = accentizer;

        doingThings = false;
        modifications = 0;

        state = State.WRITING;
        Log.d(LOG_TAG, "WRITING");
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public boolean isDoingThings() {
        return doingThings;
    }

    public void setDoingThings(boolean doingThings) {
        this.doingThings = doingThings;
    }

    public int getModifications() {
        return modifications;
    }

    public void decreaseModifications() {
        modifications--;
    }

    public void handleDelete(String currentWord) {
        Log.d(LOG_TAG, "handleDelete");
        switch (state) {
            case WRITING:
                Log.d(LOG_TAG, "WRITING");
                break;
            case ACCENTIZED:
                state = State.BAD_SUGGESTION;
                Log.d(LOG_TAG, "BAD_SUGGESTION");

                doingThings = true;
                modifications += 2;
                inputConnection.deleteSurroundingText(currentWord.length(), 0);
                inputConnection.commitText(accentizer.deaccentize(currentWord), 0);

                break;
            case BAD_SUGGESTION:
                state = State.ACCENTIZING_OFF;
                Log.d(LOG_TAG, "ACCENTIZING_OFF");
                break;
            case ACCENTIZING_OFF:

                if (inputConnection.getTextBeforeCursor(1, 0).toString().matches("\\s+")) {
                    state = State.WRITING;
                    Log.d(LOG_TAG, "WRITING");
                } else {
                    Log.d(LOG_TAG, "ACCENTIZING_OFF");
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
                    Log.d(LOG_TAG, "ACCENTIZED");

                    doingThings = true;
                    modifications += 2;
                    Log.d(LOG_TAG, "doingThings set");

                    String suggestion = accentizer.accentize(currentWord);
                    inputConnection.deleteSurroundingText(suggestion.length(), 0);
                    inputConnection.commitText(suggestion, 0);
                    Log.d(LOG_TAG, "text replaced");
                } else {
                    Log.d(LOG_TAG, "WRITING");
                }

                break;
            case ACCENTIZED:
                state = State.WRITING;
                Log.d(LOG_TAG, "WRITING");
                break;
            case BAD_SUGGESTION:
                state = State.WRITING;
                Log.d(LOG_TAG, "WRITING");
                break;
            case ACCENTIZING_OFF:
                state = State.WRITING;
                Log.d(LOG_TAG, "WRITING");
                break;
        }
    }

    public void handleCharacter(char character, boolean isCapitalized) {
        Log.d(LOG_TAG, "handleCharacter");
        switch (state) {
            case WRITING:
                Log.d(LOG_TAG, "WRITING");
                break;
            case ACCENTIZED:
                state = State.WRITING;
                Log.d(LOG_TAG, "WRITING");
                break;
            case BAD_SUGGESTION:
                state = State.WRITING;
                Log.d(LOG_TAG, "WRITING");
                break;
            case ACCENTIZING_OFF:
                Log.d(LOG_TAG, "ACCENTIZING_OFF");
                break;
        }

        if (Character.isLetter(character) && isCapitalized) {
            character = Character.toUpperCase(character);
        }

        inputConnection.commitText(String.valueOf(character), 0);
    }

    public void handleCursorChange(boolean isWordChanged) {
        Log.d(LOG_TAG, "handleCursorChange");
        switch (state) {
            case WRITING:
                if (isWordChanged) {
                    state = State.ACCENTIZING_OFF;
                    Log.d(LOG_TAG, "ACCENTIZING_OFF");
                } else {
                    Log.d(LOG_TAG, "WRITING");
                }
                break;
            case ACCENTIZED:
                if (isWordChanged) {
                    state = State.ACCENTIZING_OFF;
                    Log.d(LOG_TAG, "ACCENTIZING_OFF");
                } else {
                    Log.d(LOG_TAG, "ACCENTIZED");
                }
                break;
            case BAD_SUGGESTION:
                state = State.ACCENTIZING_OFF;
                Log.d(LOG_TAG, "ACCENTIZING_OFF");
                break;
            case ACCENTIZING_OFF:
                Log.d(LOG_TAG, "ACCENTIZING_OFF");
                break;
        }
    }
}
