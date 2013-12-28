package com.sssprog.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Sergey Samoylin <samoylin@gmail.com>.
 */
class ColorPickerLeftView extends View {

    private final Paint paint = new Paint();
    // saturation and value are always 1
    private final float[] onlyHueColor = { 360f, 1f, 1f };
    // Real color that now is chosen
    private final float[] currentColor = { 360f, 1f, 1f };
    private LeftColorPickerViewListener listener;

    private Drawable cursorDrawable;

    public ColorPickerLeftView(Context context) {
        this(context, null);
    }

    public ColorPickerLeftView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerLeftView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        cursorDrawable = getContext().getResources().getDrawable(R.drawable.cp_left_view_cursor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0f, 0f, getWidth(), getHeight(), paint);
        cursorDrawable.setBounds(getCursorDrawRect());
        cursorDrawable.draw(canvas);
    }

    private Rect getCursorDrawRect() {
        int left = (int) (getCursorXPosition() - cursorDrawable.getIntrinsicWidth() / 2);
        int top = (int) (getCursorYPosition() - cursorDrawable.getIntrinsicHeight() / 2);
        return new Rect(left,
                top,
                left + cursorDrawable.getIntrinsicWidth(),
                top + cursorDrawable.getIntrinsicHeight());
    }

    private float getCursorXPosition() {
        return currentColor[1] * getWidth();
    }

    private float getCursorYPosition() {
        return (1 - currentColor[2]) * getHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePaint();
    }

    public void setHue(float hue) {
        hue = NumbersUtils.putInsideInterval(hue, 0, 360);
        currentColor[0] = hue;
        onlyHueColor[0] = hue;
        updatePaint();
        invalidate();
    }

    public void setSaturation(float saturation) {
        currentColor[1] = NumbersUtils.putInsideInterval(saturation, 0, 1);
        updatePaint();
        invalidate();
    }

    public void setValue(float value) {
        currentColor[2] = NumbersUtils.putInsideInterval(value, 0, 1);
        updatePaint();
        invalidate();
    }

    private void updatePaint() {
        LinearGradient verticalGradient = new LinearGradient(0f, 0f, 0f, getHeight(), Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        LinearGradient horizontalGradient = new LinearGradient(0f, 0f, getWidth(), 0f, Color.WHITE, Color.HSVToColor(onlyHueColor), Shader.TileMode.CLAMP);
        ComposeShader shader = new ComposeShader(verticalGradient, horizontalGradient, PorterDuff.Mode.MULTIPLY);
        paint.setShader(shader);
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
        float saturation = event.getX() / getWidth();
        currentColor[1] = NumbersUtils.putInsideInterval(saturation, 0, 1);

        float value = 1 - event.getY() / getHeight();
        currentColor[2] = NumbersUtils.putInsideInterval(value, 0, 1);

        invalidate();
        dispatchColorChangedEvent();
    }

    private void dispatchColorChangedEvent() {
        if (listener != null) {
            listener.onColorChanged(currentColor[1], currentColor[2]);
        }
    }

    public void setListener(LeftColorPickerViewListener listener) {
        this.listener = listener;
    }


    public interface LeftColorPickerViewListener {
        /**
         * Called only when user changes this values
         * @param saturation
         * @param value
         */
        void onColorChanged(float saturation, float value);
    }

}
