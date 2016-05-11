package com.zscseh93.accentizerkeyboard;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;

import com.firebase.client.Firebase;
import com.zscseh93.accentizerkeyboard.dictionary.Suggestion;
import com.zscseh93.accentizerkeyboard.dictionary.SuggestionDictionary;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 16..
 */
public class AccentizerKeyboard extends InputMethodService implements KeyboardView
        .OnKeyboardActionListener {

    private static final String LOG_TAG = "AccentizerKeyboard";

    private static final String PREF_DEBUG = "pref_debug";
    private static final String PREF_SEND = "pref_send";

    private AccentizerKeyboardView keyboardView;
    //    private Keyboard qwertzKeyboard;
//    private Keyboard qwertzGoKeyboard;
//    private Keyboard symbolsKeyboard;
//    private Keyboard symbolsAltKeyboard;
    private KeyboardViewManager keyboardViewManager;
    private Accentizer accentizer;
    //    private boolean isCapitalized = false;
    private boolean isAccentizingOn = true;
    private CandidateView candidateView;
    private String currentWord = "";
    private AccentizingStateMachine accentizingStateMachine;
    private InputConnection inputConnection;
    private TextInputConnection textInputConnection;

    private boolean wasEvent = false;

    private boolean isDebugModeOn;

    private SuggestionDictionary suggestionDictionary;

//    private int imeOptions = EditorInfo.IME_ACTION_NONE;

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

        FirebaseManager firebaseManager = new FirebaseManager(firebase);
        suggestionDictionary = new SuggestionDictionary();

        SuggestionManager suggestionManager = new SuggestionManager(firebaseManager, suggestionDictionary);

        accentizingStateMachine = new AccentizingStateMachine(inputConnection, accentizer,
                suggestionManager);
    }

    @Override
    public View onCreateInputView() {
        Log.d(LOG_TAG, "onCreateInputView");

        keyboardView = (AccentizerKeyboardView) getLayoutInflater().inflate(R.layout.keyboard,
                null);

//        qwertzKeyboard = new Keyboard(this, R.xml.qwertz);
//        qwertzGoKeyboard = new Keyboard(this, R.xml.qwertz_go);
//        symbolsKeyboard = new Keyboard(this, R.xml.symbols);
//        symbolsAltKeyboard = new Keyboard(this, R.xml.symbols_alt);
        keyboardViewManager = new KeyboardViewManager(this, keyboardView);

//        keyboardView.setKeyboard(qwertzKeyboard);
//        keyboardView.setKeyboard(keyboardViewManager.updateKeyboardView(KeyboardViewManager
// .Mode.QWERTZ));
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setPreviewEnabled(false);

        return keyboardView;
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d(LOG_TAG, "onCreateCandidatesView");

        Suggestor suggestor = new Suggestor(accentizer, suggestionDictionary);

        try {
            candidateView = new CandidateView(this, textInputConnection, suggestor);
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

//        inputConnection = getCurrentInputConnection();
//        textInputConnection.setInputConnection(inputConnection);
        try {
            updateInputConnection();
        } catch (InputConnectionException e) {
            e.printStackTrace();
            return;
        }

//        if (isConnectionSucceeded) {
            ExtractedText extractedText = inputConnection.getExtractedText(new
                    ExtractedTextRequest(), 0);

            if (extractedText != null) {
                textInputConnection.updateCursorPosition(extractedText.selectionStart);
                currentWord = textInputConnection.getCurrentWord(/*cursorHandler*/);
                candidateView.setCurrentWord(currentWord);
            }
//        }

//        Log.d(LOG_TAG, "ime options: " + String.valueOf(info.imeOptions));
//        if (info.imeOptions == EditorInfo.IME_ACTION_GO || info.imeOptions == EditorInfo
// .IME_ACTION_DONE || info.imeOptions == 234881027) {
////            keyboardView.setKeyboard(qwertzGoKeyboard);
//            keyboardViewManager.updateKeyboardView(KeyboardViewManager.Mode.QWERTZ_GO);
//        } else {
////            keyboardView.setKeyboard(qwertzKeyboard);
//            keyboardViewManager.updateKeyboardView(KeyboardViewManager.Mode.QWERTZ);
//        }
//        imeOptions = info.imeOptions;
        keyboardViewManager.setType(info.imeOptions);

        updatePreferences();


        // innen

        Iterator<Suggestion> db = Suggestion.findAll(Suggestion.class);
        if (db.hasNext()) {
            for (Suggestion suggestion; db.hasNext();) {
                suggestion = db.next();
                Log.d(LOG_TAG + "_ORM", suggestion.getWord() + " - " + suggestion.getSuggestion() + " - " + suggestion.getCount());
            }
        }

        // idáig

//        Suggestion.deleteAll(Suggestion.class);
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

        try {
            updateInputConnection();
        } catch (InputConnectionException e) {
            e.printStackTrace();
            return;
        }

//        boolean b = cursorHandler.isWordChanged(newSelStart);
        boolean isWordChanged = textInputConnection.updateCursorPosition(newSelStart);

        if (!wasEvent) {
            accentizingStateMachine.handleCursorChange(currentWord, isWordChanged);
        }

        currentWord = textInputConnection.getCurrentWord(/*cursorHandler*/);

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

        try {
            updateInputConnection();
        } catch (InputConnectionException e) {
            e.printStackTrace();
            return;
        }

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                inputConnection.deleteSurroundingText(1, 0);
                accentizingStateMachine.handleBackspace(textInputConnection.getPreviousWord());

                break;
            case Keyboard.KEYCODE_SHIFT:
//                handleShift();
                keyboardViewManager.handleShift();
                break;
            case Keyboard.KEYCODE_DONE:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent
                        .KEYCODE_ENTER));
                break;
            case -7:
                isAccentizingOn = !isAccentizingOn;
                keyboardView.setAccentizingOn(isAccentizingOn);
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
//                handleModeChange();
                keyboardViewManager.changeMode();
                break;
            case -8:
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_GO);
            case ' ':
            case '\n':
