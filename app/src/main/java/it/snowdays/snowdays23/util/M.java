package it.snowdays.snowdays23.util;

import android.util.DisplayMetrics;

import java.util.Objects;

public class M {

    private static DisplayMetrics sDisplayMetrics;

    public static Float dp(float dp) {
        Objects.requireNonNull(sDisplayMetrics);
        return dp * sDisplayMetrics.density;
    }

    public static int screenWidth() {
        Objects.requireNonNull(sDisplayMetrics);
        return sDisplayMetrics.widthPixels;
    }

    public static int screenHeight() {
        Objects.requireNonNull(sDisplayMetrics);
        return sDisplayMetrics.heightPixels;
    }

    public static void setDisplayMetrics(DisplayMetrics metrics) {
        sDisplayMetrics = new DisplayMetrics();
        sDisplayMetrics.setTo(metrics);
    }

}
