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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.testdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
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

    private Context mContext;
    private PopupWindow mPopupWindow;
    private LinearLayout mLlPopUpContent;
    private LinearLayout mLlMenuList;

    private TextView mNextPage;
    private TextView mPreviousPage;
    private View mIndicatorView;

    private List<TextView> mTvMenuList = new ArrayList<>();
    private List<String> mMenuList;

    public MenuPopUp(Context context) {
        mContext = context;
        mIndicatorView = getDefaultIndicatorView(mContext);

        initPopUpPanel();
        initPageSwitchBtn();
    }

    private void initPopUpPanel() {
        mLlPopUpContent = new LinearLayout(mContext);
        mLlPopUpContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mLlPopUpContent.setOrientation(LinearLayout.VERTICAL);
        mLlMenuList = new LinearLayout(mContext);
        mLlMenuList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mLlMenuList.setOrientation(LinearLayout.HORIZONTAL);
        mLlMenuList.setGravity(Gravity.CENTER_VERTICAL);
        mLlMenuList.setBackgroundResource(R.drawable.shape_corner4_black);
        mLlPopUpContent.addView(mLlMenuList);

        if (mIndicatorView != null) {
            LinearLayout.LayoutParams layoutParams;
            if (mIndicatorView.getLayoutParams() == null) {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            } else {
                layoutParams = (LinearLayout.LayoutParams) mIndicatorView.getLayoutParams();
            }
            layoutParams.gravity = Gravity.CENTER;
            mIndicatorView.setLayoutParams(layoutParams);
            ViewParent viewParent = mIndicatorView.getParent();
            if (viewParent instanceof ViewGroup) {
                ((ViewGroup) viewParent).removeView(mIndicatorView);
            }
            mLlPopUpContent.addView(mIndicatorView);
        }
    }

    public void showPopupListWindow(View mAnchorView, float offsetX, float offsetY) {
        if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
            return;
        }
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mContext);
            mPopupWindow.setContentView(mLlPopUpContent);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(null);
        }

        int mPopupWindowWidth = getViewWidth(mLlPopUpContent);
        int mPopupWindowHeight = getViewHeight(mLlPopUpContent);
        int mIndicatorWidth = getViewHeight(mIndicatorView);

        int[] location = new int[2];
        mAnchorView.getLocationOnScreen(location);
        if (mIndicatorView != null) {
            float leftTranslationLimit = mIndicatorWidth / 2f /*+ mBackgroundCornerRadius*/ - mPopupWindowWidth / 2f;
            float rightTranslationLimit = mPopupWindowWidth / 2f - mIndicatorWidth / 2f /*- mBackgroundCornerRadius*/;
            float maxWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            if (location[0] + offsetX < mPopupWindowWidth / 2f) {
                mIndicatorView.setTranslationX(Math.max(location[0] + offsetX - mPopupWindowWidth / 2f, leftTranslationLimit));
            } else if (location[0] + offsetX + mPopupWindowWidth / 2f > maxWidth) {
                mIndicatorView.setTranslationX(Math.min(location[0] + offsetX + mPopupWindowWidth / 2f - maxWidth, rightTranslationLimit));
            } else {
                mIndicatorView.setTranslationX(0);
            }
        }
        if (!mPopupWindow.isShowing()) {
            int x = (int) (location[0] + offsetX - mPopupWindowWidth / 2f + 0.5f);
            int y = (int) (location[1] + offsetY - mPopupWindowHeight + 0.5f);
            mPopupWindow.showAtLocation(mAnchorView, Gravity.NO_GRAVITY, x, y);
//            mPopupWindow.showAsDropDown(mAnchorView);
        }
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
        mMenuList = menuList;
        if (menuList == null) return;

        int[] ints;
        int size = menuList.size();
        ints = new int[size];
        int currentWidth = 0;
        List<Integer> pagePoint = new ArrayList<>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int widthPerPage = screenWidth / 4 * 3;

        for (int i = 0; i < size; i++) {
            String s = menuList.get(i);
            TextView tv = new TextView(mContext);
            tv.setTextColor(Color.WHITE);
//            tv.setBackgroundResource();
            tv.setPadding(dp2px(7f), dp2px(5f), dp2px(7f), dp2px(5f));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            tv.setText(s);
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener((view) -> {
                /*if (onMenuClickListener != null) {
                    onMenuClickListener.onMenuClick(view);
                }*/
            });

            ints[i] = getViewWidth(tv);
            System.out.println("greyson tv's width = " + ints[i] + ", currentWidth = " + currentWidth + ", screen = " + widthPerPage);
            currentWidth += ints[i];
            if (currentWidth > widthPerPage) {
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

        mShowingPage = 0;
        showMenuInPage(mShowingPage);
    }

    private void showNextPage() {
        if (mShowingPage + 1 > mLastIndexPerPage.length) {
            return;
        }
        showMenuInPage(++mShowingPage);
    }

    private void showPreviousPage() {
        if (mShowingPage - 1 < 0) {
            return;
        }
        showMenuInPage(--mShowingPage);
    }

    private void showMenuInPage(int page) {
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
    }

    private View getLineView() {
        View line = new View(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp2px(1f), LinearLayout.LayoutParams.MATCH_PARENT);
        line.setLayoutParams(lp);
        line.setBackgroundColor(Color.WHITE);
        return line;
    }


    public void hidePopupListWindow() {
        if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
            return;
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
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


        float triangleWidth = dp2px(10f);
        float triangleHeight = dp2pxFloat(6f);

        Drawable rightTriangleDrawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                Path path = new Path();
                path.moveTo(0f, 0f);
                path.lineTo(0f, triangleWidth);
                path.lineTo(triangleHeight, triangleWidth / 2);
                path.close();
                canvas.drawPath(path, paint);
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
                return (int) triangleHeight;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) triangleWidth;
            }
        };
        mNextPage.setCompoundDrawablesWithIntrinsicBounds(null, null, rightTriangleDrawable, null);

        Drawable leftTriangleDrawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                Path path = new Path();
                path.moveTo(triangleHeight, 0f);
                path.lineTo(triangleHeight, triangleWidth);
                path.lineTo(0f, triangleWidth / 2);
                path.close();
                canvas.drawPath(path, paint);
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
                return (int) triangleHeight;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) triangleWidth;
            }
        };
        mPreviousPage.setCompoundDrawablesWithIntrinsicBounds(leftTriangleDrawable, null, null, null);
    }


    public View getDefaultIndicatorView(Context context) {
        return getTriangleIndicatorView(context, dp2px(10), dp2px(6), DEFAULT_NORMAL_BACKGROUND_COLOR);
    }

    private View getTriangleIndicatorView(Context context, final float widthPixel
            , final float heightPixel, final int color) {
        ImageView indicator = new ImageView(context);
        Drawable drawable = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);
                path.moveTo(0f, 0f);
                path.lineTo(widthPixel, 0f);
                path.lineTo(widthPixel / 2, heightPixel);
                path.close();
                canvas.drawPath(path, paint);
            }

            @Override
            public void setAlpha(int alpha) {
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }

            @Override
            public int getIntrinsicWidth() {
                return (int) widthPixel;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) heightPixel;
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

}

