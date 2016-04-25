package com.zscseh93.accentizerkeyboard.popup;

/**
 * Created by zscse on 2016. 04. 25..
 */
public class PositionedKey {
    private char key;
    private float position;
    private int width;

    public PositionedKey(char key, float position, int width) {
        this.key = key;
        this.position = position;
        this.width = width;
    }

    public char getKey() {
        return key;
    }

    public float getStart() {
        return position;
    }

    public float getEnd() {
        return position + width;
    }
}
