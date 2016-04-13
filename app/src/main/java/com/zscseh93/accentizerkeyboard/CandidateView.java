package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.IOException;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 17..
 */
public class CandidateView extends RelativeLayout {

    private String suggestion;
    private Accentizer accentizer;
    private Button button;

    private final String LOG_TAG = "CandidateView";

    public CandidateView(Context context, final TextInputConnection inputConnection, Accentizer accentizer) throws IOException {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_candidate, this, true);

        suggestion = "";

        button = (Button) findViewById(R.id.btnCandidate);
        button.setTextSize(18);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                inputConnection.replaceCurrentWord(suggestion);
            }
        });

        this.accentizer = accentizer;
    }

    public void setCurrentWord(String word) {
        String accentizedWord = accentizer.accentize(word);

        if (word.equals(accentizedWord)) {
            suggestion = accentizer.deaccentize(accentizedWord);
        } else {
            suggestion = accentizedWord;
        }

        button.setText(suggestion);
    }
}
