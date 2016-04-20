package com.zscseh93.accentizerkeyboard.rules;

/**
 * Created by zscse on 2016. 04. 13..
 */
public class HashtagRule implements AccentizerRule {

    @Override
    public boolean isAccentizable(String word) {
        if (word.charAt(0) == '#') {
            return false;
        }
        return true;
    }
}
