package com.testdemo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsProvider;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 * <p>
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

    @Test
    public void testAndroidQ() {
        BufferedInputStream bufferedInputStream = null;
        try {
            File file = mTargetContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            Log.d("greyson", "Documents' path: " + file.getAbsolutePath());
            final String fileName = "test.txt";
            // final String filePath = file.getAbsolutePath() + "/" + fileName;
            final String filePath = "/storage/emulated/0/Documents/" + fileName;
            Log.d("greyson", "filePath's path: " + filePath);
            final String uriPath = "content://com.android.externalstorage.documents/document/primary%3ADocuments%2Ftest.txt";

Uri uri1 = Uri.parse(uriPath);
// Uri uri1 = Uri.parse("file:///sdcard/Documents/test.txt");
String uri2 = Uri.decode("file:///sdcard/Documents/test.txt");
            Log.d("greyson", "testAndroidQ: " + uri2);
            // Uri uri = Uri.fromFile(new File(filePath));

            InputStream is = mTargetContext.getContentResolver().openInputStream(uri1);
            // InputStream is = mTargetContext.openFileInput(filePath);

            bufferedInputStream = new BufferedInputStream(is);
            Scanner scanner = new Scanner(bufferedInputStream);
            while (scanner.hasNextLine()) {
                Log.d("greyson", "content: " + scanner.nextLine());
            }

            /*byte[] buff = new byte[1024];
            int buffLength = 0;
            while ((buffLength = bufferedInputStream.read(buff)) > 0) {

            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
