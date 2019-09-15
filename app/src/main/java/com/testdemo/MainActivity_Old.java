package com.testdemo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.testdemo.testBlurAndGlass.BlurGlassSoOnActivity;
import com.testdemo.testCanDragLayout.TestDragViewActivity;
import com.testdemo.testCanDragScrollView.DraggableScrollViewAct;
import com.testdemo.testCenterRefresh.CollapsingRecyclerActivity;
import com.testdemo.testDatePicker.CreateInfoAct;
import com.testdemo.testGiftAnim.TestGiftAnimAct;
import com.testdemo.testPictureSelect.TestPictureSelectAct;
import com.testdemo.testShader.TestShaderAct;
import com.testdemo.testSpecialEditLayout.SpecialEditLayoutAct;
import com.testdemo.testVerticalScrollView.TestActivity3;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Greyson on 2018/1/25.
 */
public class MainActivity_Old extends ListActivity {

    ArrayList<String> classNameList = new ArrayList<>();
    ArrayList<Class> classList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinkedHashMap<String, Class> menuListMap = new LinkedHashMap<>();
//        HashMap<String, Class> menuListMap = new HashMap<>();
        menuListMap.put("高斯模糊和玻璃破碎效果", BlurGlassSoOnActivity.class);
        menuListMap.put("动画实现弹窗", TestActivity2.class);
        menuListMap.put("上下滑动切换界面的ViewPager？", TestActivity3.class);
        menuListMap.put("可以在容器间拖曳的组件", TestDragViewActivity.class);
        menuListMap.put("子View可被拉伸回弹的ScrollView", DraggableScrollViewAct.class);
        menuListMap.put("中间刷新的List", CollapsingRecyclerActivity.class);
        menuListMap.put("仿QQ横向图片选择器", TestPictureSelectAct.class);
        menuListMap.put("可滚动缩放的编辑器", SpecialEditLayoutAct.class);
        menuListMap.put("礼物动画框架例子SVGA", TestGiftAnimAct.class);
        menuListMap.put("自定义组件中的shader应用和圆角ViewGroup", TestShaderAct.class);
        menuListMap.put("自定义年月日时分秒选择器", CreateInfoAct.class);

        classNameList.addAll(menuListMap.keySet());
        classList.addAll(menuListMap.values());

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
