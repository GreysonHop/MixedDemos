package com.testdemo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.testdemo.testCanDragLayout.TestDragViewActivity;
import com.testdemo.testCanDragScrollView.DraggableScrollViewAct;
import com.testdemo.testCenterRefresh.CollapsingRecyclerActivity;
import com.testdemo.testCenterRefresh.CollapsingToolbarLayoutActivity;
import com.testdemo.testPictureSelect.TestPictureSelectAct;
import com.testdemo.testVerticalScrollView.TestActivity3;

import java.util.ArrayList;

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
        classList.add(DraggableScrollViewAct.class);
        classList.add(CollapsingRecyclerActivity.class);
        classList.add(TestPictureSelectAct.class);

        classNameList.add("高斯模糊和玻璃破碎效果");
        classNameList.add("动画实现弹窗");
        classNameList.add("上下滑动切换界面的ViewPager？");
        classNameList.add("可以在容器间拖曳的组件");
        classNameList.add("子View可被拉伸回弹的ScrollView");
        classNameList.add("中间刷新的List");
        classNameList.add("仿QQ横向图片选择器");

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
