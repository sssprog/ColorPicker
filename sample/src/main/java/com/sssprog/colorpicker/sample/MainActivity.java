package com.sssprog.colorpicker.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.sssprog.colorpicker.ColorPickerView;

public class MainActivity extends ActionBarActivity {

    private ColorPickerView colorPicker;
    private View currentColorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorPicker = (ColorPickerView) findViewById(R.id.colorPicker);
        currentColorView = findViewById(R.id.currentColorView);

        colorPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                currentColorView.setBackgroundColor(color);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentColorView.setBackgroundColor(colorPicker.getColor());
    }

}
