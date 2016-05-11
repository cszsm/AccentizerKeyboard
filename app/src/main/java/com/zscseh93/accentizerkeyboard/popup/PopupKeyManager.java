package com.zscseh93.accentizerkeyboard.popup;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by zscse on 2016. 04. 25..
 */
public class PopupKeyManager {

    private KeyContainer keyContainer;
    private static final String LOG_TAG = "PopupKeyManager";

    public PopupKeyManager(KeyContainer keyContainer) {
        this.keyContainer = keyContainer;
    }

    public char handleMotion(MotionEvent event, int popupPosition) {
        float x = event.getX() - popupPosition;

        for (PositionedKey key :
                keyContainer.getKeys()) {
            if (key.getStart() < x && x < key.getEnd()) {
                // TODO: értelmesen jelezni az aktuális betűt
                Log.d(LOG_TAG, String.valueOf(key.getKey()));
                return key.getKey();
            }
        }

        // TODO
        return '0';
    }

    public Character handleRelease(MotionEvent event, int popupPosition) {
        float x = event.getX() - popupPosition;

        for (PositionedKey key :
                keyContainer.getKeys()) {
            if (key.getStart() < x && x < key.getEnd()) {
                return key.getKey();
            }
        }

        return null;
    }
}
