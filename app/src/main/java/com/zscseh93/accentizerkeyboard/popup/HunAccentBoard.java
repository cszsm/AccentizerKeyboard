package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.node.POJONode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zscse on 2016. 04. 25..
 */
public class HunAccentBoard extends AccentBoard {

    private Map<Character, List<TextView>> accents;

    private PopupKeyCreator keyCreator;

    // TODO
    private char state;

    public HunAccentBoard(Context context) {
        super(context);

        keyCreator = new PopupKeyCreator();
        accents = new HashMap<>();

        initA(context);
        initE(context);
        initI(context);
        initO(context);
        initU(context);
    }

    private void initA(Context context) {
        accents.put('a', new ArrayList<TextView>());
        accents.get('a').add(keyCreator.create(context, "Á"));
    }

    private void initE(Context context) {
        accents.put('e', new ArrayList<TextView>());
        accents.get('e').add(keyCreator.create(context, "É"));
    }

    private void initI(Context context) {
        accents.put('i', new ArrayList<TextView>());
        accents.get('i').add(keyCreator.create(context, "Í"));
    }

    private void initO(Context context) {
        accents.put('o', new ArrayList<TextView>());
        accents.get('o').add(keyCreator.create(context, "Ó"));
        accents.get('o').add(keyCreator.create(context, "Ö"));
        accents.get('o').add(keyCreator.create(context, "Ő"));
    }

    private void initU(Context context) {
        accents.put('u', new ArrayList<TextView>());
        accents.get('u').add(keyCreator.create(context, "Ú"));
        accents.get('u').add(keyCreator.create(context, "Ü"));
        accents.get('u').add(keyCreator.create(context, "Ű"));
    }

    @Override
    public List<PositionedKey> getAccents() {
//        if (accents.keySet().contains(state)) {
            return wrapToKeyList(accents.get(state));
//        }
//        return null;
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

        for (TextView textView :
                accents.get(state)) {
            addView(textView);
        }

//        switch (state) {
//            case 'a':
//                for (TextView view :
//                        aAccents) {
//                    addView(view);
//                }
//                break;
//            case 'e':
//                for (TextView view :
//                        eAccents) {
//                    addView(view);
//                }
//                break;
//            case 'i':
//                for (TextView view :
//                        iAccents) {
//                    addView(view);
//                }
//                break;
//            case 'o':
//                for (TextView view :
//                        oAccents) {
//                    addView(view);
//                }
//                break;
//            case 'u':
//                for (TextView view :
//                        uAccents) {
//                    addView(view);
//                }
//                break;
//        }
    }

    @Override
    public boolean isAccentizable(char c) {
        return accents.keySet().contains(c);
    }
}
