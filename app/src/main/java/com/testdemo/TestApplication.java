package com.testdemo;

import android.app.Activity;
import android.app.Application;
import android.net.http.HttpResponseCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by Greyson on 2018/3/7.
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

        //set for SVGA
        File dir = getApplicationContext().getCacheDir();
        File cacheDir = new File(dir, "http");
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
