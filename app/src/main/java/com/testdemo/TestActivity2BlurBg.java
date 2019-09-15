package com.testdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.testdemo.blurbehind.Blur;
import com.wonderkiln.blurkit.BlurLayout;

/**
 * Created by Administrator on 2018/3/6.
 */
public class TestActivity2BlurBg extends Activity {

    private FrameLayout container;
    private BlurLayout blurLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act2_blur_bg);

        container = (FrameLayout) findViewById(R.id.container);
//        blurLayout = (BlurLayout) findViewById(R.id.blurLayout);

        final Activity activity = ((TestApplication) getApplication()).getActivity();
        Log.i("greyson", "TestActivity2BlurBg last activity = " + activity);

        blurBackground(activity);
    }

    private void blurBackground(final Activity activity) {
        CacheBlurBehindAndExecuteTask cacheBlurBehindAndExecuteTask = new CacheBlurBehindAndExecuteTask(activity);
        cacheBlurBehindAndExecuteTask.execute();
    }

    class CacheBlurBehindAndExecuteTask extends AsyncTask<Void, Void, Void> {
        private Activity activity;

        private View decorView;
        private Bitmap image;
        private BitmapDrawable result;

        public CacheBlurBehindAndExecuteTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            decorView = activity.getWindow().getDecorView();
            View tempView = decorView.findViewById(android.R.id.content);
            if (tempView != null) {
                decorView = tempView;
            }

            decorView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            decorView.setDrawingCacheEnabled(true);
            decorView.buildDrawingCache();

            image = decorView.getDrawingCache();
        }

        @Override
        protected Void doInBackground(Void... params) {
            result = new BitmapDrawable(activity.getResources(), coverColor(Blur.apply(activity, image, 20), Color.parseColor("#dd2B282E")));
            Log.i("greyson", "default alpha = " + result.getAlpha());
            image.recycle();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            container.setBackground(result);
            decorView.destroyDrawingCache();
            decorView.setDrawingCacheEnabled(false);
            activity = null;
        }
    }

    public static Bitmap coverColor(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        new Canvas(bitmap).drawRoundRect(rect, 0, 0, paint);
        return bitmap;
    }
}
