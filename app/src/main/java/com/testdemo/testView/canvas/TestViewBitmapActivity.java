package com.testdemo.testView.canvas;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.testdemo.BaseCommonActivity;
import com.testdemo.R;
import com.testdemo.blurbehind.Blur;

/**
 * Create by Greyson on 2022/11/09
 */
public class TestViewBitmapActivity extends BaseCommonActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.act_test_view_bitmap;
    }

    @Override
    protected void initView() {
        ImageView iv_blur = findViewById(R.id.iv_im);
        ImageView iv_main = findViewById(R.id.iv_main);

        iv_main.postDelayed(() -> {
            iv_main.setDrawingCacheEnabled(true);
            iv_main.destroyDrawingCache();
            iv_main.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            Bitmap cacheBitmap = iv_main.getDrawingCache();

            new Thread(() -> {
                Bitmap blurBitmap = Blur.apply(getBaseContext(), cacheBitmap, 10);
                if (blurBitmap == null) return;

                iv_blur.post(() -> {
                    iv_blur.setImageBitmap(blurBitmap);
                });
            }).start();
        }, 2000);

    }


}
