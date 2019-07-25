package com.testdemo.testDatePicker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

public class MyDatePicker extends ConstraintLayout {

    public MyDatePicker(Context context) {
        this(context, null);
    }

    public MyDatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutParams lp = new LayoutParams(107, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
