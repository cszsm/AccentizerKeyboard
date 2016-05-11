package com.zscseh93.accentizerkeyboard.rules;

/**
 * Created by zscse on 2016. 04. 13..
 */
public class HashtagRule implements AccentizerRule {

    @Override
    public boolean isAccentizable(String word) {
        return word.charAt(0) != '#';
    }
}
