package com.bitjini.vzcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by VEENA on 11/27/2015.
 */
public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf");
        this.setTypeface(face);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf");
        this.setTypeface(face);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf");
        if (defStyle == Typeface.BOLD) {
            super.setTypeface(face/*, -1*/);
        }
        //this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }
}
