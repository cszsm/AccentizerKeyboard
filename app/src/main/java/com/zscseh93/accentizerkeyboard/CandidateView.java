package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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

    private final String LOG_TAG = "keyboard_candidate";

    public CandidateView(Context context, final AccentizerKeyboard accentizerKeyboard) throws IOException {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_candidate, this, true);
        setBackgroundColor(Color.WHITE);

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

        initializeAccentizer(context);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.drawText("telnet", 10, 10, paint);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        setMeasuredDimension(50, 50);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setCurrentWord(String word) {
        String accentizedWord = accentizer.accentize(word);

        if (word.equals(accentizedWord)) {
            suggestion = deaccentize(accentizedWord);
        } else {
            suggestion = accentizedWord;
        }

        button.setText(suggestion);
    }

    public String getSuggestion() {
        return accentizer.accentize(suggestion);
    }

    private void initializeAccentizer(Context context) throws IOException {
        accentizer = new Accentizer();
        Resources resources = context.getResources();

        accentizer.load('a', resources.openRawResource(R.raw.a));
        accentizer.load('e', resources.openRawResource(R.raw.e));
        accentizer.load('i', resources.openRawResource(R.raw.i));
        accentizer.load('o', resources.openRawResource(R.raw.o));
        accentizer.load('u', resources.openRawResource(R.raw.u));
    }

    // TODO
    private String deaccentize(String accentizedWord) {
        String deaccentizedWord = "";

        for (int i = 0; i < accentizedWord.length(); i++) {
            char c = accentizedWord.charAt(i);

            switch (c) {
                case 'á':
                    c = 'a';
                    break;
                case 'é':
                    c = 'e';
                    break;
                case 'í':
                    c = 'i';
                    break;
                case 'ó':
                case 'ö':
                case 'ő':
                    c = 'o';
                    break;
                case 'ú':
                case 'ü':
                case 'ű':
                    c = 'u';
                    break;
            }

            deaccentizedWord += c;
        }

        return deaccentizedWord;
    }
}
