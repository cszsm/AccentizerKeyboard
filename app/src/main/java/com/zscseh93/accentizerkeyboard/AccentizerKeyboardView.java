package com.zscseh93.accentizerkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zscseh93.accentizerkeyboard.popup.KeyPopup;

/**
 * Created by zscse on 2016. 04. 20..
 */
public class AccentizerKeyboardView extends KeyboardView {

    private final String LOG_TAG = "AccentizerKeyboardView";

    private Context context;
    private int popupX;

//    private PopupWindow popupWindow;
    private KeyPopup popupWindow;
    private LinearLayout linearLayout;

    public AccentizerKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        linearLayout = new LinearLayout(context);

//        textView.setText("káo");
//        linearLayout.addView(textView);
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = layoutInflater.inflate(R.layout.key_popup, linearLayout);
//        popupWindow = new PopupWindow(context);
//        popupWindow.setContentView(linearLayout);
        popupWindow = new KeyPopup(context);
    }

    @Override
    protected boolean onLongPress(Keyboard.Key popupKey) {
        linearLayout.removeAllViews();

        switch (popupKey.codes[0]) {
            case 97:
                TextView tvA1 = new TextView(context);
                tvA1.setText("Á");
                tvA1.setTextSize(32);
                linearLayout.addView(tvA1);
                Log.d(LOG_TAG, "ÁÁ");
                break;
            default:
                TextView tvD = new TextView(context);
                tvD.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                tvD.setText("d");
                TextView tvJ = new TextView(context);
                tvJ.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                tvJ.setText("j");
                TextView tvK = new TextView(context);
                tvK.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                tvK.setText("k");
                linearLayout.addView(tvD);
                linearLayout.addView(tvJ);
                linearLayout.addView(tvK);
                Log.d(LOG_TAG, "DJK");
                break;
        }
//        tvK.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "KÁO", Toast.LENGTH_SHORT).show();
//            }
//        });
//        tvK.setOnHoverListener(new OnHoverListener() {
//            @Override
//            public boolean onHover(View v, MotionEvent event) {
//                Toast.makeText(context, "KÁO Hover", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

//        int[] ints = new int[2];
//        tvD.getLocationOnScreen(ints);
//        Log.d(LOG_TAG, String.valueOf(ints[0]));
//        tvJ.getLocationOnScreen(ints);
//        Log.d(LOG_TAG, String.valueOf(ints[0]));
//        tvK.getLocationOnScreen(ints);
//        Log.d(LOG_TAG, String.valueOf(ints[0]));


        popupWindow.updatePopup((char) popupKey.codes[0]);
        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, 0, 0);
        popupWindow.update(popupKey.x, popupKey.y, 100, 100);
        popupX = popupKey.x;

        Log.d(LOG_TAG, "longPress");
        for (int i : popupKey.codes) {
            Log.d(LOG_TAG, String.valueOf(i));
        }
        Log.d(LOG_TAG, String.valueOf(popupKey.codes[0]));

//        return super.onLongPress(popupKey);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_UP) {
//            popupWindow.dismiss();
            float x = popupWindow.cancel();
            Log.d(LOG_TAG, String.valueOf(me.getX()));
            Log.d(LOG_TAG, String.valueOf(popupX + x));
        }
        return super.onTouchEvent(me);
    }

//    @Override
//    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        Log.d(LOG_TAG, "onKeyLongPress");
//        return super.onKeyLongPress(keyCode, event);
//    }
}
