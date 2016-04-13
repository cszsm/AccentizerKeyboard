package com.zscseh93.accentizerkeyboard;

import android.util.Log;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

/**
 * Created by zscse on 2016. 04. 13..
 */
public class TextInputConnection {
    private InputConnection inputConnection;
    private int cursorPosition;
    private final String LOG_TAG = "TextInputConnection";

    public TextInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public void setInputConnection(InputConnection inputConnection) {
        this.inputConnection = inputConnection;
    }

    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public String getCurrentWord(CursorHandler cursorHandler) {
        Log.d(LOG_TAG, "updateCurrentWord");
        int beforeLength = 1;
        int afterLength = 1;

        // Todo: frissíteni az inputconnection előtte és megnézni, hogy null-e
//        inputConnection = getCurrentInputConnection();
//        textInputConnection.setInputConnection(inputConnection);
//        if (inputConnection == null) {
//            Log.d(LOG_TAG, "InputConnection is null");
//            Toast.makeText(AccentizerKeyboard.this, "TODO: EditText has changed without the " +
//                    "keyboard.", Toast
//                    .LENGTH_SHORT).show();
//            return;
//        }


        if (inputConnection.getTextBeforeCursor(beforeLength, 0) == null) {
//            currentWord = "";
            Log.d(LOG_TAG, "getTextBeforeCursor is null");
            return "";
        }

        String textBeforeCursor = getWordBeforeCursor();

//        String textBeforeCursor = inputConnection.getTextBeforeCursor(beforeLength, 0).toString();
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

//        cursorHandler.setWord(cursorPosition - beforeLength + 1, cursorPosition + afterLength - 1);
        cursorHandler.setWord(cursorPosition - textBeforeCursor.length(), cursorPosition + afterLength - 1);

        Log.d(LOG_TAG, "current word: _" + textBeforeCursor + textAfterCursor + "_");
        return textBeforeCursor + textAfterCursor;
    }

    // Used for deaccentizing the last word on backspace
    public String getPreviousWord() {
        // Starting position is 2 before the cursor therefore the checking of first character is
        // skipped, so if it is space the reading of input will continue
        return getWordBeforePosition(2);
    }

    // Used for auto-accentizing on space
    public String getWordBeforeCursor() {
        return getWordBeforePosition(1);
    }

    // Todo: kicserélni a függényeket
    public void replaceCurrentWord(String newWord) {

        //Todo: updatelni...
//        updateInputConnection();

        String textBeforeCursor = inputConnection.getTextBeforeCursor(1, 0).toString();
        while (textBeforeCursor.length() > 0 && !textBeforeCursor.matches("\\s+")) {
            inputConnection.deleteSurroundingText(1, 0);
            textBeforeCursor = inputConnection.getTextBeforeCursor(1, 0).toString();
        }

        String textAfterCursor = inputConnection.getTextAfterCursor(1, 0).toString();
        while (textAfterCursor.length() > 0 && !textAfterCursor.matches("\\s+")) {
            inputConnection.deleteSurroundingText(0, 1);
            textAfterCursor = inputConnection.getTextAfterCursor(1, 0).toString();
        }

        inputConnection.commitText(newWord, 0);
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
}
