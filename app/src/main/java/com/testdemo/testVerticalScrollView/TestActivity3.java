package com.testdemo.testVerticalScrollView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testdemo.R;
import com.testdemo.testVerticalScrollView.ThreeScrollView;

/**
 * Created by Administrator on 2018/1/25.
 */
public class TestActivity3 extends Activity implements ThreeScrollView.OnScrollChangeListener, DialogViewPresenter.OnViewClickListener {
    private final static String TAG = "Test3-greyson";

    IndexPointLayout indexPointLayout;

    private View blurBgIV;
    private FrameLayout containLayout;
    //    private boolean isAdd;
    private DialogViewPresenter dialogViewPresenter;

    private boolean showCallerServiceInfo;
    private View managerServiceInfoLayout;
    private View callerServiceInfoLayout;
    private TextView mediaChatTV;

    private Handler handler = new Handler();
    private Runnable myRunnable = new Runnable() {
        public void run() {
            TranslateAnimation translateOutAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            translateOutAnimation.setDuration(1000);

            TranslateAnimation translateInAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            translateInAnimation.setDuration(1000);

//            Log.i(TAG, "handler run --- ");
            if (showCallerServiceInfo) {
                callerServiceInfoLayout.setAnimation(translateOutAnimation);
                managerServiceInfoLayout.setAnimation(translateInAnimation);
                callerServiceInfoLayout.setVisibility(View.GONE);
                managerServiceInfoLayout.setVisibility(View.VISIBLE);
                showCallerServiceInfo = false;
            } else {
                callerServiceInfoLayout.setAnimation(translateInAnimation);
                managerServiceInfoLayout.setAnimation(translateOutAnimation);
                callerServiceInfoLayout.setVisibility(View.VISIBLE);
                managerServiceInfoLayout.setVisibility(View.GONE);
                showCallerServiceInfo = true;
            }
            handler.postDelayed(this, 2500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act3);

        ThreeScrollView threeScrollView = (ThreeScrollView) findViewById(R.id.threeScrollView);

        threeScrollView.addChildView(findViewById(R.id.firstView));
        threeScrollView.addChildView(findViewById(R.id.holdUpView));
        threeScrollView.addChildView(findViewById(R.id.thirdView));
        threeScrollView.addChildView(findViewById(R.id.forthView));

        threeScrollView.setOnScrollChangeListener(this);

        blurBgIV = findViewById(R.id.blurBgIV);
        containLayout = (FrameLayout) findViewById(R.id.containLayout);

        managerServiceInfoLayout = findViewById(R.id.managerServiceInfoLayout);
        callerServiceInfoLayout = findViewById(R.id.callerServiceInfoLayout);
        mediaChatTV = (TextView) findViewById(R.id.mediaChatTV);
        mediaChatTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (containLayout == null || dialogViewPresenter == null || dialogViewPresenter.getDialogView() == null) {
                    return;
                }
                containLayout.addView(dialogViewPresenter.getDialogView());*/
                dialogViewPresenter.showDialog();
            }
        });

        handler.postDelayed(myRunnable, 2500);


        //
        indexPointLayout = (IndexPointLayout) findViewById(R.id.indexPointLayout);
        indexPointLayout.setPointCount(4, 1);

        ProgressBar ratingPB = (ProgressBar) findViewById(R.id.ratingPB);
        ratingPB.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progressbar2));
        ratingPB.setMax(10);
        ratingPB.setProgress(7);

        ProgressBar ratingPB2 = (ProgressBar) findViewById(R.id.ratingPB2);
        ratingPB2.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progressbar2));
        ratingPB2.setMax(10);
        ratingPB2.setProgress(5);

        ProgressBar ratingPB3 = (ProgressBar) findViewById(R.id.ratingPB3);
        ratingPB3.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progressbar2));
        ratingPB3.setMax(10);
        ratingPB3.setProgress(3);

        ProgressBar ratingPB4 = (ProgressBar) findViewById(R.id.ratingPB4);
        ratingPB4.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progressbar2));
        ratingPB4.setMax(10);
        ratingPB4.setProgress(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dialogViewPresenter = new DialogViewPresenter(this);
        dialogViewPresenter.setOnViewClickListener(this);
        initDialogView();
    }

    @Override
    public void onVideoClick() {

    }

    @Override
    public void onAudioClick() {

    }

    @Override
    public void onHireClick() {

    }

    @Override
    public void onBackGroundClick() {

    }

    private void initDialogView() {
        mediaChatTV.post(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics metrics = new DisplayMetrics();
                ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
                int windowWidth = metrics.widthPixels;

                int[] locations = new int[2];
                mediaChatTV.getLocationInWindow(locations);
                System.out.println("width = " + windowWidth + " - " + mediaChatTV.getWidth() + " - " + locations[0]);
                dialogViewPresenter.setItemsLayoutMarginRight(windowWidth - locations[0] - mediaChatTV.getWidth());
                containLayout.addView(dialogViewPresenter.getDialogView());
            }
        });
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
    }

    @Override
    public void onFirstScrollToSecondChange(float nowY, float startY, float endY) {
        float value = (nowY - startY) / (endY - startY);
        blurBgIV.setAlpha(value);

        if (nowY < endY) {
            indexPointLayout.setSelection(1);
        } else {
            indexPointLayout.setSelection(3);
        }
    }

    public void onClick(View view) {
        Log.i(TAG, "onClick-" + view.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && myRunnable != null) {
            handler.removeCallbacks(myRunnable);
        }
    }
}
