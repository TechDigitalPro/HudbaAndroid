package com.uru.hudba.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomRoundSquireImageView extends AppCompatImageView {
    private float radius = 25.0f;
    private Path path;
    private RectF rect;

    public CustomRoundSquireImageView(Context context) {
        super(context);
        init();
    }

    public CustomRoundSquireImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRoundSquireImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
//        if (measuredWidth > measuredHeight) {
//            setMeasuredDimension(measuredHeight, measuredHeight);
//        } else {
//            setMeasuredDimension(measuredWidth, measuredWidth);
//        }
    }
}
