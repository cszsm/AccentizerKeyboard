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

    public void handleMotion(MotionEvent event, int popupPosition) {
        float x = event.getX() - popupPosition;

        for (PositionedKey key :
                keyContainer.getKeys()) {
            if (key.getStart() < x && x < key.getEnd()) {
                // TODO: értelmesen jelezni az aktuális betűt
                Log.d(LOG_TAG, String.valueOf(key.getKey()));
            }
        }
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

    public int getWidth() {
        int width = 0;
        for (PositionedKey key :
                keyContainer.getKeys()) {
            // TODO
            width += (key.getEnd() - key.getStart());
        }
        return width;
    }
}
