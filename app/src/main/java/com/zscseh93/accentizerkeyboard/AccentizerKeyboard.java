package com.zscseh93.accentizerkeyboard;

import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import java.io.IOException;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 16..
 */
public class AccentizerKeyboard extends InputMethodService implements KeyboardView
        .OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard qwertzKeyboard;
    private Keyboard symbolsKeyboard;
    private Keyboard symbolsAltKeyboard;

//    private AccentizerCreator accentizer;
    private Accentizer accentizer;

    private boolean isCapitalized = false;
    private boolean isAccentizingOn = true;

    private CandidateView candidateView;
    private String currentWord = "";
//    private String previousWord = "";
//
//    private int cursorPos = 0;

    private static final String LOG_TAG = "AccentizerKeyboard";

    private KeyHandler keyHandler;
    private CursorHandler cursorHandler;
    private InputConnection inputConnection;

    private boolean wasEvent = false;

    @Override
    public View onCreateInputView() {
        Log.d(LOG_TAG, "onCreateInputView");
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);

        qwertzKeyboard = new Keyboard(this, R.xml.qwertz);
        symbolsKeyboard = new Keyboard(this, R.xml.symbols);
        symbolsAltKeyboard = new Keyboard(this, R.xml.symbols_alt);

        keyboardView.setKeyboard(qwertzKeyboard);
        keyboardView.setOnKeyboardActionListener(this);

        AccentizerCreator accentizerCreator;
        try {
            accentizerCreator = new AccentizerCreator(getResources());
            accentizer = accentizerCreator.getAccentizer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputConnection = getCurrentInputConnection();
        keyHandler = new KeyHandler(inputConnection, accentizer);
        cursorHandler = new CursorHandler();

        return keyboardView;
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        wasEvent = true;
        Log.d(LOG_TAG, "wasEvent: true");

        updateInputConnection();

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                inputConnection.deleteSurroundingText(1, 0);
                keyHandler.handleDelete(currentWord);

                break;
            case Keyboard.KEYCODE_SHIFT:
                handleShift();
                break;
            case Keyboard.KEYCODE_DONE:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent
                        .KEYCODE_ENTER));
                break;
            case -7:
                if (isAccentizingOn) {

                }
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                handleModeChange();
                break;
            case ' ':
            case '\n':
//                String suggestion = candidateView.getSuggestion();
                if (isAccentizingOn) {
                    keyHandler.handleSpace(currentWord);
                }

                inputConnection.commitText(String.valueOf((char) primaryCode), 0);
                break;
            default:
                keyHandler.handleCharacter((char) primaryCode, isCapitalized);
                break;
        }
//        candidateView.setCurrentWord(currentWord);
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    @Override
    public View onCreateCandidatesView() {
        try {
            candidateView = new CandidateView(this, this, accentizer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setCandidatesViewShown(true);
        return candidateView;
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart,
                candidatesEnd);

        updateInputConnection();

        if(!wasEvent) {
            keyHandler.handleCursorChange(cursorHandler.isWordChanged(newSelStart));
        }
        wasEvent = false;
        Log.d(LOG_TAG, "wasEvent: false");

        updateCurrentWord(newSelStart);

        if(candidateView != null) {
            candidateView.setCurrentWord(currentWord);
        }

        Log.d(LOG_TAG, "onUpdateSelection");
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        Log.d(LOG_TAG, "onStartInputView");
//        updateCurrentWord();
        candidateView.setCurrentWord(currentWord);
    }

    @Override
    public void onFinishInput() {

        super.onFinishInput();
        currentWord = "";
    }

    public void replaceCurrentWord(String newWord) {

        updateInputConnection();

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


    private void handleModeChange() {
        Keyboard currentKeyboard = keyboardView.getKeyboard();

        if (currentKeyboard == qwertzKeyboard) {
            keyboardView.setKeyboard(symbolsKeyboard);
        } else {
            keyboardView.setKeyboard(qwertzKeyboard);
        }
    }

    private void handleShift() {
        Keyboard currentKeyboard = keyboardView.getKeyboard();

        if (currentKeyboard == qwertzKeyboard) {
            isCapitalized = !isCapitalized;
            qwertzKeyboard.setShifted(isCapitalized);
            keyboardView.invalidateAllKeys();
        } else if (currentKeyboard == symbolsKeyboard) {
            keyboardView.setKeyboard(symbolsAltKeyboard);
        } else if (currentKeyboard == symbolsAltKeyboard) {
            keyboardView.setKeyboard(symbolsKeyboard);
        }
    }

    private void updateCurrentWord(int cursorPosition) {
        int beforeLength = 1;
        int afterLength = 1;

        // TODO
        if(inputConnection == null || inputConnection.getTextBeforeCursor(beforeLength, 0) == null) {
            Toast.makeText(AccentizerKeyboard.this, "TODO: EditText has changed without the keyboard.", Toast
                    .LENGTH_SHORT).show();
            return;
        }

        String textBeforeCursor = inputConnection.getTextBeforeCursor(beforeLength, 0).toString();
        String textAfterCursor = inputConnection.getTextAfterCursor(afterLength, 0).toString();

        while (textBeforeCursor.length() == beforeLength) {
            if (!textBeforeCursor.substring(0, 1).matches("\\s+")) {
                beforeLength++;
                textBeforeCursor = inputConnection.getTextBeforeCursor(beforeLength, 0).toString();
            } else {
                textBeforeCursor = textBeforeCursor.substring(1);
            }
        }

        if(beforeLength == 1) {
            cursorHandler.setWord(cursorPosition, cursorPosition);
            currentWord = "";
            return;
        }

        while (textAfterCursor.length() == afterLength) {
            if (!textAfterCursor.substring(textAfterCursor.length() - 1, textAfterCursor.length()).matches("\\s+")) {
                afterLength++;
                textAfterCursor = inputConnection.getTextAfterCursor(afterLength, 0).toString();
            } else {
                textAfterCursor = textAfterCursor.substring(0, textAfterCursor.length() - 1);
            }
        }

        cursorHandler.setWord(cursorPosition - beforeLength + 1, cursorPosition + afterLength - 1);
        currentWord = textBeforeCursor + textAfterCursor;
    }

    private void updateInputConnection() {

        inputConnection = getCurrentInputConnection();

        if (keyHandler != null) {
            keyHandler.setInputConnection(inputConnection);
        }
    }
}
