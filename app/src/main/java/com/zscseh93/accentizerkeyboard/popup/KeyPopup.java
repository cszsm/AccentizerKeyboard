package com.zscseh93.accentizerkeyboard.popup;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by zscse on 2016. 04. 21..
 */
public class KeyPopup extends PopupWindow implements KeyContainer {

//    private TextView tv1;
//    private TextView tv2;

    private AccentBoard accentBoard;

    public KeyPopup(final Context context, AccentBoard accentBoard) {
        super(context);

        this.accentBoard = accentBoard;

        LinearLayout linearLayout = new LinearLayout(context);
        setContentView(accentBoard);

//        tv1 = new TextView(context);
//        tv1.setText("A");
//        tv1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Toast.makeText(context, "KÁO", Toast.LENGTH_SHORT).show();
//            }
//        });
//        tv2 = new TextView(context);
//        tv2.setText("B");
//
//        linearLayout.addView(tv1);
//        linearLayout.addView(tv2);
    }

//    @Override
//    public void dismiss() {
//        super.dismiss();
//        Log.d("log", "ezitt" + String.valueOf(tv2.getX()));
//        int[] ints = new int[2];
//        tv2.getLocationOnScreen(ints);
//        Log.d("log", String.valueOf(ints[0]) + ints[1]);
//    }

    public void updatePopup(char c) {
//        setContentView(accentBoard.getAccentBoardLayout(c));
        accentBoard.update(c);
    }

    public float cancel() {
        super.dismiss();
//        return tv2.getX();
        return 142;
    }

    @Override
    public List<PositionedKey> getKeys() {
        return accentBoard.getAccents();
    }
}
