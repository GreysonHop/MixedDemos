package com.testdemo.testNestedScroll.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;


public class ImageBehavior extends CoordinatorLayout.Behavior<ImageView> {

    public ImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull ImageView child, @NonNull View dependency) {
        Log.i("ImageBehavior", "layoutDependsOn:dependency= " + dependency + ", child=" + child);
//        return dependency instanceof RecyclerView;
        return dependency instanceof AppBarLayout;
//        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ImageView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.d("ImageBehavior", "onStartNestedScroll: child=" + child.getClass().getName() + ", directTargetChild=" + directTargetChild + ", type = " +type);
        return true;
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ImageView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        Log.d("ImageBehavior", "onNestedScroll:dxCon="+dxConsumed+", dyCon="+dyConsumed+", dxUnCon="+dxUnconsumed+", dyUnCon="+dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull ImageView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        child.setTranslationX(dy);
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        Log.d("ImageBehavior", "onNestedPreScroll:dxCon=$dxConsumed, dyCon=$dyConsumed, dxUnCon=$dxUnconsumed, dyUnCon=$dyUnconsumed");
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull ImageView child, @NonNull View dependency) {
        if (dependency instanceof AppBarLayout) {
            AppBarLayout abl = (AppBarLayout) dependency;
            float offset = abl.getY();
            child.setTranslationX(Math.abs(offset));
            Log.v("ImageBehavior", "onDependentViewChanged;: " + abl.getY());
        }
        Log.w("ImageBehavior", "dependency= " + dependency + ", child=" + child);
        return super.onDependentViewChanged(parent, child, dependency);
    }

}
