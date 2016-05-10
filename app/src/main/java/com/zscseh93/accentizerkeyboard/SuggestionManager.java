package com.zscseh93.accentizerkeyboard;

import com.zscseh93.accentizerkeyboard.dictionary.SuggestionDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zscse on 2016. 05. 10..
 */
public class SuggestionManager {

    private FirebaseManager firebaseManager;
    private SuggestionDictionary suggestionDictionary;

    public SuggestionManager(FirebaseManager firebaseManager, SuggestionDictionary
            suggestionDictionary) {
        this.firebaseManager = firebaseManager;
        this.suggestionDictionary = suggestionDictionary;
    }

    public void saveSuggestion(String word, String userSuggestion, String accentizerSuggestion) {
        if (!word.equals(accentizerSuggestion)) {
            firebaseManager.saveSuggestion(userSuggestion, accentizerSuggestion);
        }

        if (!word.equals(userSuggestion)) {
            suggestionDictionary.saveSuggestion(word, userSuggestion);
        }
    }
}
