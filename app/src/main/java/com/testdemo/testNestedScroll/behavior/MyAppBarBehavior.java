package com.testdemo.testNestedScroll.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;


public class MyAppBarBehavior extends AppBarLayout.ScrollingViewBehavior {

    public MyAppBarBehavior() {

    }

    public MyAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /*@Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull View dependency) {
        Log.d("GreysonAppBarBehavior", "dependency= " + dependency);

        return super.onDependentViewChanged(parent, child, dependency);
    }*/

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        Log.w("GreysonAppBarBehavior", "layoutDepend: dependency= " + dependency + ", child=" + child);
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        Log.e("GreysonAppBarBehavior", "dependency= " + dependency + ", child=" + child);
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
