package com.testdemo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 *
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    private Context mTargetContext;

    /*@Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.testdemo", appContext.getPackageName());
    }*/

    @Before
    public void setUp() throws Exception {
        mTargetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // mTargetContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testOnCreate() {
        String data = null;
        try {
            ImageView testBitmap = new ImageView(mTargetContext);


            ApplicationInfo applicationInfo = mTargetContext.getPackageManager()
                    .getApplicationInfo(mTargetContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            data = metaData.getString("com.google.android.geo.API_KEY");
            System.out.println("AndroidTest, data = " + data);
            System.out.println("AndroidTest, UMENG_CHANNEL = " + metaData.getString("UMENG_CHANNEL"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals("AIzaSyDmWtdkIbK6qdMvVnVQn2vqnjkQkVygnbI", data);
    }


    @Test
    public void testBase64() {
        String s = "ChphcHAuSW52aXRhdGlvbk5vdGlmaWNhdGlvblKqAQoudHlwZS5nb29nbGVhcGlzLmNvbS9hcHAuSW52aXRhdGlvbk5vdGlmaWNhdGlvbhJ4CiQ3MGU2NzdhNS05MWQxLTQzODItOGNlMS02YWM1MDY2NzZlZjQSOgoYNjA4YTljMTU0ZjM4NmExYTA0MDc1MzUxEglFQk9BMDAwMjQ6DwoLMTMzODAzNDQ2ODEQVkICQ04YASIHZWJvX2FpcioJbGN3MDItMDAx";
        byte[] bytes = Base64.decode(s, Base64.DEFAULT);
        byte[] bytes2 = Base64.decode(s, Base64.CRLF);
        byte[] bytes3 = Base64.decode(s, Base64.NO_CLOSE);
        byte[] bytes4 = Base64.decode(s, Base64.NO_WRAP);
        byte[] bytes5 = Base64.decode(s, Base64.URL_SAFE);
        byte[] bytes6 = Base64.decode(s, Base64.NO_PADDING);
        Log.d("greyson", new String(bytes));
        Log.d("greyson", new String(bytes2));
        Log.d("greyson", new String(bytes3));
        Log.d("greyson", new String(bytes4));
        Log.d("greyson", new String(bytes5));
        Log.d("greyson", new String(bytes6));
    }
}
