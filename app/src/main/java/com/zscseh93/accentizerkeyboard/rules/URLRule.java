package com.zscseh93.accentizerkeyboard.rules;

/**
 * Created by zscse on 2016. 04. 14..
 */
public class URLRule implements AccentizerRule {
    @Override
    public boolean isAccentizable(String word) {
        return !(word.startsWith("http://") ||
                word.startsWith("https://") ||
                word.startsWith("www."));
    }
}
