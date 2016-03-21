package com.zscseh93.accentizerkeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;

import java.io.IOException;

/**
 * Created by zscse on 2016. 03. 16..
 */
public class AccentizerKeyboard extends InputMethodService implements KeyboardView
        .OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard qwertzKeyboard;
    private Keyboard symbolsKeyboard;
    private Keyboard symbolsAltKeyboard;

    private HunAccentizer accentizer;

    private boolean caps = false;
    private boolean isAccentizingOn = true;

    private CandidateView candidateView;
    private String currentWord = "";

    private static final String LOG_TAG = "keyboard";

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);

        qwertzKeyboard = new Keyboard(this, R.xml.qwertz);
        symbolsKeyboard = new Keyboard(this, R.xml.symbols);
        symbolsAltKeyboard = new Keyboard(this, R.xml.symbols_alt);

        keyboardView.setKeyboard(qwertzKeyboard);
        keyboardView.setOnKeyboardActionListener(this);

        try {
            accentizer = new HunAccentizer(getResources());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        InputConnection inputConnection = getCurrentInputConnection();
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                inputConnection.deleteSurroundingText(1, 0);
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
//                String suggestion = candidateView.getSuggestion();
                if (isAccentizingOn) {
                    accentize(inputConnection);
                }
                inputConnection.commitText(" ", 1);
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                inputConnection.commitText(String.valueOf(code), 1);
        }
//        candidateView.setSuggestion(currentWord);
    }

    private void accentize(InputConnection inputConnection) {
        String suggestion = accentizer.getSuggestion(currentWord);
        inputConnection.deleteSurroundingText(suggestion.length(), 0);
        inputConnection.commitText(suggestion, 0);
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

//    @Override
//    public View onCreateCandidatesView() {
//        try {
//            candidateView = new CandidateView(this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        setCandidatesViewShown(true);
//
//        return candidateView;
//    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart,
                candidatesEnd);

        CharSequence text = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest
                (), 0)
                .text;

//        candidateView.setSuggestion(getCurrentWord(text.toString(), newSelStart));
        currentWord = getCurrentWord(text.toString(), newSelStart);
    }

    private String getCurrentWord(String text, int cursor) {
        String[] words = text.split(" ");

        for (String word :
                words) {

            cursor -= word.length();
            if (cursor < 1) {
                return word;
            }
            cursor--; // subtract spaces
        }

        return "";
    }
}
