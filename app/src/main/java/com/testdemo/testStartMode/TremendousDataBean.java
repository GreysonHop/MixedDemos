package com.testdemo.testStartMode;

import android.os.Binder;

/**
 * Create by Greyson on 2023/04/07
 */
class TremendousDataBean extends Binder {

    Object mData;

    public TremendousDataBean() {}

    public TremendousDataBean(Object data) {
        mData = data;
    }
}
