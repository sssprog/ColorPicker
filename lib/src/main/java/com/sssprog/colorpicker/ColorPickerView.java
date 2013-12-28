package com.sssprog.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author Sergey Samoylin <samoylin@gmail.com>.
 */
public class ColorPickerView extends FrameLayout implements ColorPickerHueView.OnHueChangeListener,
        ColorPickerLeftView.LeftColorPickerViewListener {

    private ColorPickerLeftView leftView;
    private ColorPickerHueView hueView;
    private View oldColorView;
    private View newColorView;

    private int oldColor;
    private float[] currentColor;
    private OnColorChangedListener listener;

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflateViews();
        initUI();
    }

    private void inflateViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.color_picker, this, false);
        leftView = (ColorPickerLeftView) view.findViewById(R.id.cp_leftView);
        hueView = (ColorPickerHueView) view.findViewById(R.id.cp_hueView);
        oldColorView = view.findViewById(R.id.cp_oldColor);
        newColorView = view.findViewById(R.id.cp_newColor);
        addView(view);
    }

    private void initUI() {
        hueView.setOnHueValueChangeListener(this);
        leftView.setListener(this);

        currentColor = new float[] {360, 1, 1};
        oldColor = getColor();
        updateUI();
    }

    public int getColor() {
        return Color.HSVToColor(currentColor);
    }

    public void setColor(int color) {
        oldColor = color;
        Color.colorToHSV(color, currentColor);
        updateUI();
        dispatchColorChangedEvent();
    }

    private void updateUI() {
        hueView.setHue(currentColor[0]);
        leftView.setHue(currentColor[0]);
        leftView.setSaturation(currentColor[1]);
        leftView.setValue(currentColor[2]);
        oldColorView.setBackgroundColor(oldColor);
        newColorView.setBackgroundColor(getColor());
    }

    @Override
    public void onHueChanged(float hue) {
        currentColor[0] = hue;
        leftView.setHue(hue);
        updateUI();
        dispatchColorChangedEvent();
    }

    @Override
    public void onColorChanged(float saturation, float value) {
        currentColor[1] = saturation;
        currentColor[2] = value;
        updateUI();
        dispatchColorChangedEvent();
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    private void dispatchColorChangedEvent() {
        if (listener != null) {
            listener.onColorChanged(getColor());
        }
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }



    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.oldColor = oldColor;
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

        oldColor = ss.oldColor;
        currentColor = ss.currentColor;
        updateUI();
    }

    public static class SavedState extends BaseSavedState {

        public int oldColor;
        public float[] currentColor;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            oldColor = in.readInt();
            in.readFloatArray(currentColor);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(oldColor);
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
