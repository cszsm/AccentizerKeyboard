package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.zscseh93.accentizerkeyboard.popup.AccentBoard;
import com.zscseh93.accentizerkeyboard.popup.HunAccentBoard;
import com.zscseh93.accentizerkeyboard.popup.KeyPopup;
import com.zscseh93.accentizerkeyboard.popup.PopupKeyManager;

/**
 * Created by zscse on 2016. 04. 20..
 */
public class AccentizerKeyboardView extends KeyboardView {

    private static final String LOG_TAG = "AccentizerKeyboardView";

    private PopupKeyManager popupKeyManager;

    private KeyPopup popupWindow;
    private LinearLayout linearLayout;

    private int popupPosition;

    public AccentizerKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        linearLayout = new LinearLayout(context);

        AccentBoard accentBoard = new HunAccentBoard(context);
        popupWindow = new KeyPopup(context, accentBoard);

        popupKeyManager = new PopupKeyManager(popupWindow);
    }

    @Override
    protected boolean onLongPress(Keyboard.Key popupKey) {
        linearLayout.removeAllViews();

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

                    Character c = popupKeyManager.handleRelease(me, popupPosition);
                    if (c != null) {
                        getOnKeyboardActionListener().onKey(c, new int[]{c, -1, -1, -1, -1, -1,
                                -1, -1,
                                -1, -1, -1, -1});
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (popupWindow.isShowing()) {
                    popupKeyManager.handleMotion(me, popupPosition);
                }
                break;
        }
        return super.onTouchEvent(me);
    }
}
