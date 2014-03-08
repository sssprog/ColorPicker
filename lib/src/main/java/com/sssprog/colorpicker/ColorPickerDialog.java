package com.sssprog.colorpicker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;

public class ColorPickerDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String PARAM_COLOR = "PARAM_COLOR";
    private static final String PARAMS_TITLE = "PARAMS_TITLE";
    private static final String PARAM_POSITIVE_BUTTON_TEXT = "PARAM_POSITIVE_BUTTON_TEXT";
    private static final String PARAM_NEGATIVE_BUTTON_TEXT = "PARAM_NEGATIVE_BUTTON_TEXT";

    private ColorPickerView colorPicker;

    public static ColorPickerDialog newInstance(int color, int title, int positiveButtonText, int negativeButtonText) {
        ColorPickerDialog dialog = new ColorPickerDialog();
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
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.color_picker_dialog, null);
        int title = getArguments().getInt(PARAMS_TITLE);
        int positiveButtonText = getArguments().getInt(PARAM_POSITIVE_BUTTON_TEXT);
        int negativeButtonText = getArguments().getInt(PARAM_NEGATIVE_BUTTON_TEXT);

        colorPicker = (ColorPickerView) rootView.findViewById(R.id.colorPicker);
        if (savedInstanceState == null) {
            colorPicker.setColor(getArguments().getInt(PARAM_COLOR));
        }

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

        // Adjust dialog dimensions
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(getDialog().getWindow().getAttributes());
                lp.width = Utils.inLandscape(getActivity()) ? Utils.getMinScreenSide(getActivity()) : WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = Utils.inPortrait(getActivity()) ? getPortraitHeight() : WindowManager.LayoutParams.WRAP_CONTENT;
                getDialog().getWindow().setAttributes(lp);
            }
        });

        return dialog;
    }

    private boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    private int getPortraitHeight() {
        int portraitHeight = Utils.getMinScreenSide(getActivity()) +
                getActivity().getResources().getDimensionPixelSize(R.dimen.color_picker_additional_portrait_height);
        portraitHeight = Math.min(portraitHeight, Utils.getMaxScreenSide(getActivity()));
        return portraitHeight;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) {
            ((ColorPickerDialogResultListener) getActivity()).onColorChosen(colorPicker.getColor());
        }
    }
}
