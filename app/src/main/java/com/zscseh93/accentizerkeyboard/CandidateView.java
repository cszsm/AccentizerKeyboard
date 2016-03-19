package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.Set;

import accentizer.Accentizer;

/**
 * Created by zscse on 2016. 03. 17..
 */
public class CandidateView extends RelativeLayout {

    private String suggestion;
    private Accentizer accentizer;

    public CandidateView(Context context) throws IOException {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_candidate, this, true);
        setBackgroundColor(Color.WHITE);

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

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
        Button button = (Button) findViewById(R.id.btnCandidate);
        button.setTextSize(18);
        button.setText(accentizer.accentize(suggestion));
    }

    public String getSuggestion() {
        return suggestion;
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
