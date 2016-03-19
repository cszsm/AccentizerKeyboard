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
    private Keyboard keyboard;

    private boolean caps = false;

    private CandidateView candidateView;
//    private String currentWord = "";

    private static final String LOG_TAG = "keyboard";

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
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
                caps = !caps;
                keyboard.setShifted(caps);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent
                        .KEYCODE_ENTER));
                break;
            case ' ':
                String suggestion = candidateView.getSuggestion();
                Log.d(LOG_TAG, "_" + suggestion + "_");
                inputConnection.deleteSurroundingText(suggestion.length(), 0);
                inputConnection.commitText(suggestion + " ", 1);
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                inputConnection.commitText(String.valueOf(code), 1);
//                currentWord += code;
        }
//        candidateView.setSuggestion(currentWord);

//        Log.d(LOG_TAG, "onKey");
//        Log.d(LOG_TAG, inputConnection.getTextBeforeCursor(-1, 0).toString());
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
            candidateView = new CandidateView(this);
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

        String text = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0)
                .text.toString();
        candidateView.setSuggestion(getCurrentWord(text, newSelStart));
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
