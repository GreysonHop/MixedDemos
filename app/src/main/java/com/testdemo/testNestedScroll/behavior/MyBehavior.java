package com.testdemo.testNestedScroll.behavior;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;


public class MyBehavior extends CoordinatorLayout.Behavior<RecyclerView> {

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency) {
        Log.d("MyBehavior", "dependency= " + dependency + ", child=" + child);
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency) {
        Log.d("MyBehavior", "dependency= " + dependency + ", child=" + child);
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
