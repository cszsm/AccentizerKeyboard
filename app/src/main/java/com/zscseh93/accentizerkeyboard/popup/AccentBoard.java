package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by zscse on 2016. 04. 25..
 */
public abstract class AccentBoard extends LinearLayout {

    private boolean isCapitalized = false;

    public AccentBoard(Context context) {
        super(context);
    }

    public abstract List<PositionedKey> getAccents();

    public abstract void update(char c);

    public abstract boolean isAccentizable(char c);

    public boolean isCapitalized() {
        return isCapitalized;
    }

    public void setCapitalized(boolean capitalized) {
        isCapitalized = capitalized;
    }
}