package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.zscseh93.accentizerkeyboard.dictionary.Suggestion;

import java.io.IOException;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 17..
 */
public class CandidateView extends RelativeLayout {

    private String accentizerSuggestion;
    private String dictionarySuggestion;
//    private Accentizer accentizer;

    private Suggestor suggestor;

    private Button btnOriginal;
    private Button btnAccentizer;
    private Button btnDictionary;

    private final String LOG_TAG = "CandidateView";

    public CandidateView(Context context, final TextInputConnection inputConnection, Suggestor suggestor) throws IOException {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_candidate, this, true);

        accentizerSuggestion = "";
        dictionarySuggestion = "";

        btnOriginal = (Button) findViewById(R.id.btnCandidateOriginal);

        btnAccentizer = (Button) findViewById(R.id.btnCandidateAccentizer);
        btnAccentizer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputConnection.replaceCurrentWord(accentizerSuggestion);
            }
        });

        btnDictionary = (Button) findViewById(R.id.btnCandidateDictionary);
        btnDictionary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputConnection.replaceCurrentWord(dictionarySuggestion);
            }
        });

//        this.accentizer = accentizer;
        this.suggestor = suggestor;
    }

    public void setCurrentWord(String word) {
//        String accentizedWord = accentizer.accentize(word);
//
//        if (word.equals(accentizedWord)) {
//            accentizerSuggestion = accentizer.deaccentize(accentizedWord);
//        } else {
//            accentizerSuggestion = accentizedWord;
//        }

        accentizerSuggestion = suggestor.suggestByAccentizer(word);
        dictionarySuggestion = suggestor.suggestByDictionary(word);

        btnAccentizer.setText(accentizerSuggestion);
        btnDictionary.setText(dictionarySuggestion);

        if (accentizerSuggestion.equals("")) {
            btnAccentizer.setEnabled(false);
        } else {
            btnAccentizer.setEnabled(true);
        }

        if (dictionarySuggestion.equals("")) {
            btnDictionary.setEnabled(false);
        } else {
            btnDictionary.setEnabled(true);
        }
    }

    public void setBackground(int color) {
        btnAccentizer.setBackgroundColor(color);
    }
}
