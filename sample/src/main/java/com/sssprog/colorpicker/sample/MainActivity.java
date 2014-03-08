package com.sssprog.colorpicker.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.sssprog.colorpicker.ColorPickerDialog;
import com.sssprog.colorpicker.ColorPickerDialogResultListener;
import com.sssprog.colorpicker.ColorWheelDialog;

public class MainActivity extends ActionBarActivity implements ColorPickerDialogResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void onColorPickerClick(View view) {
        ColorPickerDialog.newInstance(Color.WHITE, R.string.app_name, R.string.save, R.string.cancel)
                .show(getSupportFragmentManager(), null);
    }

    @Override
    public void onColorChosen(int color) {

    }

    public void onColorWheelPickerClick(View view) {
        ColorWheelDialog.newInstance(Color.WHITE, R.string.app_name, R.string.save, R.string.cancel)
                .show(getSupportFragmentManager(), null);
    }
}
