package com.zscseh93.accentizerkeyboard;

import android.content.res.Resources;

import java.io.IOException;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 20..
 */
public class HunAccentizer {
    private Accentizer accentizer;

    public HunAccentizer(Resources resources) throws IOException {
        accentizer = new Accentizer();

        accentizer.load('a', resources.openRawResource(R.raw.a));
        accentizer.load('e', resources.openRawResource(R.raw.e));
        accentizer.load('i', resources.openRawResource(R.raw.i));
        accentizer.load('o', resources.openRawResource(R.raw.o));
        accentizer.load('u', resources.openRawResource(R.raw.u));
    }

    public String getSuggestion(String word) {
        return accentizer.accentize(word);
    }
}
