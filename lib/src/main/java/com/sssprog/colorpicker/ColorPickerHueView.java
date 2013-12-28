package com.sssprog.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Sergey Samoylin <samoylin@gmail.com>.
 */
class ColorPickerHueView extends View {

    private final Drawable hueDrawable;
    private final Drawable cursorDrawable;
    private final int cursorPadding;
    private float hue = 360;
    private OnHueChangeListener listener;

    public ColorPickerHueView(Context context) {
        this(context, null);
    }

    public ColorPickerHueView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerHueView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        hueDrawable = getContext().getResources().getDrawable(R.drawable.cp_hue_view_bg);
        cursorDrawable = getContext().getResources().getDrawable(R.drawable.cp_hue_cursor);
        cursorPadding = getResources().getDimensionPixelSize(R.dimen.cp_hue_cursor_padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        hueDrawable.draw(canvas);
        cursorDrawable.setBounds(getCursorDrawRect());
        cursorDrawable.draw(canvas);
    }

    private Rect getCursorDrawRect() {
        int top = (int) (getCursorYPosition() - cursorDrawable.getIntrinsicHeight() / 2);
        return new Rect(0,
                top,
                cursorDrawable.getIntrinsicWidth(),
                top + cursorDrawable.getIntrinsicHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        hueDrawable.setBounds(cursorPadding + cursorDrawable.getIntrinsicWidth(), 0, getWidth(), getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                cursorMoved(event);
                return true;

            default:
                return false;
        }
    }

    private void cursorMoved(MotionEvent event) {
        hue = (1 - event.getY() / getHeight()) * 360;
        hue = NumbersUtils.putInsideInterval(hue, 0, 360);
        invalidate();
        dispatchHueChangedEvent();
    }

    private void dispatchHueChangedEvent() {
        if (listener != null) {
            listener.onHueChanged(getHue());
        }
    }

    public void setOnHueValueChangeListener(OnHueChangeListener listener) {
        this.listener = listener;
    }

    private float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = NumbersUtils.putInsideInterval(hue, 0, 360);
    }

    private float getCursorYPosition() {
        return (1 - hue / 360) * getHeight();
    }


    interface OnHueChangeListener {
        /**
         * Called only when user changes hue value
         * @param hue
         */
        void onHueChanged(float hue);
    }
}
