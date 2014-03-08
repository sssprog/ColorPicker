package com.sssprog.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorWheelView extends View {

    private float[] currentColor = { 360f, 1f, 1f };

    private Drawable borderDrawable;
    private Drawable cursorDrawable;
    private Drawable wheelDrawable;
    private int borderWidth;

    private ColorWheelListener listener;

    public ColorWheelView(Context context) {
        super(context);
        init(null, 0);
    }

    public ColorWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ColorWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        borderWidth = getResources().getDimensionPixelSize(R.dimen.cp_default_wheel_border_width);
        int borderColor = getResources().getColor(R.color.cp_default_wheel_border_color);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorWheelView, defStyle, 0);
            borderColor = a.getColor(R.styleable.ColorWheelView_borderColor, borderColor);
            borderWidth = a.getDimensionPixelSize(R.styleable.ColorWheelView_borderWidth, borderWidth);
            a.recycle();
        }

        cursorDrawable = getResources().getDrawable(R.drawable.cp_left_view_cursor);
        wheelDrawable = getResources().getDrawable(R.drawable.color_wheel);
        borderDrawable = getResources().getDrawable(R.drawable.wheel_border);
        borderDrawable = getBorderDrawable(borderWidth, borderColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int size = 0;
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
            size = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.EXACTLY) {
            size = Math.min(size, MeasureSpec.getSize(heightMeasureSpec));
        }
        if (size > 0) {
            setMeasuredDimension(size, size);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private Drawable getBorderDrawable(int borderWidth, int borderColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(borderWidth, borderColor);
        return drawable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        borderDrawable.draw(canvas);
        wheelDrawable.draw(canvas);
        cursorDrawable.setBounds(getCursorDrawRect());
        cursorDrawable.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        borderDrawable.setBounds(0, 0, getWidth(), getHeight());
        wheelDrawable.setBounds(borderWidth, borderWidth, getWidth() - borderWidth, getHeight() - borderWidth);
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
        return (float) (getWidth() / 2.0 + getRadius() * currentColor[1] * Math.cos(Math.toRadians(currentColor[0])));
    }

    private float getCursorYPosition() {
        return (float) (getHeight() - (getRadius() * currentColor[1] * Math.sin(Math.toRadians(currentColor[0])) + getHeight() / 2.0));
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
        double radius = getRadius();
        // M(x, y) coordinates for system with (0, 0) in center of the wheel
        double x = event.getX() - getWidth() / 2.0;
        double y = -(event.getY() - getWidth() / 2.0);

        // Handle edge cases
        if (x == 0) {
            x = 0.0001;
        }
        if (y == 0) {
            y = 0.0001;
        }

        double distance = Math.sqrt(x * x + y * y);
        // alpha - angle between OX and OM (M event location)
        double alpha = Math.acos(Math.abs(x) / distance);
        if (x < 0 && y > 0) {
            alpha = Math.PI - alpha;
        } else if (x < 0 && y < 0) {
            alpha += Math.PI;
        } else if (x > 0 && y < 0) {
            alpha = 2 * Math.PI - alpha;
        }

        double hue = Math.toDegrees(alpha);

        currentColor[0] = NumbersUtils.putInsideInterval((float) hue, 0, 360);
        currentColor[1] = NumbersUtils.putInsideInterval((float) (distance / radius), 0, 1);

        invalidate();
        dispatchColorChangedEvent();
    }

    private void dispatchColorChangedEvent() {
        if (listener != null) {
            listener.onColorChanged(getColor());
        }
    }

    private float getRadius() {
        return getWidth() / 2f - borderWidth;
    }

    public int getColor() {
        return Color.HSVToColor(currentColor);
    }

    public void setColor(int color) {
        Color.colorToHSV(color, currentColor);
        invalidate();
        dispatchColorChangedEvent();
    }

    public void setListener(ColorWheelListener listener) {
        this.listener = listener;
    }

    public static interface ColorWheelListener {
        void onColorChanged(int color);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.currentColor = currentColor;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        currentColor = ss.currentColor;
        dispatchColorChangedEvent();
        invalidate();
    }

    public static class SavedState extends BaseSavedState {

        public float[] currentColor;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            in.readFloatArray(currentColor);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloatArray(currentColor);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
