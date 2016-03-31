package com.zscseh93.accentizerkeyboard;

import android.content.res.Resources;

import java.io.IOException;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 20..
 */
public class AccentizerCreator {
    private Accentizer accentizer;

    public AccentizerCreator(Resources resources) throws IOException {
        accentizer = new Accentizer();

        accentizer.load('a', resources.openRawResource(R.raw.a));
        accentizer.load('e', resources.openRawResource(R.raw.e));
        accentizer.load('i', resources.openRawResource(R.raw.i));
        accentizer.load('o', resources.openRawResource(R.raw.o));
        accentizer.load('u', resources.openRawResource(R.raw.u));
    }

    public Accentizer getAccentizer() {
        return accentizer;
    }
}
