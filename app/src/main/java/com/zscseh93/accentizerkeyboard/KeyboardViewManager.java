package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

import java.util.List;

/**
 * Created by zscse on 2016. 05. 08..
 */
public class KeyboardViewManager {

    private static final String LOG_TAG = "KeyboardViewManager";

    private AccentizerKeyboardView keyboardView;

    private Mode mode;
    private Type type;
    private boolean isCapitalized;

    private Keyboard qwertzKeyboard;
    private Keyboard qwertzGoKeyboard;
    private Keyboard symbolsKeyboard;
    private Keyboard altSymbolsKeyboard;

    public KeyboardViewManager(Context context, AccentizerKeyboardView keyboardView) {

        mode = Mode.QWERTZ;
        type = Type.DEFAULT;
        isCapitalized = false;

        qwertzKeyboard = new Keyboard(context, R.xml.qwertz);
        qwertzGoKeyboard = new Keyboard(context, R.xml.qwertz_go);
        symbolsKeyboard = new Keyboard(context, R.xml.symbols);
        altSymbolsKeyboard = new Keyboard(context, R.xml.symbols_alt);

        this.keyboardView = keyboardView;
        keyboardView.setKeyboard(qwertzKeyboard);
    }

//    public void updateKeyboardView(Mode newState) {
//        mode = newState;
//
//        switch (mode) {
//            case QWERTZ:
//                keyboardView.setKeyboard(qwertzKeyboard);
//                break;
//            case QWERTZ_GO:
//                keyboardView.setKeyboard(qwertzGoKeyboard);
//                break;
//            case SYMBOLS:
//                keyboardView.setKeyboard(symbolsKeyboard);
//                break;
//            case SYMBOLS_ALT:
//                keyboardView.setKeyboard(altSymbolsKeyboard);
//                break;
//        }
//    }

    public void changeMode() {
        if (mode == Mode.QWERTZ) {
            mode = Mode.SYMBOLS;
            keyboardView.setKeyboard(symbolsKeyboard);
        } else {
            mode = Mode.QWERTZ;
            keyboardView.setKeyboard(qwertzKeyboard);
        }
    }

    public void setType(int imeOptions) {

        switch (imeOptions) {
            case EditorInfo.IME_ACTION_GO:
            case EditorInfo.IME_ACTION_DONE:
            case 234881027:
                type = Type.GO;
                keyboardView.setKeyboard(qwertzGoKeyboard);
                break;
            default:
                type = Type.DEFAULT;
                keyboardView.setKeyboard(qwertzKeyboard);
                break;
        }
    }

    public boolean isCapitalized() {
        return isCapitalized;
    }

    public void handleShift() {

        switch (mode) {
            case QWERTZ:
                isCapitalized = !isCapitalized;
                keyboardView.getKeyboard().setShifted(isCapitalized);
                keyboardView.setCapitalized(isCapitalized);
                keyboardView.invalidateAllKeys();
                break;
            case SYMBOLS:
                mode = Mode.SYMBOLS_ALT;
                keyboardView.setKeyboard(altSymbolsKeyboard);
                break;
            case SYMBOLS_ALT:
                mode = Mode.SYMBOLS;
                keyboardView.setKeyboard(symbolsKeyboard);
                break;
        }
    }

    enum Mode {
        QWERTZ,
        SYMBOLS,
        SYMBOLS_ALT
    }

    enum Type {
        DEFAULT,
        GO
    }
}