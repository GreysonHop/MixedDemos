package com.testdemo

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.libraries.places.api.Places
import com.testdemo.testBlurAndGlass.BlurGlassSoOnActivity
import com.testdemo.testCanDragLayout.TestDragViewActivity
import com.testdemo.testCanDragScrollView.DraggableScrollViewAct
import com.testdemo.testCenterRefresh.CollapsingRecyclerActivity
import com.testdemo.testDateMsgLog.DateMsgLogAct
import com.testdemo.testDatePicker.CreateInfoAct
import com.testdemo.testFlipView.TestFlipperActivity
import com.testdemo.testGiftAnim.TestGiftAnimAct
import com.testdemo.testMap.TestMapAct
import com.testdemo.testNCalendar.TestNCalendarAct
import com.testdemo.testPictureSelect.TestPictureSelectAct
import com.testdemo.testRecyclerViewType.TestRecyclerViewAct
import com.testdemo.testShader.TestShaderAct
import com.testdemo.testSpecialEditLayout.SpecialEditLayoutAct
import com.testdemo.testStartMode.ActivityA
import com.testdemo.testVerticalScrollView.TestActivity3

/**
 * Create by Greyson on 2019/9/14
 */
class MainActivity : ListActivity() {

    private val classNameList = mutableListOf<String>()
    private val classList = mutableListOf<Class<out Activity>>()

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
        menuListMap["万年历"] = TestNCalendarAct::class.java
        menuListMap["定位、地图显示、第三方调用等"] = TestMapAct::class.java
        menuListMap["FlipperView测试"] = TestFlipperActivity::class.java
        menuListMap["RecyclerView特效"] = TestRecyclerViewAct::class.java
        menuListMap["RecyclerView实现的日历"] = DateMsgLogAct::class.java
        menuListMap["测试启动模式"] = ActivityA::class.java


        classNameList.addAll(menuListMap.keys)
        classList.addAll(menuListMap.values)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classNameList)
        listAdapter = adapter

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_map_api_key))
        }

        // 注册监听系统时间12/24小时制的变动
        contentResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.TIME_12_24), true, object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                val timeFormat = Settings.System.getInt(contentResolver, Settings.System.TIME_12_24, 24)
                Log.d("greyson", "监听系统小时制变化，timeFormat:$timeFormat")
                var mTimeFormat = 12
                if (12 == timeFormat) {
                    mTimeFormat = 12
                } else if (24 == timeFormat) {
                    mTimeFormat = 24
                }

            }
        })
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        if (position == 0) {
            val intent = Intent()
            intent.setClass(this, classList[position])
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            return
        }

        startActivity(Intent(this, classList[position]))
    }
}