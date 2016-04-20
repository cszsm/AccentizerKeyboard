package com.zscseh93.accentizerkeyboard.rules;

/**
 * Created by zscse on 2016. 04. 14..
 */
public class EmailRule implements AccentizerRule {

    @Override
    public boolean isAccentizable(String word) {
        if (word.contains("@")) {
            return false;
        }
        return true;
    }
}
