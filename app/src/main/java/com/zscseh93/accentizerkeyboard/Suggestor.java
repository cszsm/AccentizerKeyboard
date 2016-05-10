package com.zscseh93.accentizerkeyboard;

import com.zscseh93.accentizerkeyboard.dictionary.SuggestionDictionary;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 05. 10..
 */
public class Suggestor {
    private Accentizer accentizer;
    private SuggestionDictionary dictionary;

    public Suggestor(Accentizer accentizer, SuggestionDictionary dictionary) {
        this.accentizer = accentizer;
        this.dictionary = dictionary;
    }

    public String suggestByAccentizer(String word) {
        String deaccentizedWord = accentizer.deaccentize(word);
        String accentizedWord = accentizer.accentize(deaccentizedWord);
        if (word.equals(accentizedWord)) {
            return accentizer.deaccentize(word);
        } else {
            return accentizedWord;
        }
    }

    public String suggestByDictionary(String word) {
        String deaccentizedWord = accentizer.deaccentize(word);
        String suggestion = dictionary.getMostFrequentSuggestion(deaccentizedWord);
        if (word.equals(suggestion)) {
            return deaccentizedWord;
        }
        return suggestion;
    }
}
