package com.testdemo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
//        mTargetContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testOnCreate() {//todo 还是不能正常运行？？。。。
        String data = null;
        try {
            ImageView testBitmap = new ImageView(mTargetContext);


            ApplicationInfo applicationInfo = mTargetContext.getPackageManager()
                    .getApplicationInfo(mTargetContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = ((ApplicationInfo) applicationInfo).metaData;
            data = metaData.getString("com.google.android.geo.API_KEY");
            System.out.println("AndroidTest, data = " + data);
//            assertEquals("1234567890", data);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals("1234567890", data);
    }
}
