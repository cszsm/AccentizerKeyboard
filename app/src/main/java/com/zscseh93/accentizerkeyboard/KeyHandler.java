package com.zscseh93.accentizerkeyboard;

import android.view.inputmethod.InputConnection;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 31..
 */
public class KeyHandler {

    private InputConnection inputConnection;
    private Accentizer accentizer;

    public KeyHandler(InputConnection inputConnection, Accentizer accentizer) {
        this.inputConnection = inputConnection;
        this.accentizer = accentizer;
    }

    public void handleDelete() {
        inputConnection.deleteSurroundingText(1, 0);
    }

    public void handleSpace(String currentWord) {
        String suggestion = accentizer.accentize(currentWord);
        inputConnection.deleteSurroundingText(suggestion.length(), 0);
        inputConnection.commitText(suggestion, 0);
    }
}
