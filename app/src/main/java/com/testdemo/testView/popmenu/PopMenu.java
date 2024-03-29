package com.testdemo.testView.popmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.testdemo.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 弹出菜单
 * Created by Joe on 16/12/10
 */
public class PopMenu {


    /**
     * 默认的列数为4个
     */
    private static final int DEFAULT_COLUMN_COUNT = 3;

    /**
     * 动画时间
     */
    private static final int DEFAULT_DURATION = 300;

    /**
     * 拉力系数
     */
    private static final int DEFAULT_TENSION = 10;
    /**
     * 摩擦力系数
     */
    private static final int DEFAULT_FRICTION = 5;

    /**
     * item水平之间的间距
     */
    private static final int DEFAULT_HORIZONTAL_PADDING = 40;
    /**
     * item竖直之间的间距
     */
    private static final int DEFAULT_VERTICAL_PADDING = 15;


    /**
     * 文字大小
     */
    private int textSize = -1;

    /**
     * 文字颜色 资源R.Color.颜色值
     */
    private int textColor = -1;

    public static int getDefaultColumnCount() {
        return DEFAULT_COLUMN_COUNT;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    private WeakReference<Activity> weakReference;
    private int mColumnCount;
    private List<PopMenuItem> mMenuItems = new ArrayList<>();
    private RelativeLayout mAnimateLayout;
    private GridLayout mGridLayout;
    private ImageView mCloseIv;
    private int mDuration;
    private double mTension;
    private double mFriction;
    private int mHorizontalPadding;
    private int mVerticalPadding;
    private PopMenuItemListener mPopMenuItemListener;
    private boolean isCloseVisible = true;
    private Bitmap overlay = null;
    private Bitmap mBitmap = null;

    public boolean isCloseVisible() {
        return isCloseVisible;
    }

    public void setCloseVisible(boolean visible) {
        this.isCloseVisible = visible;
    }

    private int mScreenWidth;
    private int mScreenHeight;


    /**
     * 返回相应的menuitem
     */
    public PopSubView getMenuItem(int i) {
        PopSubView subView = null;
        try {
            subView = (PopSubView) mGridLayout.getChildAt(i);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (subView != null)
            return subView;
        else
            return null;

    }


    public int getmBackGroundColor() {
        return mBackGroundColor;
    }

    public void setmBackGroundColor(int mBackGroundColor) {
        this.mBackGroundColor = mBackGroundColor;
    }

    /**
     * 透明背景
     */
    public void setmBackGroundTrasnparent() {

        this.mBackGroundColor = Color.parseColor("#00ffffff");
    }


    public int getmCloseButtomResourceid() {
        return mCloseButtomResourceid;
    }

    public void setmCloseButtomResourceid(int mCloseButtomResourceid) {
        this.mCloseButtomResourceid = mCloseButtomResourceid;
    }

    public int getCloseMenuMarginbottom() {
        return mCloseMenuMarginbottom;
    }

    public void setCloseMenuMarginbottom(int mCloseMenuMarginbottom) {
        this.mCloseMenuMarginbottom = mCloseMenuMarginbottom;
    }

    /**
     * 关闭按钮距离屏幕底部位置单位dp
     */
    private int mCloseMenuMarginbottom = 50;

    /**
     * 背景颜色
     */
    private int mBackGroundColor = Color.parseColor("#f0f3f3f3");

    /**
     * 关闭按钮的图片
     */
    private int mCloseButtomResourceid = R.drawable.ic_close;

    /**
     * Menu相对于屏幕顶部的距离（去掉菜单本身高度剩下部分除以这个倍数因子）
     */

    private float mMarginTopRemainSpace = 1.5f;

    /**
     * 是否错位弹出菜单
     */
    private boolean mIsmalpositionAnimatOut = true;

    /**
     * 错位动画时间（毫秒）默认50
     */
    private int malposition = 50;


    private boolean isShowing = false;

    public float getmMarginTopRemainSpace() {
        return mMarginTopRemainSpace;
    }

    public void setmMarginTopRemainSpace(float mMarginTopRemainSpace) {
        this.mMarginTopRemainSpace = mMarginTopRemainSpace;
    }

    public boolean ismIsmalpositionAnimatOut() {
        return mIsmalpositionAnimatOut;
    }

    public void setmIsmalpositionAnimatOut(boolean mIsmalpositionAnimatOut) {
        this.mIsmalpositionAnimatOut = mIsmalpositionAnimatOut;
    }


    public int getMalposition() {
        return malposition;
    }

    public void setMalposition(int malposition) {
        this.malposition = malposition;
    }

    private SpringSystem mSpringSystem;

    {
        mSpringSystem = SpringSystem.create();
    }

    private PopMenu(Builder builder) {
        this.weakReference = builder.weakReference;
        this.mMenuItems.clear();
        this.mMenuItems.addAll(builder.itemList);

        this.mColumnCount = builder.columnCount;
        this.mDuration = builder.duration;
        this.mTension = builder.tension;
        this.mFriction = builder.friction;
        this.mHorizontalPadding = builder.horizontalPadding;
        this.mVerticalPadding = builder.verticalPadding;
        this.mPopMenuItemListener = builder.popMenuItemListener;

        mScreenWidth = weakReference.get().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = weakReference.get().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 显示菜单
     */
    public void show() {
        buildAnimateGridLayout();

        if (mAnimateLayout.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) mAnimateLayout.getParent();
            viewGroup.removeView(mAnimateLayout);
        }

        ViewGroup decorView = (ViewGroup) weakReference.get().getWindow().getDecorView();
        decorView.addView(mAnimateLayout);

        // decorView.setPadding(0,0,0,getNavigationBarHeight(mActivity));
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mAnimateLayout.getLayoutParams();
        lp.setMargins(0, 0, 0, 0/*getNavigationBarHeight(weakReference.get())*/);
        mAnimateLayout.setLayoutParams(lp);

        //执行显示动画
        showSubMenus(mGridLayout);

        isShowing = true;
    }

    /**
     * 隐藏菜单
     */
    public void hide() {
        //先执行消失的动画
        if (isShowing && mGridLayout != null) {
            hideSubMenus(mGridLayout, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewGroup decorView = (ViewGroup) weakReference.get().getWindow().getDecorView();
                    decorView.removeView(mAnimateLayout);
                    overlay = null;
                }
            });
            isShowing = false;
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    /**
     * 构建动画布局
     */
    private void buildAnimateGridLayout() {
        mAnimateLayout = new RelativeLayout(weakReference.get());
        mAnimateLayout.setBackground(new BitmapDrawable(weakReference.get().getResources(), blur()));
        mAnimateLayout.setOnClickListener(v -> hide());
        mAnimateLayout.setClipChildren(false);

        //关闭按钮
        int closeIvID = 0x11011;
        mCloseIv = new ImageView(weakReference.get());
        mCloseIv.setId(closeIvID);
        mCloseIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mCloseIv.setImageResource(mCloseButtomResourceid);
        mCloseIv.setOnClickListener(v -> hide());
        if (isCloseVisible) {
            mCloseIv.setVisibility(View.VISIBLE);
        } else {
            mCloseIv.setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.bottomMargin = dp2px(weakReference.get(), mCloseMenuMarginbottom);
        mAnimateLayout.addView(mCloseIv, layoutParams);

        // 底部item
        mGridLayout = new GridLayout(weakReference.get());
        mGridLayout.setColumnCount(mColumnCount);
        mGridLayout.setClipChildren(false);
//        mGridLayout.setClipToOutline(false);
//        mGridLayout.setClipToPadding(false);
//        mGridLayout.setBackgroundColor(mBackGroundColor);
        int hPadding = dp2px(weakReference.get(), mHorizontalPadding);
        int vPadding = dp2px(weakReference.get(), mVerticalPadding);
        int itemWidth = (mScreenWidth - (mColumnCount + 1) * hPadding) / mColumnCount;

        int rowCount = mMenuItems.size() % mColumnCount == 0 ? mMenuItems.size() / mColumnCount :
                mMenuItems.size() / mColumnCount + 1;

        int topMargin = (int) ((mScreenHeight - (itemWidth + vPadding) * rowCount + vPadding) / mMarginTopRemainSpace);

        for (int i = 0; i < mMenuItems.size(); i++) {
            final int position = i;
            PopSubView subView = new PopSubView(weakReference.get());
            if (textColor != -1) {
                subView.getTextView().setTextColor(weakReference.get().getResources().getColor(textColor));

            }
            if (textSize != -1) {
                subView.getTextView().setTextSize(textSize);

            }
            PopMenuItem menuItem = mMenuItems.get(i);
            subView.setPopMenuItem(menuItem);
            subView.setOnClickListener(v -> {
                if (mPopMenuItemListener != null) {
                    mPopMenuItemListener.onItemClick(PopMenu.this, position);
                }
                hide();
            });

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = itemWidth;
            lp.leftMargin = hPadding;

            /*if (i / mColumnCount == 0) {
                lp.topMargin = topMargin;
            } else {
                lp.topMargin = vPadding;
            }*/
            mGridLayout.addView(subView, lp);
        }

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams2.addRule(RelativeLayout.ABOVE, closeIvID);
        layoutParams2.bottomMargin = dp2px(weakReference.get(), 40);
        layoutParams2.leftMargin = dp2px(weakReference.get(), 10);//这里先写死
        layoutParams2.rightMargin = dp2px(weakReference.get(), 10);

        mAnimateLayout.addView(mGridLayout, layoutParams2);

    }

    private Bitmap blur() {
        View view = weakReference.get().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        mBitmap = Bitmap.createBitmap(view.getDrawingCache());
        float scaleFactor = 8;
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        view.setDrawingCacheEnabled(false);

//        BlurKit.getInstance().blur(overlay, 20);
        return overlay;
    }

    /**
     * show sub menus with animates
     */
    private void showSubMenus(ViewGroup viewGroup) {
        if (viewGroup == null) return;
        int childCount = viewGroup.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View view = viewGroup.getChildAt(i);
            view.setVisibility(View.INVISIBLE);

            animationAction(i, view);

        }
    }

    /**
     * 动画动作
     */
    private void animationAction(int i, final View view) {

        if (mIsmalpositionAnimatOut) {
            new Handler().postDelayed(() -> {

                view.setVisibility(View.VISIBLE);
                animateViewDirection(view, mScreenHeight, 0, mTension, mFriction);


            }, i * malposition);
        } else {

            view.setVisibility(View.VISIBLE);
            animateViewDirection(view, mScreenHeight, 0, mTension, mFriction);

        }
    }

    /**
     * hide sub menus with animates
     */
    private void hideSubMenus(ViewGroup viewGroup, final AnimatorListenerAdapter listener) {
        if (viewGroup == null) return;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.animate().translationY(mScreenHeight).setDuration(mDuration).setListener(listener).start();
        }
    }

    public void destroy() {
        if (null != overlay) {
            overlay.recycle();
            overlay = null;
            System.gc();
        }
        if (null != mBitmap) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
    }

    /**
     * 弹簧动画
     *
     * @param v        动画View
     * @param from     from
     * @param to       to
     * @param tension  拉力系数
     * @param friction 摩擦力系数
     */
    private void animateViewDirection(final View v, float from, float to, double tension, double friction) {
        Spring spring = mSpringSystem.createSpring();
        spring.setCurrentValue(from);
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, friction));
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                v.setTranslationY((float) spring.getCurrentValue());
            }
        });
        spring.setEndValue(to);
    }

    public static class Builder {

        private WeakReference<Activity> weakReference;
        private int columnCount = DEFAULT_COLUMN_COUNT;
        private List<PopMenuItem> itemList = new ArrayList<>();
        private int duration = DEFAULT_DURATION;
        private double tension = DEFAULT_TENSION;
        private double friction = DEFAULT_FRICTION;
        private int horizontalPadding = DEFAULT_HORIZONTAL_PADDING;
        private int verticalPadding = DEFAULT_VERTICAL_PADDING;
        private PopMenuItemListener popMenuItemListener;

        public Builder attachToActivity(Activity activity) {
            weakReference = new WeakReference<>(activity);
            return this;
        }

        public Builder columnCount(int count) {
            this.columnCount = count;
            return this;
        }

        public Builder addMenuItem(PopMenuItem menuItem) {
            this.itemList.add(menuItem);
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder tension(double tension) {
            this.tension = tension;
            return this;
        }

        public Builder friction(double friction) {
            this.friction = friction;
            return this;
        }

        public Builder horizontalPadding(int padding) {
            this.horizontalPadding = padding;
            return this;
        }

        public Builder verticalPadding(int padding) {
            this.verticalPadding = padding;
            return this;
        }

        public Builder setOnItemClickListener(PopMenuItemListener listener) {
            this.popMenuItemListener = listener;
            return this;
        }

        public PopMenu build() {
            return new PopMenu(this);
        }
    }

    //获取是否存在NavigationBar
  /*  public  boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }*/

    private int getNavigationBarHeight(Context context) {
        int showNavigationId = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        // 判断系统是否写入了关于是否显示虚拟导航栏的相关变量,如果为true，表示有虚拟导航栏
        try {
            Log.w("greyson1", "showNavigationBar: " + (showNavigationId > 0 && context.getResources().getBoolean(showNavigationId)));
        } catch (Exception e) {e.printStackTrace();}

        boolean hasPermanentMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        int resId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        try {
            Log.w("greyson1", "NavigationBarHeight: " + (resId > 0 ? context.getResources().getDimensionPixelSize(resId) : -1));
        } catch (Exception e) {e.printStackTrace();}

        return (resId > 0 && !hasPermanentMenuKey) ? context.getResources().getDimensionPixelSize(resId) : 0;
    }

    public boolean checkDeviceHasNavigationBar(Context context) {
        WindowManager windowManager = ((Activity) context).getWindowManager();

        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);

        int screenHeight = dm.heightPixels;
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(realDisplayMetrics);
        } else {
            Class c;
            try {
                c = Class.forName("android.view.Display");
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, realDisplayMetrics);
            } catch (Exception e) {
                realDisplayMetrics.setToDefaults();
                e.printStackTrace();
            }
        }

        int screenRealHeight = realDisplayMetrics.heightPixels;


        return (screenRealHeight - screenHeight) > 0;//screenRealHeight上面方法中有计算
    }


    /**
     * dp 2 px
     *
     * @param context context
     * @param dpVal   dp
     * @return px
     */
    protected int dp2px(Context context, int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
