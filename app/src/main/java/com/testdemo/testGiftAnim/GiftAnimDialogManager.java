package com.testdemo.testGiftAnim;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.testdemo.R;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.LinkedList;

/**
 * 礼物动画播放布局对话框
 * Created by greyson on 2018/09/26.
 */
public class GiftAnimDialogManager {

    private static volatile GiftAnimDialogManager instance;

    public static GiftAnimDialogManager getInstance() {
        if (instance == null) {
            synchronized (GiftAnimDialogManager.class) {
                if (instance == null) {
                    instance = new GiftAnimDialogManager();
                }
            }
        }
        return instance;
    }

    private Dialog dialog;
    /*private ImageView ivClose;
    private SVGAImageView ivSVGA;*/

    private LinkedList<String> urlList = new LinkedList<>();
    //private boolean hasExit;
    private WeakReference<Context> contextWeakReference;
    private boolean isPlaying;

    private GiftAnimDialogManager() {
    }

    private void initDialog(Context context, SVGADrawable svgaDrawable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_gift_anim_layout, null);

        dialog = new Dialog(context, R.style.GiftAnimDialog);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
        SVGAImageView ivSVGA = (SVGAImageView) view.findViewById(R.id.iv_svga);

        dialog.setContentView(view);
        dialog.setOnDismissListener((d) -> {
//            ivClose = null;
//            ivSVGA = null;
            Log.i("greyson", "dialog dismiss listener. ivClose=" + ivClose + " | ivSVGA=" + ivSVGA);
        });

        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialogWindow.setAttributes(lp);
        }

        ivClose.setOnClickListener((v) -> {
            ivSVGA.stopAnimation();
            dialog.dismiss();
        });
        ivSVGA.setLoops(1);
        ivSVGA.setClearsAfterStop(true);
        ivSVGA.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
            }

            @Override
            public void onFinished() {
                Log.d("greyson", "gift onFinished");
                dialog.dismiss();
                synchronized (GiftAnimDialogManager.this) {
                    isPlaying = false;
                    String url = urlList.pollFirst();
                    if (!TextUtils.isEmpty(url)) {
                        showGiftAnim(url, "");
                    } else {
//                    hasExit = true;
                    }
                }
            }

            @Override
            public void onRepeat() {
            }

            @Override
            public void onStep(int i, double v) {
            }
        });

        ivSVGA.setImageDrawable(svgaDrawable);
        ivSVGA.startAnimation();
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addGiftAnim(Context context, String fileUrl, final String giftDesc) {
        contextWeakReference = new WeakReference<>(context);
        synchronized (GiftAnimDialogManager.this) {
            if (isPlaying) {
                urlList.add(fileUrl);
            } else {
                showGiftAnim(fileUrl, giftDesc);
            }
        }
    }

    /**
     * @param fileUrl  要显示的礼物动画的url
     * @param giftDesc 显示动画时要告诉用户的文字，null或空字符串则不显示
     */
    public void showGiftAnim(String fileUrl, final String giftDesc) {

        /*Intent intent = new Intent(context, GiftAnimActivity.class);
        intent.putExtra("fileUrl", fileUrl);
        intent.putExtra("giftDesc", giftDesc);
        context.startActivity(intent);*/

        isPlaying = true;
        if (contextWeakReference == null || contextWeakReference.get() == null) {
            urlList.clear();
            return;
        }
//        Context context = contextWeakReference.get();

        try {
            SVGAParser parser = new SVGAParser(contextWeakReference.get());
            parser.parse(new URL(fileUrl), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {

                    if (dialog != null && dialog.isShowing()) {//context对象可能不是同一个，所以要关闭上一个dialog
                        /*if (ivSVGA.isAnimating()) {
                            ivSVGA.stopAnimation();
                        }*/
                        dialog.dismiss();
                    }

                    if (contextWeakReference == null || contextWeakReference.get() == null) {
                        return;
                    }
                    initDialog(contextWeakReference.get(), new SVGADrawable(videoItem));//如果activity已经被关闭了，动画没必要显示

                    /*if (dialog.isShowing() && ivSVGA.isAnimating()) {
                        ivSVGA.stopAnimation();
                    }*/

                    /*SVGADrawable drawable = new SVGADrawable(videoItem);
                    ivSVGA.setImageDrawable(drawable);
                    ivSVGA.startAnimation();
                    dialog.show();
                    if (!TextUtils.isEmpty(giftDesc)) {
//                        ToastUtil.showShortToastSafe(giftDesc);
                    }*/
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

}
