package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zscse on 2016. 04. 25..
 */
public interface AccentBoard {
    public List<TextView> getAccents(char c);
    public LinearLayout getAccentBoardLayout(char c);
}
