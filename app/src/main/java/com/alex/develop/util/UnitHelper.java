package com.alex.develop.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by alex on 15-7-31.
 * 常用的单位转换工具
 */
public class UnitHelper {

    public static void init(Context context) {
        dm = context.getResources().getDisplayMetrics();
    }

    public static float dp2px(float dp) {
        return dp * dm.density + FACTOR;
    }

    public static float sp2px(float sp) {
        return sp * dm.scaledDensity + FACTOR;
    }

    private UnitHelper() {}

    private static DisplayMetrics dm;
    private static final float FACTOR = 0.5f;
}
