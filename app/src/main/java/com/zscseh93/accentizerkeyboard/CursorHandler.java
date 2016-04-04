package com.zscseh93.accentizerkeyboard;

/**
 * Created by zscse on 2016. 04. 01..
 */
public class CursorHandler {

    private int wordStart = 0;
    private int wordEnd = 0;

    public CursorHandler() {

    }

    public void setWord(int wordStart, int wordEnd) {
        this.wordStart = wordStart;
        this.wordEnd = wordEnd;
    }

    public boolean isWordChanged(int newPosition) {
        return !(wordStart <= newPosition && newPosition <= wordEnd);
    }
}
