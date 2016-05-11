package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.zscseh93.accentizerkeyboard.popup.AccentBoard;
import com.zscseh93.accentizerkeyboard.popup.HunAccentBoard;
import com.zscseh93.accentizerkeyboard.popup.KeyPopup;
import com.zscseh93.accentizerkeyboard.popup.PopupKeyManager;

import java.util.List;

/**
 * Created by zscse on 2016. 04. 20..
 */
public class AccentizerKeyboardView extends KeyboardView {

    private static final String LOG_TAG = "AccentizerKeyboardView";

    private PopupKeyManager popupKeyManager;

    private KeyPopup popupWindow;
    // TODO: kell ez ide? lehetne a popupwindowban is...
    private AccentBoard accentBoard;
    private LinearLayout linearLayout;

    private int popupPosition;

    private int screenWidth;

    public AccentizerKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        linearLayout = new LinearLayout(context);

        accentBoard = new HunAccentBoard(context);
        popupWindow = new KeyPopup(context, accentBoard);

        popupKeyManager = new PopupKeyManager(popupWindow);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
    }

    @Override
    protected boolean onLongPress(Keyboard.Key popupKey) {
        linearLayout.removeAllViews();

        if (popupKey.repeatable) {
            return false;
        }

        boolean hasAccents = popupWindow.updatePopup((char) popupKey.codes[0]);
        if (hasAccents) {
            popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, 0, 0);
            popupWindow.update(popupKey.x, popupKey.y, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            popupPosition = popupKey.x;
        }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        switch (me.getAction()) {
            case MotionEvent.ACTION_UP:
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    accentBoard.dismiss();

                    // TODO: ez kétszer van
                    int realPosition = popupPosition;
                    if (screenWidth - accentBoard.getWidth() < popupPosition) {
                        realPosition = screenWidth - accentBoard.getWidth();
                    }

                    Character c = popupKeyManager.handleRelease(me, realPosition);
                    if (c != null) {
                        getOnKeyboardActionListener().onKey(c, new int[]{c, -1, -1, -1, -1, -1,
                                -1, -1,
                                -1, -1, -1, -1});
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (popupWindow.isShowing()) {

                    int realPosition = popupPosition;
                    if (screenWidth - accentBoard.getWidth() < popupPosition) {
                        realPosition = screenWidth - accentBoard.getWidth();
                    }

                    char currentCharacter = popupKeyManager.handleMotion(me, realPosition);
                    accentBoard.setCurrentLetter(currentCharacter);
                }
                break;
        }
        return super.onTouchEvent(me);
    }

    public void setCapitalized(boolean isCapitalized) {
        accentBoard.setCapitalized(isCapitalized);
    }

    public void setAccentizingOn(boolean isAccentizingOn) {
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key :
                keys) {
            if (key.codes[0] == -7) {
                if (isAccentizingOn) {
                    key.label = "ON";
                } else {
                    key.label = "OFF";
                }
            }
        }
    }
}
