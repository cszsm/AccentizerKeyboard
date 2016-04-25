package com.zscseh93.accentizerkeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;

import com.firebase.client.Firebase;

import java.io.IOException;
import java.util.List;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 16..
 */
public class AccentizerKeyboard extends InputMethodService implements KeyboardView
        .OnKeyboardActionListener {

    private AccentizerKeyboardView keyboardView;
    private Keyboard qwertzKeyboard;
    private Keyboard qwertzGoKeyboard;
    private Keyboard symbolsKeyboard;
    private Keyboard symbolsAltKeyboard;

    private Accentizer accentizer;

    private boolean isCapitalized = false;
    private boolean isAccentizingOn = true;

    private CandidateView candidateView;
    private String currentWord = "";

    private static final String LOG_TAG = "AccentizerKeyboard";

    private KeyHandler keyHandler;
    private InputConnection inputConnection;
    private TextInputConnection textInputConnection;

    private boolean wasEvent = false;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate();

        inputConnection = getCurrentInputConnection();
        textInputConnection = new TextInputConnection(inputConnection);

        AccentizerCreator accentizerCreator;
        try {
            accentizerCreator = new AccentizerCreator(getResources());
            accentizer = accentizerCreator.getAccentizer();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Firebase.setAndroidContext(this);
        Firebase firebase = new Firebase("https://glowing-torch-1852.firebaseio" +
                ".com/wrong-suggestions");

        keyHandler = new KeyHandler(inputConnection, accentizer, firebase);
    }

    @Override
    public View onCreateInputView() {
        Log.d(LOG_TAG, "onCreateInputView");

        keyboardView = (AccentizerKeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);

        qwertzKeyboard = new Keyboard(this, R.xml.qwertz);
        qwertzGoKeyboard = new Keyboard(this, R.xml.qwertz_go);
        symbolsKeyboard = new Keyboard(this, R.xml.symbols);
        symbolsAltKeyboard = new Keyboard(this, R.xml.symbols_alt);

        keyboardView.setKeyboard(qwertzKeyboard);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setPreviewEnabled(false);

        return keyboardView;
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d(LOG_TAG, "onCreateCandidatesView");
        try {
            candidateView = new CandidateView(this, textInputConnection, accentizer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setCandidatesViewShown(true);
        return candidateView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.d(LOG_TAG, "onStartInputView");
        super.onStartInputView(info, restarting);

        inputConnection = getCurrentInputConnection();
        textInputConnection.setInputConnection(inputConnection);
        ExtractedText extractedText = inputConnection.getExtractedText(new ExtractedTextRequest()
                , 0);

        if (extractedText != null) {
            textInputConnection.updateCursorPosition(extractedText.selectionStart);
            currentWord = textInputConnection.getCurrentWord(/*cursorHandler*/);
            candidateView.setCurrentWord(currentWord);
        }

        Log.d(LOG_TAG, "ime options: " + String.valueOf(info.imeOptions));
        if (info.imeOptions == EditorInfo.IME_ACTION_GO || info.imeOptions == EditorInfo.IME_ACTION_DONE) {
            keyboardView.setKeyboard(qwertzGoKeyboard);
        } else {
            keyboardView.setKeyboard(qwertzKeyboard);
        }
    }

    @Override
    public void onFinishInput() {
        Log.d(LOG_TAG, "onFinishInput");
        super.onFinishInput();
        currentWord = "";
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        Log.d(LOG_TAG, "onUpdateSelection");
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart,
                candidatesEnd);

        updateInputConnection();

//        boolean b = cursorHandler.isWordChanged(newSelStart);
        boolean isWordChanged = textInputConnection.updateCursorPosition(newSelStart);
        currentWord = textInputConnection.getCurrentWord(/*cursorHandler*/);

        if (!wasEvent) {
            keyHandler.handleCursorChange(isWordChanged);
        }
        wasEvent = false;

        Log.d(LOG_TAG, "previous word: " + textInputConnection.getPreviousWord());
        Log.d(LOG_TAG, "word before cursor: " + textInputConnection.getWordBeforeCursor());

        if (candidateView != null) {
            candidateView.setCurrentWord(currentWord);
        }
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
                keyHandler.handleDelete(textInputConnection.getPreviousWord());

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
            case -8:
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_GO);
            case ' ':
            case '\n':
//                String suggestion = candidateView.getSuggestion();

                if (isAccentizingOn) {
                    keyHandler.handleSpace(textInputConnection.getWordBeforeCursor());
                }

                inputConnection.commitText(String.valueOf((char) primaryCode), 0);
                break;
            default:
                keyHandler.handleCharacter((char) primaryCode, isCapitalized);
                break;
        }


        List<Keyboard.Key> keys = qwertzKeyboard.getKeys();
        for (Keyboard.Key key :
                keys) {
            if (key.codes[0] == -7) {
//                Log.d(LOG_TAG, "-7");
                if (isAccentizingOn) {
//                    Log.d(LOG_TAG, "ON");
                    key.label = "ON";
                } else {
//                    Log.d(LOG_TAG, "OFF");
                    key.label = "OFF";
                }
            }
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

    private void updateInputConnection() {

        inputConnection = getCurrentInputConnection();
        textInputConnection.setInputConnection(inputConnection);

        if (keyHandler != null) {
            keyHandler.setInputConnection(inputConnection);
        }
    }
}
