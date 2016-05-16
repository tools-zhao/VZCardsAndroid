package com.bitjini.vzcards;

/**
 * Created by bitjini on 14/5/16.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
public class CustomImageView extends View {

        private final Drawable logo;

        public CustomImageView(Context context) {
                super(context);
                logo = context.getResources().getDrawable(R.drawable.no_pic_placeholder);
                setBackgroundDrawable(logo);
        }

        public CustomImageView(Context context, AttributeSet attrs) {
                super(context, attrs);
                logo = context.getResources().getDrawable(R.drawable.no_pic_placeholder);
                setBackgroundDrawable(logo);
        }

        public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
                logo = context.getResources().getDrawable(R.drawable.no_pic_placeholder);
                setBackgroundDrawable(logo);
        }

        @Override protected void onMeasure(int widthMeasureSpec,
                                           int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = width * logo.getIntrinsicHeight() / logo.getIntrinsicWidth();
                setMeasuredDimension(width, height);
        }
}