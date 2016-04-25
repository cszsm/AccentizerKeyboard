package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by zscse on 2016. 04. 25..
 */
public class PopupKeyCreator {

    public TextView create(Context context, String s) {
        TextView textView = new TextView(context);
        textView.setTextSize(32);
        textView.setPadding(6, 0, 6, 0);

        textView.setText(s);
        return textView;
    }
}
