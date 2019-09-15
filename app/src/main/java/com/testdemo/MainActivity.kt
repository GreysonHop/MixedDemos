package com.testdemo

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.testdemo.testBlurAndGlass.BlurGlassSoOnActivity
import com.testdemo.testCanDragLayout.TestDragViewActivity
import com.testdemo.testCanDragScrollView.DraggableScrollViewAct
import com.testdemo.testCenterRefresh.CollapsingRecyclerActivity
import com.testdemo.testDatePicker.CreateInfoAct
import com.testdemo.testGiftAnim.TestGiftAnimAct
import com.testdemo.testPictureSelect.TestPictureSelectAct
import com.testdemo.testShader.TestShaderAct
import com.testdemo.testSpecialEditLayout.SpecialEditLayoutAct
import com.testdemo.testVerticalScrollView.TestActivity3

/**
 * Create by Greyson on 2019/9/14
 */
class MainActivity : ListActivity() {

    val classNameList = mutableListOf<String>()
    val classList = mutableListOf<Class<out Activity>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val menuListMap = linkedMapOf<String, Class<out Activity>>()
        menuListMap.put("高斯模糊和玻璃破碎效果", BlurGlassSoOnActivity::class.java)
        menuListMap["动画实现弹窗"] = TestActivity2::class.java
        menuListMap["上下滑动切换界面的ViewPager？"] = TestActivity3::class.java
        menuListMap["可以在容器间拖曳的组件"] = TestDragViewActivity::class.java
        menuListMap["子View可被拉伸回弹的ScrollView"] = DraggableScrollViewAct::class.java
        menuListMap["中间刷新的List"] = CollapsingRecyclerActivity::class.java
        menuListMap["仿QQ横向图片选择器"] = TestPictureSelectAct::class.java
        menuListMap["可滚动缩放的编辑器"] = SpecialEditLayoutAct::class.java
        menuListMap["礼物动画框架例子SVGA"] = TestGiftAnimAct::class.java
        menuListMap["自定义组件中的shader应用和圆角ViewGroup"] = TestShaderAct::class.java
        menuListMap["自定义年月日时分秒选择器"] = CreateInfoAct::class.java

        classNameList.addAll(menuListMap.keys)
        classList.addAll(menuListMap.values)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classNameList)
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        val intent = Intent()
        intent.setClass(this, classList[position])
        startActivity(intent)
    }
}