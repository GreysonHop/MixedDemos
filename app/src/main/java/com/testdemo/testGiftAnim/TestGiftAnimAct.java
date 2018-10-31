package com.testdemo.testGiftAnim;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.testdemo.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;

/**
 * Created by Greyson on 2016/4/26.
 */
public class TestGiftAnimAct extends Activity implements View.OnClickListener {

    SVGAImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test_gift_anim);

//        setStatusBarViewOptions(false, -1, true);
//        immerseStatusBar();

        imageView = (SVGAImageView) findViewById(R.id.iv_svga);
        imageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
            }

            @Override
            public void onFinished() {
                Toast.makeText(TestGiftAnimAct.this, "播放完毕", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRepeat() {
            }

            @Override
            public void onStep(int i, double v) {
            }
        });
        imageView.setLoops(1);

        printFile(0, getApplicationContext().getCacheDir().getParentFile());

        SVGAParser parser = new SVGAParser(this);
        try {
//            parser.parse(new URL("http://public-file.nos-eastchina1.126.net/%E5%9F%8E%E5%A0%A1.svga"), new SVGAParser.ParseCompletion() {
            parser.parse(new URL("https://github.com/yyued/SVGA-Samples/blob/master/kingset.svga?raw=true"), new SVGAParser.ParseCompletion() {
//            parser.parse("520.svga", new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    SVGADrawable drawable = new SVGADrawable(videoItem, requestDynamicItemWithSpannableText());
//                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    imageView.setImageDrawable(drawable);
                    imageView.startAnimation();
                }

                @Override
                public void onError() {
                    Log.e("greyson", "error!!!!");
                }
            });

        } catch (Exception e) {
            System.out.print(true);
        }
    }

    private void printFile(int ceng, File dir) {
        if (dir == null) {
            return;
        }

        String kong = "";
        for (int i = 0; i < ceng; i++) {
            kong += " ";
        }

        if (dir.isDirectory()) {
            System.out.println(kong + dir.getAbsolutePath());

            File[] childs = dir.listFiles();
            for (int i = 0; i < childs.length; i++) {
                printFile(ceng + 1, childs[i]);
                /*if (childs[i].isDirectory()) {
                    System.out.println(kong + childs[i].getName());
                    printFile(ceng + 1, childs[i]);
                } else {
                    System.out.println(kong + childs[i].getName());
                }*/
            }
        } else {
            System.out.println(kong + dir.getAbsolutePath());
        }
    }

    public void onClick(View v) {
        if (imageView != null) {
            imageView.startAnimation();
        }
    }

    /**
     * 你可以设置富文本到 ImageKey 相关的元素上
     * 富文本是会自动换行的，不要设置过长的文本
     *
     * @return
     */
    private SVGADynamicEntity requestDynamicItemWithSpannableText() {
        SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("greyson 送了一打生蚝给主播");
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(48);
        dynamicEntity.setDynamicText(new StaticLayout(
                spannableStringBuilder,
                0,
                spannableStringBuilder.length(),
                textPaint,
                0,
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                false
        ), "boy");
       /*dynamicEntity.setDynamicDrawer(new Function2<Canvas, Integer, Boolean>() {
            @Override
            public Boolean invoke(Canvas canvas, Integer frameIndex) {
                Paint aPaint = new Paint();
                aPaint.setColor(Color.WHITE);
                canvas.drawCircle(50, 54, frameIndex % 5, aPaint);
                return false;
            }
        }, "boy");*/

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_vip_gold);
        dynamicEntity.setDynamicImage(bitmap, "boy");
        return dynamicEntity;
    }
}
