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
public class AccentizingStateMachine {

    private static final String LOG_TAG = "AccentizingStateMachine";
    private static final String STATE_TAG = "KeyHandlerState";
    private static final String whitespace = "\\s+";
    private InputConnection inputConnection;
    private Accentizer accentizer;
    private State state;
    private SuggestionManager suggestionManager;
    private List<AccentizerRule> rules;

    private boolean isSendingEnabled = true;

    public AccentizingStateMachine(InputConnection inputConnection, Accentizer accentizer, SuggestionManager suggestionManager) {
        this.inputConnection = inputConnection;
        this.accentizer = accentizer;

        setState(State.WRITING);

        this.suggestionManager = suggestionManager;

        initializeRules();
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public void handleSpace(String currentWord) {
        Log.d(LOG_TAG, "handleSpace");
        switch (state) {
            case WRITING:

                // Todo: comment
                if (currentWord.length() == 0) {
                    Log.d(LOG_TAG, "length = 0");
                    break;
                }

                /* If the last word has accented characters (which means that the user has
                modified it), it must not be accentized. */
                if (checkRules(currentWord) && currentWord.equals(accentizer.deaccentize
                        (currentWord))) {
                    setState(State.ACCENTIZED);

                    String suggestion = accentizer.accentize(currentWord);

                    /* Deleting the last word and writing it back accentized */
                    inputConnection.beginBatchEdit();
                    inputConnection.deleteSurroundingText(suggestion.length(), 0);
                    inputConnection.commitText(suggestion, 0);
                    inputConnection.endBatchEdit();
                } else {
                    setState(State.WRITING);
                }

                break;
            case ACCENTIZED:
                setState(State.WRITING);
                break;
            case BAD_SUGGESTION:
                setState(State.WRITING);

                tryToSaveWord(currentWord);
                break;
        }
    }

    private boolean checkRules(String currentWord) {
        boolean isAccentizable = true;
        for (AccentizerRule rule :
                rules) {
            if (!rule.isAccentizable(currentWord)) {
                isAccentizable = false;
            }
        }
        return isAccentizable;
    }

    public void handleBackspace(final String currentWord) {
        Log.d(LOG_TAG, "handleBackspace");

        /* If there are no characters before the cursor, or the last character is space, the new
        word has to be accentized on space */
        String previousCharacter = inputConnection.getTextBeforeCursor(1, 0).toString();

        switch (state) {
            case WRITING:
                setState(State.WRITING);
                break;
            case ACCENTIZED:
                setState(State.BAD_SUGGESTION);

                Log.d(LOG_TAG, "_" + currentWord + "_");

                tryToSaveWord(currentWord);

                inputConnection.beginBatchEdit();
                inputConnection.deleteSurroundingText(currentWord.length(), 0);
                inputConnection.commitText(accentizer.deaccentize(currentWord), 0);
                inputConnection.endBatchEdit();

                break;
            case BAD_SUGGESTION:

                if (previousCharacter.matches(whitespace) || previousCharacter.equals("")) {
                    setState(State.WRITING);
                } else {
                    setState(State.BAD_SUGGESTION);
                }

                break;
        }
    }

    public void handleCursorChange(String previousWord, boolean isWordChanged) {
        Log.d(LOG_TAG, "handleCursorChange");

        /* If there are no characters before the cursor, or the last character is space, the new
        word has to be accentized on space */
        CharSequence previousCharacter = inputConnection.getTextBeforeCursor(1, 0);
        if (previousCharacter == null || previousCharacter.equals("") || previousCharacter
                .toString().matches(whitespace)) {
            setState(State.WRITING);
            tryToSaveWord(previousWord);
            return;
        } else {
            Log.d(LOG_TAG, "_" + previousCharacter + "_");
        }

        if (isWordChanged) {
            handleCursorChangedToOtherWord(previousWord);
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
        }

    }

    private void handleCursorChangedToOtherWord(String previousWord) {
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
                tryToSaveWord(previousWord);
                break;
        }

    }

    // TODO: rename
    private void tryToSaveWord(String currentWord) {
        Log.d(LOG_TAG, "tryToSaveWord");

        String deaccentizedWord = accentizer.deaccentize(currentWord);

        // The word originally suggested by the accentizer
        String suggestedWord = accentizer.accentize(deaccentizedWord);

        // If the current word and the suggested word are equal, the current word must not be saved
        boolean isModified = !currentWord.equals(suggestedWord);

        if (isSendingEnabled && isModified) {
            suggestionManager.saveSuggestion(deaccentizedWord, currentWord, suggestedWord);
        }

        Log.d(LOG_TAG, currentWord);
        Log.d(LOG_TAG, suggestedWord);
    }

    private void initializeRules() {
        rules = new ArrayList<>();
        rules.add(new HashtagRule());
        rules.add(new EmailRule());
        rules.add(new URLRule());
    }

    private void setState(State newState) {
        state = newState;
        Log.d(STATE_TAG, state.name());
    }

    // TODO
    public boolean isAccentizing() {
        if (state != State.BAD_SUGGESTION) {
            return true;
        }
        return false;
    }

    private enum State {
        WRITING,
        ACCENTIZED,
        BAD_SUGGESTION
    }

    public void setSendingEnabled(boolean sendingEnabled) {
        isSendingEnabled = sendingEnabled;
    }
}
