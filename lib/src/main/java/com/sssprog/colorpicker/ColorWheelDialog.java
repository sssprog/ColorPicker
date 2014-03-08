package com.sssprog.colorpicker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.sssprog.colorpicker.ColorWheelView.ColorWheelListener;

public class ColorWheelDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String PARAM_COLOR = "PARAM_COLOR";
    private static final String PARAMS_TITLE = "PARAMS_TITLE";
    private static final String PARAM_POSITIVE_BUTTON_TEXT = "PARAM_POSITIVE_BUTTON_TEXT";
    private static final String PARAM_NEGATIVE_BUTTON_TEXT = "PARAM_NEGATIVE_BUTTON_TEXT";

    private ColorWheelView colorWheel;
    private View oldColorView;
    private View newColorView;

    public static ColorWheelDialog newInstance(int color, int title, int positiveButtonText, int negativeButtonText) {
        ColorWheelDialog dialog = new ColorWheelDialog();
        Bundle args = new Bundle();
        args.putInt(PARAM_COLOR, color);
        args.putInt(PARAMS_TITLE, title);
        args.putInt(PARAM_POSITIVE_BUTTON_TEXT, positiveButtonText);
        args.putInt(PARAM_NEGATIVE_BUTTON_TEXT, negativeButtonText);
        dialog.setArguments(args);
        return dialog;
    }

    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.color_wheel_dialog, null);
        int title = getArguments().getInt(PARAMS_TITLE);
        int positiveButtonText = getArguments().getInt(PARAM_POSITIVE_BUTTON_TEXT);
        int negativeButtonText = getArguments().getInt(PARAM_NEGATIVE_BUTTON_TEXT);

        colorWheel = (ColorWheelView) rootView.findViewById(R.id.colorPicker);
        oldColorView = rootView.findViewById(R.id.cp_oldColor);
        newColorView = rootView.findViewById(R.id.cp_newColor);
        int color = getArguments().getInt(PARAM_COLOR);
        if (savedInstanceState == null) {
            colorWheel.setColor(color);
        }
        oldColorView.setBackgroundColor(color);
        newColorView.setBackgroundColor(colorWheel.getColor());

        colorWheel.setListener(new ColorWheelListener() {
            @Override
            public void onColorChanged(int color) {
                newColorView.setBackgroundColor(color);
            }
        });

        AlertDialog.Builder builder = hasICS() ?
                new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK) :
                new AlertDialog.Builder(getActivity());

        if (title > 0) {
            builder.setTitle(title);
        }
        if (positiveButtonText > 0) {
            builder.setPositiveButton(positiveButtonText, this);
        }
        if (negativeButtonText > 0) {
            builder.setNegativeButton(negativeButtonText, this);
        }

        AlertDialog dialog = builder
                .setView(rootView)
                .create();

        return dialog;
    }

    private boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) {
            ((ColorPickerDialogResultListener) getActivity()).onColorChosen(colorWheel.getColor());
        }
    }
}