//                String suggestion = candidateView.getSuggestion();

                if (isAccentizingOn) {
                    accentizingStateMachine.handleSpace(textInputConnection.getWordBeforeCursor());
                }

                inputConnection.commitText(String.valueOf((char) primaryCode), 0);
                break;
            default:
//                accentizingStateMachine.handleCharacter((char) primaryCode, isCapitalized);
                accentizingStateMachine.handleCharacter((char) primaryCode, keyboardViewManager
                        .isCapitalized());

//                Log.d(LOG_TAG, "prim: " + primaryCode + " hossz: " + keyCodes.length);
//                for (int i :
//                        keyCodes) {
//                    Log.d(LOG_TAG, String.valueOf(i));
//                }
                break;
        }

//        Keyboard tmp = null;
//        if (keyboardView.getKeyboard() == qwertzKeyboard) {
//            tmp = qwertzKeyboard;
//        } else {
//            tmp = qwertzGoKeyboard;
//        }
//
//        if (tmp != null) {
//            List<Keyboard.Key> keys = tmp.getKeys();
//            for (Keyboard.Key key :
//                    keys) {
//                if (key.codes[0] == -7) {
////                Log.d(LOG_TAG, "-7");
//                    if (isAccentizingOn) {
////                    Log.d(LOG_TAG, "ON");
//                        key.label = "OFF";
//                    } else {
////                    Log.d(LOG_TAG, "OFF");
//                        key.label = "ON";
//                    }
//                }
//            }
//        }


        if (isDebugModeOn) {
            if (accentizingStateMachine.isAccentizing() && isAccentizingOn) {
                candidateView.setBackground(Color.rgb(138, 194, 73));
            } else {
                candidateView.setBackground(Color.rgb(243, 66, 53));
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

//    private void handleModeChange() {
//        Keyboard currentKeyboard = keyboardView.getKeyboard();
//
//        if (currentKeyboard == qwertzKeyboard) {
//            keyboardView.setKeyboard(symbolsKeyboard);
//        } else {
//            keyboardView.setKeyboard(qwertzKeyboard);
//        }
//    }

//    private void handleShift() {
//        Keyboard currentKeyboard = keyboardView.getKeyboard();
//
//
//        // TODO: layoutok szebben
//        if (currentKeyboard == qwertzKeyboard || currentKeyboard == qwertzGoKeyboard) {
//            isCapitalized = !isCapitalized;
//            qwertzKeyboard.setShifted(isCapitalized);
//            qwertzGoKeyboard.setShifted(isCapitalized);
//            keyboardView.setCapitalized(isCapitalized);
//            keyboardView.invalidateAllKeys();
//            Log.d(LOG_TAG, "qwertzKeyboard");
//        } else if (currentKeyboard == symbolsKeyboard) {
//            keyboardView.setKeyboard(symbolsAltKeyboard);
//        } else if (currentKeyboard == symbolsAltKeyboard) {
//            keyboardView.setKeyboard(symbolsKeyboard);
//        } else {
//            Log.d(LOG_TAG, String.valueOf(currentKeyboard));
//        }
//    }

    private void updateInputConnection() throws InputConnectionException {

        inputConnection = getCurrentInputConnection();
        if (inputConnection == null) {
            throw new InputConnectionException("There is no InputConnection bound to the input method.");
        }
        textInputConnection.setInputConnection(inputConnection);

        if (accentizingStateMachine != null) {
            accentizingStateMachine.setInputConnection(inputConnection);
        }
    }

    private void updatePreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isDebugModeOn = sharedPreferences.getBoolean(PREF_DEBUG, false);
        if (!isDebugModeOn) {
            candidateView.setBackground(ContextCompat.getColor(this, R.color.colorCandidate));
        }

        accentizingStateMachine.setSendingEnabled(sharedPreferences.getBoolean(PREF_SEND, false));
    }
}
