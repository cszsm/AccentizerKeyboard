package com.zscseh93.accentizerkeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

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

    private boolean caps = false;
    private boolean isAccentizingOn = true;

    private CandidateView candidateView;
    private String currentWord = "";
    private String previousWord = "";

    private int cursorPos = 0;

    private static final String LOG_TAG = "keyboard";

    private KeyHandler keyHandler;
    private InputConnection inputConnection;

    @Override
    public View onCreateInputView() {
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
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                keyHandler.handleDelete();
                break;
            case Keyboard.KEYCODE_SHIFT:
                handleShift();
                break;
            case Keyboard.KEYCODE_DONE:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent
                        .KEYCODE_ENTER));
                break;
            case -7:
                isAccentizingOn = !isAccentizingOn;
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
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                inputConnection.commitText(String.valueOf(code), 1);
        }
//        candidateView.setCurrentWord(currentWord);
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
            caps = !caps;
            qwertzKeyboard.setShifted(caps);
            keyboardView.invalidateAllKeys();
        } else if (currentKeyboard == symbolsKeyboard) {
            keyboardView.setKeyboard(symbolsAltKeyboard);
        } else if (currentKeyboard == symbolsAltKeyboard) {
            keyboardView.setKeyboard(symbolsKeyboard);
        }
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
            candidateView = new CandidateView(this, this);
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

        updateCurrentWord();
        candidateView.setCurrentWord(currentWord);
    }

    public void replaceCurrentWord(String newWord) {
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

    private void updateCurrentWord() {
        int beforeLength = 1;
        int afterLength = 1;

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

        while (textAfterCursor.length() == afterLength) {
            if (!textAfterCursor.substring(textAfterCursor.length() - 1, textAfterCursor.length()).matches("\\s+")) {
                afterLength++;
                textAfterCursor = inputConnection.getTextAfterCursor(afterLength, 0).toString();
            } else {
                textAfterCursor = textAfterCursor.substring(0, textAfterCursor.length() - 1);
            }
        }

        currentWord = textBeforeCursor + textAfterCursor;
    }
}
