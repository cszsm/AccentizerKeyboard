package com.zscseh93.accentizerkeyboard.dictionary;

import android.util.Log;

import com.orm.SugarRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zscse on 2016. 05. 09..
 */
public class DictionaryElement {

    private String word;
    private Map<String, Integer> suggestions;

    public DictionaryElement(String word) {
        this.word = word;
        suggestions = new HashMap<>();
    }

    public String getWord() {
        return word;
    }

    /* TODO: ezt kell hívni, ha az adatbázisból töltjük fel */
    public void addSuggestion(Suggestion suggestion) {
        suggestions.put(suggestion.getSuggestion(), suggestion.getCount());
    }

    /* TODO: ezt kell hívni, ha új javaslatot mentünk */
    public void addSuggestion(String suggestion) {
        if (suggestions.containsKey(suggestion)) {
            int count = suggestions.get(suggestion);
            suggestions.put(suggestion, ++count);

            Suggestion persistableSuggestion = Suggestion.find(Suggestion.class, "word = ? and suggestion = ?", word, suggestion).get(0);
            persistableSuggestion.increaseCount();
            persistableSuggestion.save();
        } else {
            suggestions.put(suggestion, 1);

            Suggestion persistableSuggestion = new Suggestion(word, suggestion, 1);
            persistableSuggestion.save();
        }
    }

    public String getMostFrequentSuggestion() {
        int maxCount = 0;
        String mostFrequentSuggestion = "";
        for (Map.Entry entry :
                suggestions.entrySet()) {
            int currentCount = (int) entry.getValue();
            if (maxCount < currentCount) {
                maxCount = currentCount;
                mostFrequentSuggestion = (String) entry.getKey();
            }

            Log.d("ELEMENT", entry.getKey() + " - " + entry.getValue());
        }

        return mostFrequentSuggestion;
    }
}
