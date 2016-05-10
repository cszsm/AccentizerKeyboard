package com.zscseh93.accentizerkeyboard.dictionary;

import com.orm.SugarRecord;

/**
 * Created by zscse on 2016. 05. 10..
 */
public class Suggestion extends SugarRecord {

    private String word;
    private String suggestion;
    private int count;

    public Suggestion() {
    }

    public Suggestion(String word, String suggestion, int count) {
        this.word = word;
        this.suggestion = suggestion;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        count++;
    }
}
