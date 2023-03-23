package com.testdemo.util;

import android.view.View;

public class SystemUiUtils {

    public static void switchLightStatusBar(View view, boolean on) {
        final int option = view.getSystemUiVisibility();
        if (on) {
            view.setSystemUiVisibility(option | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            view.setSystemUiVisibility(option & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


}
