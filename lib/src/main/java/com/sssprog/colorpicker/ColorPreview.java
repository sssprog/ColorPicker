package com.sssprog.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Sergey Samoylin <samoylin@gmail.com>.
 */
class ColorPreview extends View {

    private final Paint borderPaint;

    public ColorPreview(Context context) {
        this(context, null);
    }

    public ColorPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1 * getContext().getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);
    }
}
