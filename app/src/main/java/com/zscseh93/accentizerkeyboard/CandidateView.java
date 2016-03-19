package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    public CandidateView(Context context) throws IOException {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_candidate, this, true);
        setBackgroundColor(Color.WHITE);

        button = (Button) findViewById(R.id.btnCandidate);
        button.setTextSize(18);

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

    public void setSuggestion(String word) {
        suggestion = accentizer.accentize(word);
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
}
