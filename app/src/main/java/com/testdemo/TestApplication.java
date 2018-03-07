package com.testdemo;

import android.app.Activity;
import android.app.Application;

/**
 * Created by Administrator on 2018/3/7.
 */
public class TestApplication extends Application {

    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
