package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.node.POJONode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zscse on 2016. 04. 25..
 */
public class HunAccentBoard extends AccentBoard {
    

    private List<TextView> aAccents;
    private List<TextView> eAccents;
    private List<TextView> iAccents;
    private List<TextView> oAccents;
    private List<TextView> uAccents;

    private LinearLayout layout;

    private PopupKeyCreator keyCreator;

    // TODO
    private char state;

    public HunAccentBoard(Context context) {
        super(context);

        keyCreator = new PopupKeyCreator();

        initA(context);
        initE(context);
        initI(context);
        initO(context);
        initU(context);

        layout = new LinearLayout(context);
    }

    private void initA(Context context) {
        aAccents = new ArrayList<>();
        aAccents.add(keyCreator.create(context, "Á"));
    }

    private void initE(Context context) {
        eAccents = new ArrayList<>();
        eAccents.add(keyCreator.create(context, "É"));
    }

    private void initI(Context context) {
        iAccents = new ArrayList<>();
        iAccents.add(keyCreator.create(context, "Í"));
    }

    private void initO(Context context) {
        oAccents = new ArrayList<>();
        oAccents.add(keyCreator.create(context, "Ó"));
        oAccents.add(keyCreator.create(context, "Ö"));
        oAccents.add(keyCreator.create(context, "Ő"));
    }

    private void initU(Context context) {
        uAccents = new ArrayList<>();
        uAccents.add(keyCreator.create(context, "Ú"));
        uAccents.add(keyCreator.create(context, "Ü"));
        uAccents.add(keyCreator.create(context, "Ű"));
    }

    @Override
    public List<PositionedKey> getAccents() {
        switch (state) {
            case 'a':
                return wrapToKeyList(aAccents);
            case 'e':
                return wrapToKeyList(eAccents);
            case 'i':
                return wrapToKeyList(iAccents);
            case 'o':
                return wrapToKeyList(oAccents);
            case 'u':
                return wrapToKeyList(uAccents);
            default:
                return null;
        }
    }

    // TODO legyen más a neve
    private List<PositionedKey> wrapToKeyList(List<TextView> list) {
        List<PositionedKey> keys = new ArrayList<>();
        for (TextView view:
                list) {
            keys.add(new PositionedKey(view.getText().charAt(0), view.getX(), view.getWidth()));
        }
        return keys;
    }

    @Override
    public void update(char c) {
        state = c;

        removeAllViews();

        switch (state) {
            case 'a':
                for (TextView view :
                        aAccents) {
                    addView(view);
                }
                break;
            case 'e':
                for (TextView view :
                        eAccents) {
                    addView(view);
                }
                break;
            case 'i':
                for (TextView view :
                        iAccents) {
                    addView(view);
                }
                break;
            case 'o':
                for (TextView view :
                        oAccents) {
                    addView(view);
                }
                break;
            case 'u':
                for (TextView view :
                        uAccents) {
                    addView(view);
                }
                break;
        }
    }
}
