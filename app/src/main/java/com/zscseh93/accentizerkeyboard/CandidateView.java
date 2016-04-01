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

    public CandidateView(Context context, final AccentizerKeyboard accentizerKeyboard, Accentizer accentizer) throws IOException {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_candidate, this, true);

        suggestion = "";

        button = (Button) findViewById(R.id.btnCandidate);
        button.setTextSize(18);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                accentizerKeyboard.replaceCurrentWord(suggestion);
            }
        });

        this.accentizer = accentizer;
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.drawText("telnet", 10, 10, paint);
//    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
//    }

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
