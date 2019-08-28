package com.testdemo.testSpecialEditLayout;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Greyson
 */
public class MenuPopUp {

    public static final int DEFAULT_NORMAL_TEXT_COLOR = Color.WHITE;
    public static final int DEFAULT_PRESSED_TEXT_COLOR = Color.WHITE;
    public static final float DEFAULT_TEXT_SIZE_DP = 14;
    public static final float DEFAULT_TEXT_PADDING_LEFT_DP = 10.0f;
    public static final float DEFAULT_TEXT_PADDING_TOP_DP = 5.0f;
    public static final float DEFAULT_TEXT_PADDING_RIGHT_DP = 10.0f;
    public static final float DEFAULT_TEXT_PADDING_BOTTOM_DP = 5.0f;
    public static final int DEFAULT_NORMAL_BACKGROUND_COLOR = 0xCC000000;
    public static final int DEFAULT_PRESSED_BACKGROUND_COLOR = 0xE7777777;
    public static final int DEFAULT_BACKGROUND_RADIUS_DP = 8;
    public static final int DEFAULT_DIVIDER_COLOR = 0x9AFFFFFF;
    public static final float DEFAULT_DIVIDER_WIDTH_DP = 0.5f;
    public static final float DEFAULT_DIVIDER_HEIGHT_DP = 16.0f;

    private boolean hasInit;
    private Context mContext;
    private PopupWindow mPopupWindow;
    private LinearLayout mLlPopUpContent;
    private LinearLayout mLlMenuList;

    private TextView mNextPage;
    private TextView mPreviousPage;
    private View mIndicatorViewDown;
    private View mIndicatorViewUp;
    private View mIndicatorViewCurrent;

    private List<TextView> mTvMenuList = new ArrayList<>();
    private List<String> mMenuList;

    public MenuPopUp(Context context) {
        mContext = context;

        initPopUpPanel();
        initPageSwitchBtn();
    }

    private void initPopUpPanel() {
        mLlPopUpContent = new LinearLayout(mContext);
        mLlPopUpContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mLlPopUpContent.setOrientation(LinearLayout.VERTICAL);
        mLlPopUpContent.setGravity(Gravity.CENTER_HORIZONTAL);
        mLlMenuList = new LinearLayout(mContext);
        mLlMenuList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mLlMenuList.setOrientation(LinearLayout.HORIZONTAL);
        mLlMenuList.setGravity(Gravity.CENTER_VERTICAL);
        GradientDrawable cornerBackground = new GradientDrawable();
        cornerBackground.setColor(DEFAULT_NORMAL_BACKGROUND_COLOR);
        cornerBackground.setCornerRadius(dp2px(4));
        mLlMenuList.setBackground(cornerBackground);
        mLlPopUpContent.addView(mLlMenuList);

        mIndicatorViewDown = getTriangleIndicatorDown();
        mIndicatorViewUp = getTriangleIndicatorUp();
    }

    private View mAnchorView;
    private int mTouchXInScreen;
    private int mShowY;
//    private int[] mAnchorViewLocation = new int[2];

    /**
     * 依附于anchorView显示popupWindow，默认在其中间顶部
     * @param anchorView
     */
    public void showPopupWindow(View anchorView) {
        showPopupWindow(anchorView, anchorView.getWidth() / 2f, 0, false);
    }

