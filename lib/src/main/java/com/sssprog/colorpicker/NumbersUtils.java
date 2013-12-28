package com.sssprog.colorpicker;

/**
 * @author Sergey Samoylin <samoylin@gmail.com>.
 */
class NumbersUtils {

    private static final float EPS = 0.0000001f;

    public static float putInsideInterval(float value, float start, float end) {
        value = Math.max(start, value);
        value = Math.min(end, value);
        return value;
    }

    public static boolean equals(float value1, float value2) {
        return Math.abs(value1 - value2) < EPS;
    }

}
