package com.testdemo.testVerticalScrollView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testdemo.R;

/**
 * Created by Administrator on 2018/5/7.
 */
public class DialogViewPresenter implements View.OnClickListener {

    private Context context;
    private View dialogView;
    private View backgroundIV;
    private View itemsLayout;
    private View hireLayout;
    private View videoLayout;
    private View audioLayout;
    private TextView closeDialogTV;

    private int hireLayoutOffset;
    private int videoLayoutOffset;
    private int audioLayoutOffset;

    public DialogViewPresenter(Context context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.test_act3_dialog, null);
        backgroundIV = dialogView.findViewById(R.id.backgroundIV);
        itemsLayout = dialogView.findViewById(R.id.itemsLayout);
        hireLayout = dialogView.findViewById(R.id.hireLayout);
        videoLayout = dialogView.findViewById(R.id.videoLayout);
        audioLayout = dialogView.findViewById(R.id.audioLayout);
        closeDialogTV = (TextView) dialogView.findViewById(R.id.closeDialogTV);

        hireLayout.setOnClickListener(this);
        videoLayout.setOnClickListener(this);
        audioLayout.setOnClickListener(this);
        closeDialogTV.setOnClickListener(this);
        backgroundIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == closeDialogTV) {
            closeDialogTV.setEnabled(false);
            hideAnimator();
            return;
        }

        if (v == hireLayout) {
            if (onViewClickListener != null) {
                onViewClickListener.onHireClick();
            }
            return;
        }

        if (v == videoLayout) {
            if (onViewClickListener != null) {
                onViewClickListener.onVideoClick();
            }
            return;
        }

        if (v == audioLayout) {
            if (onViewClickListener != null) {
                onViewClickListener.onAudioClick();
            }
            return;
        }

        if (v == backgroundIV) {
            if (onViewClickListener != null) {
                onViewClickListener.onBackGroundClick();
            }
        }
    }

    public View getDialogView() {
        return this.dialogView;
    }

    public void setItemsLayoutMarginRight(int rightMargin) {
        if (itemsLayout != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemsLayout.getLayoutParams();
            params.rightMargin = rightMargin;
            itemsLayout.setLayoutParams(params);
        }
    }

    public void showDialog() {
        dialogView.setVisibility(View.VISIBLE);

//        setShowAnim();
        showAnimator();
    }

    public void hideDialog() {
//        hideAnim();
        closeDialogTV.setEnabled(false);
        hideAnimator();
    }

    private void showAnimator() {
        if (hireLayoutOffset == 0 || videoLayoutOffset == 0 || audioLayoutOffset == 0) {
            hireLayoutOffset = itemsLayout.getMeasuredHeight() - (int) hireLayout.getY();
            videoLayoutOffset = itemsLayout.getMeasuredHeight() - (int) videoLayout.getY();
            audioLayoutOffset = itemsLayout.getMeasuredHeight() - (int) audioLayout.getY();
            Log.i("greyson", "show items = " + hireLayoutOffset + " - " + hireLayout.getY() + "\n" + videoLayout.getY() + " _ " + itemsLayout.getLayoutParams().height);
        }

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(hireLayout, "translationY", hireLayoutOffset, 0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(videoLayout, "translationY", videoLayoutOffset, 0);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(audioLayout, "translationY", audioLayoutOffset, 0);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(backgroundIV, "alpha", 0, 1);

        set.setInterpolator(new OvershootInterpolator(1.3f));
        set.setDuration(500).play(animator).with(animator2).with(animator3).with(animator4);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                closeDialogTV.setEnabled(true);
            }
        });
        set.start();
    }

    private void hideAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(hireLayout, "translationY", 0, hireLayoutOffset);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(hireLayout, "alpha", 1, 0);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(videoLayout, "translationY", 0, videoLayoutOffset);
        ObjectAnimator animator22 = ObjectAnimator.ofFloat(videoLayout, "alpha", 1, 0);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(audioLayout, "translationY", 0, audioLayoutOffset);
        ObjectAnimator animator33 = ObjectAnimator.ofFloat(audioLayout, "alpha", 1, 0);

        ObjectAnimator animator4 = ObjectAnimator.ofFloat(backgroundIV, "alpha", 1, 0);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(500).play(animator).with(animator1).with(animator2).with(animator22).with(animator3).with(animator33).with(animator4);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hireLayout.setAlpha(1);
                videoLayout.setAlpha(1);
                audioLayout.setAlpha(1);
                dialogView.setVisibility(View.GONE);
            }
        });
        set.start();
    }

    private void setShowAnim() {
        TranslateAnimation translateMostFast = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 3,
                Animation.RELATIVE_TO_SELF, 0
        );
        translateMostFast.setDuration(800);
        hireLayout.startAnimation(translateMostFast);

        TranslateAnimation translateFast = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 2,
                Animation.RELATIVE_TO_SELF, 0
        );
        translateFast.setDuration(800);
        videoLayout.startAnimation(translateFast);

        TranslateAnimation translateNormal = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0
        );
        translateNormal.setDuration(800);
        audioLayout.startAnimation(translateNormal);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(800);
        backgroundIV.startAnimation(alphaAnimation);
    }

    private void hideAnim() {
        TranslateAnimation translateMostFast = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 3
        );
        translateMostFast.setDuration(800);
        hireLayout.startAnimation(translateMostFast);

        TranslateAnimation translateFast = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 2
        );
        translateFast.setDuration(800);
        videoLayout.startAnimation(translateFast);

        TranslateAnimation translateNormal = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1
        );
        translateNormal.setDuration(800);
        audioLayout.startAnimation(translateNormal);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(800);
        backgroundIV.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dialogView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private OnViewClickListener onViewClickListener;

    public OnViewClickListener getOnViewClickListener() {
        return onViewClickListener;
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public interface OnViewClickListener {
        void onVideoClick();

        void onAudioClick();

        void onHireClick();

        void onBackGroundClick();
    }
}