    /**
     * 在指定的点显示popupWindow
     *
     * @param anchorView
     * @param touchX     长按时最后手指所在处的X坐标，相对于anchorView
     * @param touchY     长按时最后手指所在处的Y坐标，相对于anchorView
     * @param locateInScreen touch点是参照window，而不是anchorView
     */
    public void showPopupWindow(View anchorView, float touchX, float touchY, boolean locateInScreen) {
        if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
            return;
        }
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mContext);
            mPopupWindow.setContentView(mLlPopUpContent);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(null);
        }

        mAnchorView = anchorView;

        int statusBarHeight = getStatusBarHeight();
        int touchRawY;
        if (locateInScreen) {
            touchRawY = (int) touchY;
            mTouchXInScreen = (int) touchX;
        } else {
            int[] anchorViewLocation = new int[2];
            anchorView.getLocationOnScreen(anchorViewLocation);
            touchRawY = anchorViewLocation[1] + (int) touchY;
            mTouchXInScreen = anchorViewLocation[0] + (int) touchX;
        }
        int mPopupWindowHeight = getViewHeight(mLlPopUpContent);

        if (touchRawY - mPopupWindowHeight < statusBarHeight) {
            if (mIndicatorViewCurrent != mIndicatorViewUp) {
                mLlPopUpContent.removeView(mIndicatorViewDown);
                mIndicatorViewCurrent = mIndicatorViewUp;
                mLlPopUpContent.addView(mIndicatorViewUp, 0);
            }
            mShowY = touchRawY;
        } else {
            if (mIndicatorViewCurrent != mIndicatorViewDown) {
                mLlPopUpContent.removeView(mIndicatorViewUp);
                mIndicatorViewCurrent = mIndicatorViewDown;
                mLlPopUpContent.addView(mIndicatorViewDown);
            }
            mShowY = touchRawY - mPopupWindowHeight;
        }

        showMenuInPage(0);
        adjustXAndShowPopupWindow();
    }

    public void hidePopupWindow() {
        if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
            return;
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 根据宽度的变化调整popupWindow显示位置的X坐标。以长按点为中心，去调节Location的X；
     * 如果超出屏幕则往屏幕里面偏移
     */
    private void adjustXAndShowPopupWindow() {
        if (!hasInit) {
            return;
        }
        int popupWindowWidth = getViewWidth(mLlPopUpContent);
        int indicatorWidth = getViewWidth(mIndicatorViewCurrent);

        int touchRawX = mTouchXInScreen;
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;

        float halfPopupWindowWidth = popupWindowWidth / 2f;
        float leftTranslationLimit = indicatorWidth / 2f - halfPopupWindowWidth;
        float rightTranslationLimit = halfPopupWindowWidth - indicatorWidth / 2f;

        int x;

        if (touchRawX < halfPopupWindowWidth) {
            mIndicatorViewCurrent.setTranslationX(Math.max(touchRawX - halfPopupWindowWidth, leftTranslationLimit));
            x = 0;
        } else if (touchRawX + halfPopupWindowWidth > screenWidth) {
            mIndicatorViewCurrent.setTranslationX(Math.min(touchRawX + halfPopupWindowWidth - screenWidth, rightTranslationLimit));
            x = screenWidth - popupWindowWidth / 2;
        } else {
            mIndicatorViewCurrent.setTranslationX(0);
            x = touchRawX - popupWindowWidth / 2;
        }


        if (mPopupWindow.isShowing()) {
//            mPopupWindow.dismiss();
            mPopupWindow.update(x, mShowY, popupWindowWidth, getViewHeight(mLlPopUpContent));
        } else
//        int x = (int) (touchRawX - halfPopupWindowWidth + 0.5f);
//        int y = (int) (touchRawY - mPopupWindowHeight + 0.5f);
            mPopupWindow.showAtLocation(mAnchorView, Gravity.NO_GRAVITY, x, mShowY);
//            mPopupWindow.showAsDropDown(mAnchorView);

    }

    private SparseArray<TextView> mAllMenu = new SparseArray<>();
    /**
     * indicate the last index of per page. the size equal to {@link #mShowingPage}.
     * And the page data include the index indicated by this array's value.
     */
    private int[] mLastIndexPerPage;
    /**
     * the page that indicate current menu list in show. NOTE: it begin from 0
     */
    private int mShowingPage;

    public void setMenuList(List<String> menuList) {
        if (menuList == null) return;
        mMenuList = menuList;
        mTvMenuList.clear();

        int[] ints;
        int size = menuList.size();
        ints = new int[size];
        int currentWidth = 0;
        List<Integer> pagePoint = new ArrayList<>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int widthPerPage = screenWidth / 4 * 3;
        int switchPageBtnWidth = getViewWidth(mNextPage);

        for (int i = 0; i < size; i++) {
            String s = menuList.get(i);
            TextView tv = new TextView(mContext);
            tv.setTextColor(Color.WHITE);
//            tv.setBackgroundResource();
            tv.setPadding(dp2px(8f), dp2px(5f), dp2px(8f), dp2px(5f));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            tv.setText(s);
            tv.setGravity(Gravity.CENTER);
            final int position = i;
            tv.setOnClickListener((view) -> {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onMenuClick(view, position);
                }
            });

            ints[i] = getViewWidth(tv);
            System.out.println("greyson tv's width = " + ints[i] + ", currentWidth = " + currentWidth + ", screen = " + widthPerPage);
            currentWidth += ints[i];
            if (currentWidth + switchPageBtnWidth * 2 > widthPerPage) {
                pagePoint.add(i - 1);
                currentWidth = ints[i];
            }

            if (i == size - 1) {
                pagePoint.add(i);
            }

            mTvMenuList.add(tv);
            mAllMenu.put(i, tv);
        }
        mLastIndexPerPage = new int[pagePoint.size()];
        for (int i = 0; i < pagePoint.size(); i++) {
            mLastIndexPerPage[i] = pagePoint.get(i);
        }

        mShowingPage = -1;
        showMenuInPage(0);
        hasInit = true;
    }

    private void showNextPage() {
        if (mShowingPage + 1 > mLastIndexPerPage.length) {
            return;
        }
        showMenuInPage(mShowingPage + 1);
    }

    private void showPreviousPage() {
        if (mShowingPage - 1 < 0) {
            return;
        }
        showMenuInPage(mShowingPage - 1);
    }

    private void showMenuInPage(int page) {
        if (page == mShowingPage) {
            return;
        }
        mShowingPage = page;
        mLlMenuList.removeAllViews();

        int beginIndex = 0;
        if (page > 0) {
            mLlMenuList.addView(mPreviousPage);
            mLlMenuList.addView(getLineView());

            beginIndex = mLastIndexPerPage[page - 1] + 1;
        }

        int showUntilIndex = mLastIndexPerPage[page];
        for (int i = beginIndex; i <= showUntilIndex; i++) {
            mLlMenuList.addView(mTvMenuList.get(i));

            if (i == showUntilIndex) {
                if (page < mLastIndexPerPage.length - 1) {
                    mLlMenuList.addView(getLineView());
                    mLlMenuList.addView(mNextPage);
                }
            } else {
                mLlMenuList.addView(getLineView());
            }
        }
        adjustXAndShowPopupWindow();
    }

    private View getLineView() {
        View line = new View(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp2px(1f), LinearLayout.LayoutParams.MATCH_PARENT);
        line.setLayoutParams(lp);
        line.setBackgroundColor(Color.WHITE);
        return line;
    }

    private void initPageSwitchBtn() {
        mNextPage = new TextView(mContext);
        mNextPage.setGravity(Gravity.CENTER);
//        mNextPage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.list_icon_next);
        mNextPage.setPadding(dp2px(8f), dp2px(5f), dp2px(10f), dp2px(5f));
        mNextPage.setOnClickListener((v) -> showNextPage());

        mPreviousPage = new TextView(mContext);
        mPreviousPage.setGravity(Gravity.CENTER);
//        mPreviousPage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.refresh, 0, 0, 0);
        mPreviousPage.setPadding(dp2px(10f), dp2px(5f), dp2px(8f), dp2px(5f));
        mPreviousPage.setOnClickListener((v) -> showPreviousPage());


        float triangleHeight = dp2px(10f);
        float triangleWidth = dp2pxFloat(6f);

        Drawable rightTriangleDrawable = new CanvasDrawable(triangleWidth, triangleHeight) {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                Path path = new Path();
                path.moveTo(0f, 0f);
                path.lineTo(0f, mIntrinsicHeight);
                path.lineTo(mIntrinsicWidth, mIntrinsicHeight / 2);
                path.close();
                canvas.drawPath(path, paint);
            }
        };
        mNextPage.setCompoundDrawablesWithIntrinsicBounds(null, null, rightTriangleDrawable, null);

        Drawable leftTriangleDrawable = new CanvasDrawable(triangleWidth, triangleHeight) {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                Path path = new Path();
                path.moveTo(mIntrinsicWidth, 0f);
                path.lineTo(mIntrinsicWidth, mIntrinsicHeight);
                path.lineTo(0f, mIntrinsicHeight / 2);
                path.close();
                canvas.drawPath(path, paint);
            }
        };
        mPreviousPage.setCompoundDrawablesWithIntrinsicBounds(leftTriangleDrawable, null, null, null);
    }

    private View getTriangleIndicatorDown() {
        ImageView indicator = new ImageView(mContext);
        Drawable drawable = new CanvasDrawable(dp2px(16), dp2px(8)) {
            @Override
            public void draw(Canvas canvas) {
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(DEFAULT_NORMAL_BACKGROUND_COLOR);
                paint.setStyle(Paint.Style.FILL);
                path.moveTo(0f, 0f);
                path.lineTo(mIntrinsicWidth, 0f);
                path.lineTo(mIntrinsicWidth / 2, mIntrinsicHeight);
                path.close();
                canvas.drawPath(path, paint);
            }
        };
        indicator.setImageDrawable(drawable);
        return indicator;
    }

    private View getTriangleIndicatorUp() {
        ImageView indicator = new ImageView(mContext);
        Drawable drawable = new CanvasDrawable(dp2px(16), dp2px(8)) {
            @Override
            public void draw(Canvas canvas) {
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(DEFAULT_NORMAL_BACKGROUND_COLOR);
                paint.setStyle(Paint.Style.FILL);
                path.moveTo(0f, mIntrinsicHeight);
                path.lineTo(mIntrinsicWidth, mIntrinsicHeight);
                path.lineTo(mIntrinsicWidth / 2, 0);
                path.close();
                canvas.drawPath(path, paint);
            }
        };
        indicator.setImageDrawable(drawable);
        return indicator;
    }

    public Resources getResources() {
        if (mContext == null) {
            return Resources.getSystem();
        } else {
            return mContext.getResources();
        }
    }

    private int getViewWidth(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }

    private int getViewHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public int dp2px(float value) {
        return (int) dp2pxFloat(value);
    }

    public float dp2pxFloat(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getResources().getDisplayMetrics());
    }

    public int sp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                value, getResources().getDisplayMetrics());
    }

    public int getStatusBarHeight() {
        // 获得状态栏高度
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return mContext.getResources().getDimensionPixelSize(resourceId);
    }

    private OnMenuClickListener onMenuClickListener;

    public OnMenuClickListener getOnMenuClickListener() {
        return onMenuClickListener;
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    public interface OnMenuClickListener {
        void onMenuClick(View view, int position);
    }

    class CanvasDrawable extends Drawable {
        protected float mIntrinsicWidth;
        protected float mIntrinsicHeight;

        public CanvasDrawable(float intrinsicWidth, float intrinsicHeight) {
            mIntrinsicWidth = intrinsicWidth;
            mIntrinsicHeight = intrinsicHeight;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) mIntrinsicWidth;
        }

        @Override
        public int getIntrinsicHeight() {
            return (int) mIntrinsicHeight;
        }
    }
}

