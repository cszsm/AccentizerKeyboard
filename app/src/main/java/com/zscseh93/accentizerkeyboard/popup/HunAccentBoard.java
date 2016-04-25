package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.widget.TextView;

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

    @Override
    public List<PositionedKey> getAccents() {
        return wrapToKeyList(accents.get(state));
    }

    @Override
    public void update(char c) {
        state = c;

        removeAllViews();

        for (TextView textView :
                accents.get(state)) {
            addView(textView);
        }
    }

    @Override
    public boolean isAccentizable(char c) {
        return accents.keySet().contains(c);
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

    // TODO legyen más a neve
    private List<PositionedKey> wrapToKeyList(List<TextView> list) {
        List<PositionedKey> keys = new ArrayList<>();
        for (TextView view :
                list) {
            keys.add(new PositionedKey(view.getText().charAt(0), view.getX(), view.getWidth()));
        }
        return keys;
    }
}
