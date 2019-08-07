package com.testdemo.testDatePicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.testdemo.R;

public class TextDialog extends Dialog {



    public TextDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_item);
    }

    private void initLayoutParams() {
        Window window = getWindow();
        if (window != null) {
            /*window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = MATCH_PARENT;
            lp.height = WRAP_CONTENT;
            window.setAttributes(lp);
            *//*Resources.getSystem().getDisplayMetrics().widthPixels;*//*
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.animBottomMenu);*/


            window.setGravity(Gravity.START | Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.x = 0;
            lp.y = 0;
            window.setAttributes(lp);
        }
    }
}
