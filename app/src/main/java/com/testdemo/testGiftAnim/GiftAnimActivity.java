package com.testdemo.testGiftAnim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.testdemo.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * a singleton activity to show some anim of gift
 * Created by Greyson on 2018/11/8.
 */
public class GiftAnimActivity extends Activity {

    private ImageView ivClose;
    private SVGAImageView ivSVGA;

    LinkedList<String> urlList = new LinkedList<>();
    private boolean hasExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_gift_anim_layout);

        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivSVGA = (SVGAImageView) findViewById(R.id.iv_svga);

        ivSVGA.setClearsAfterStop(true);
        ivSVGA.setLoops(1);
        ivSVGA.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
                Log.d("greyson", "gift onPause");
            }

            @Override
            public void onFinished() {
                Log.d("greyson", "gift onFinished");
                if (hasExit) {
                    return;
                }

                String url = urlList.pollFirst();
                if (!TextUtils.isEmpty(url)) {
                    showGiftAnim(url, "");
                } else {
                    hasExit = true;
                    finish();
                }
            }

            @Override
            public void onRepeat() {
                Log.d("greyson", "gift onRepeat");
            }

            @Override
            public void onStep(int i, double v) {
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasExit = true;
                ivSVGA.stopAnimation();
                finish();
            }
        });

        String fileUrl = getIntent().getStringExtra("fileUrl");
        String giftDesc = getIntent().getStringExtra("giftDesc");
        showGiftAnim(fileUrl, giftDesc);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String fileUrl = intent.getStringExtra("fileUrl");
        String giftDesc = intent.getStringExtra("giftDesc");
        urlList.add(fileUrl);
//        showGiftAnim(fileUrl, giftDesc);

    }

    public void showGiftAnim(String fileUrl, final String giftDesc) {

        try {
            SVGAParser parser = new SVGAParser(this);
            resetDownloader(parser);
            parser.parse(new URL(fileUrl), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {

                    if (ivSVGA.isAnimating()) {
                        ivSVGA.stopAnimation();
                    }

                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    ivSVGA.setImageDrawable(drawable);
                    ivSVGA.startAnimation();

                    if (!TextUtils.isEmpty(giftDesc)) {
//                        Toast.makeText().showShortToastSafe(giftDesc);
                    }
                }

                @Override
                public void onError() {
                    Log.e("greyson", "onError() -- 动画播放出错");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetDownloader(SVGAParser parser) {
        parser.setFileDownloader(new SVGAParser.FileDownloader() {
            @Override
            public void resume(final URL url, final Function1<? super InputStream, Unit> complete, final Function1<? super Exception, Unit> failure) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url).get().build();
                        try {
                            Response response = client.newCall(request).execute();
                            complete.invoke(response.body().byteStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                            failure.invoke(e);
                        }
                    }
                }).start();
            }
        });
    }
}
