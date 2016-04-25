package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.widget.PopupWindow;

import java.util.List;

/**
 * Created by zscse on 2016. 04. 21..
 */
public class KeyPopup extends PopupWindow implements KeyContainer {

    private AccentBoard accentBoard;

    public KeyPopup(final Context context, AccentBoard accentBoard) {
        super(context);

        this.accentBoard = accentBoard;
        setContentView(accentBoard);
    }

    // Returns true if the selected letter has accents
    public boolean updatePopup(char c) {
        if (accentBoard.isAccentizable(c)) {
            accentBoard.update(c);
            return true;
        }
        return false;
    }

    @Override
    public List<PositionedKey> getKeys() {
        return accentBoard.getAccents();
    }
}