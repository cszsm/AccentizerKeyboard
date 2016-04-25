package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zscse on 2016. 04. 25..
 */
public abstract class AccentBoard extends LinearLayout {

    public AccentBoard(Context context) {
        super(context);
    }

    public abstract List<PositionedKey> getAccents();
//    public abstract LinearLayout getAccentBoardLayout(char c);
    public abstract void update(char c);
}
