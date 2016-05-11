package com.zscseh93.accentizerkeyboard.dictionary;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zscse on 2016. 05. 09..
 */
public class SuggestionDictionary {

    private static final String LOG_TAG = "SuggestionDictionary";

    private Map<String, DictionaryElement> dictionary;

    public SuggestionDictionary() {
        dictionary = new HashMap<>();

        List<Suggestion> suggestionList = Suggestion.listAll(Suggestion.class);
        for (Suggestion suggestion :
                suggestionList) {
            if (dictionary.containsKey(suggestion.getWord())) {
                DictionaryElement dictionaryElement = dictionary.get(suggestion.getWord());
                dictionaryElement.addSuggestion(suggestion);
                dictionary.put(dictionaryElement.getWord(), dictionaryElement);
            } else {
                DictionaryElement dictionaryElement = new DictionaryElement(suggestion.getWord());
                dictionaryElement.addSuggestion(suggestion);
                dictionary.put(dictionaryElement.getWord(), dictionaryElement);
            }
            Log.d(LOG_TAG, suggestion.getWord() + " - " + suggestion.getSuggestion() + " - " +
                    suggestion.getCount());
        }
    }

    public String getMostFrequentSuggestion(String word) {
        DictionaryElement element = dictionary.get(word);
        if (element == null) {
            return "";
        }

        return element.getMostFrequentSuggestion();
    }

    public void saveSuggestion(String word, String suggestion) {
        if (dictionary.containsKey(word)) {
            DictionaryElement element = dictionary.get(word);
            element.addSuggestion(suggestion);
        } else {
            DictionaryElement element = new DictionaryElement(word);
            element.addSuggestion(suggestion);
            dictionary.put(word, element);
        }
    }
}
