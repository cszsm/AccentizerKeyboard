package com.zscseh93.accentizerkeyboard;

import android.util.Log;
import android.view.inputmethod.InputConnection;

/**
 * Created by zscse on 2016. 04. 13..
 */
public class TextInputConnection {
    private InputConnection inputConnection;

    private int cursorPosition;
    private int wordStartPosition;
    private int wordEndPosition;

    private final String LOG_TAG = "TextInputConnection";

    public TextInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    // Returns true if the cursor replaced onto another word
    public boolean updateCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
        Log.d(LOG_TAG, "pos: " + cursorPosition);
        return !(wordStartPosition <= cursorPosition && cursorPosition <= wordEndPosition);
    }

    // Todo: talán frissíteni kell az inputconnectiont előtte és megnézni, hogy null-e
    public String getCurrentWord() {
        Log.d(LOG_TAG, "updateCurrentWord");
        int beforeLength = 1;


        if (inputConnection.getTextBeforeCursor(beforeLength, 0) == null) {
            Log.d(LOG_TAG, "getTextBeforeCursor is null");
            return "";
        }

        String textBeforeCursor = getWordBeforeCursor();
        String textAfterCursor = getWordAfterCursor();

        wordStartPosition = cursorPosition - textBeforeCursor.length();
        wordEndPosition = cursorPosition + textAfterCursor.length();

        Log.d(LOG_TAG, wordStartPosition + " : " + wordEndPosition);

        Log.d(LOG_TAG, "current word: _" + textBeforeCursor + textAfterCursor + "_");
        return textBeforeCursor + textAfterCursor;
    }

    // Todo: talán updatelni kell az inputconnectiont
    public void replaceCurrentWord(String newWord) {
        inputConnection.deleteSurroundingText(getWordBeforeCursor().length(), getWordAfterCursor
                ().length());
        inputConnection.commitText(newWord, 0);
    }

    // Used for deaccentizing the last word on backspace
    public String getPreviousWord() {
        /* Starting position is 2 before the cursor therefore the checking of first character is
        skipped, so if it is space the reading of input will continue */
        return getWordBeforePosition(2);
    }

    // Used for auto-accentizing on space
    public String getWordBeforeCursor() {
        return getWordBeforePosition(1);
    }

    private String getWordBeforePosition(int beforeLength) {
        if (inputConnection.getTextBeforeCursor(beforeLength, 0) == null) {
            return "";
        }

        String textBeforeCursor = inputConnection.getTextBeforeCursor(beforeLength, 0).toString();

        while (textBeforeCursor.length() == beforeLength) {
            if (!textBeforeCursor.substring(0, 1).matches("\\s+")) {
                beforeLength++;
                textBeforeCursor = inputConnection.getTextBeforeCursor(beforeLength, 0).toString();
            } else {
                textBeforeCursor = textBeforeCursor.substring(1);
            }
        }

        return textBeforeCursor;
    }

    private String getWordAfterCursor() {
        int afterLength = 1;

        String textAfterCursor = inputConnection.getTextAfterCursor(afterLength, 0).toString();

        while (textAfterCursor.length() == afterLength) {
            if (!textAfterCursor.substring(textAfterCursor.length() - 1, textAfterCursor.length()
            ).matches("\\s+")) {
                afterLength++;
                textAfterCursor = inputConnection.getTextAfterCursor(afterLength, 0).toString();
            } else {
                textAfterCursor = textAfterCursor.substring(0, textAfterCursor.length() - 1);
            }
        }

        return textAfterCursor;
    }
}
