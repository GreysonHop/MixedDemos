package com.testdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.testdemo.testCanDragLayout.TestDragViewActivity;
import com.testdemo.testVerticalScrollView.TestActivity3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/25.
 */
public class TestActivity extends ListActivity {

    ArrayList<Class> classList = new ArrayList<Class>();
    ArrayList<String> classNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classList.add(MainActivity.class);
        classList.add(TestActivity2.class);
        classList.add(TestActivity3.class);
        classList.add(TestDragViewActivity.class);

        classNameList.add("高斯模糊和玻璃破碎效果");
        classNameList.add("动画实现弹窗");
        classNameList.add("上下滑动切换界面的ViewPager？");
        classNameList.add("可以在容器间拖曳的组件");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classNameList);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent();
        intent.setClass(this, classList.get(position));
        startActivity(intent);
    }
}
