package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zscse on 2016. 04. 25..
 */
public class HunAccentBoard extends View implements AccentBoard {
    private List<TextView> aAccents;
    private List<TextView> eAccents;
    private List<TextView> iAccents;
    private List<TextView> oAccents;
    private List<TextView> uAccents;

    private LinearLayout layout;

    public HunAccentBoard(Context context) {
        super(context);

        aAccents = initA(context);

        layout = new LinearLayout(context);
    }

    private List<TextView> initA(Context context) {
        TextView tvA = new TextView(context);
        tvA.setText("Á");

        List<TextView> listA = new ArrayList<>();
        listA.add(tvA);
        return listA;
    }

    @Override
    public List<TextView> getAccents(char c) {
        switch (c) {
            case 'a':
                return aAccents;
            case 'e':
                return eAccents;
            case 'i':
                return iAccents;
            case 'o':
                return oAccents;
            case 'u':
                return uAccents;
            default:
                return null;
        }
    }

    @Override
    public LinearLayout getAccentBoardLayout(char c) {
        layout.removeAllViews();
        switch (c) {
            case 'a':
                for (TextView view :
                        aAccents) {
                    layout.addView(view);
                }
                break;
            // TODO
        }
        return layout;
    }
}
