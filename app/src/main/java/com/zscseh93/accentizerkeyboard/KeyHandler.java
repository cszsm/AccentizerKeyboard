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

    private final String LOG_TAG = "KeyHandler";
    private final String STATE_TAG = "KeyHandlerState";

    public KeyHandler(InputConnection inputConnection, Accentizer accentizer) {
        this.inputConnection = inputConnection;
        this.accentizer = accentizer;

        state = State.WRITING;
        Log.d(STATE_TAG, "WRITING");
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public void handleDelete(String currentWord) {
        Log.d(LOG_TAG, "handleDelete");
        switch (state) {
            case WRITING:
                Log.d(STATE_TAG, "WRITING");
                break;
            case ACCENTIZED:
                state = State.BAD_SUGGESTION;
                Log.d(STATE_TAG, "BAD_SUGGESTION");

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

                if (inputConnection.getTextBeforeCursor(1, 0).toString().matches("\\s+")) {
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
