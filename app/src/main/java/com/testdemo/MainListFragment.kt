package com.testdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.architecture.TestDataBindingAct
import com.testdemo.fileAccess.FileAccessMenuAct
import com.testdemo.testAnim.AnimationSetsAct
import com.testdemo.testAnim.ConstraintAnimAct
import com.testdemo.testAnim.activityAnim.DialogActivity
import com.testdemo.testAnim.svgaAnim.TestGiftAnimAct
import com.testdemo.testBlurAndGlass.BlurGlassSoOnActivity
import com.testdemo.testCanDragLayout.TestDragViewActivity
import com.testdemo.testCanDragScrollView.DraggableScrollViewAct
import com.testdemo.testCenterRefresh.CollapsingRecyclerActivity
import com.testdemo.testCenterRefresh.MyAdapter
import com.testdemo.testDateMsgLog.DateMsgLogAct
import com.testdemo.testDatePicker.CreateInfoAct
import com.testdemo.testFlipView.TestFlipperActivity
import com.testdemo.testMap.TestMapAct
import com.testdemo.testNCalendar.TestNCalendarAct
import com.testdemo.testPictureSelect.TestPictureSelectAct
import com.testdemo.testRecyclerViewType.TestRecyclerViewAct
import com.testdemo.testSpecialEditLayout.SpecialEditLayoutAct
import com.testdemo.testStartMode.ActivityA
import com.testdemo.testVerticalScrollView.TestMyVerticalViewPageAct
import com.testdemo.testView.canvas.TestCanvasActivity
import com.testdemo.testView.doodle.TestDoodleAct
import com.testdemo.testView.nineView.TestNineViewAct
import com.testdemo.testView.shader.TestShaderAct
import com.testdemo.webrtc.webrtctest.TestAVChatActivity

/**
 * Create by Greyson on 2022/02/20
 * 把例子的类型数据都写在Fragment中，提供参数让外部来决定要在fragment的列表中显示什么类型的数据
 */
class MainListFragment : BaseFragment() {

    private val adapter = MyAdapter(listOf()).apply {
        clickCallBack = { view, position -> itemClickListener(view, position)}
    }

    private val classNameList = mutableListOf<String>()
    private val menuListMap = linkedMapOf<String, Class<out Activity>>()

    companion object {
        const val ARGUMENT_TYPE = "argument:type"
        const val TYPE_MAIN = "list:type_main"
        const val TYPE_ANIM = "list:type_anim"
        const val TYPE_CANVAS = "list:type_canvas"
    }

    private fun itemClickListener(v: View, position: Int) {
        activity?.let {
            val intent = Intent()
            intent.setClass(it, menuListMap[classNameList[position]] as Class<*>)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return RecyclerView(inflater.context).apply {
            layoutManager = LinearLayoutManager(inflater.context)
            adapter = this@MainListFragment.adapter
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")


        arguments?.getString(ARGUMENT_TYPE)?.let { type ->
            when (type) {
                TYPE_ANIM -> initAnimData()
                else -> initMainData()
            }

            adapter.notifyDataSetChanged()
        }
    }


    private fun initMainData() {
        // 每次要在主页增加新的activity只需要在这里增加，不用管那个变量： classNameList
        menuListMap["高斯模糊和玻璃破碎效果"] = BlurGlassSoOnActivity::class.java
        menuListMap["动画实现弹窗"] = TestAnimationDialogAct::class.java
        menuListMap["文件操作相关"] = FileAccessMenuAct::class.java
        menuListMap["上下滑动切换界面的ViewPager？"] = TestMyVerticalViewPageAct::class.java
        menuListMap["可以在容器间拖曳的组件"] = TestDragViewActivity::class.java
        menuListMap["子View可被拉伸回弹的ScrollView"] = DraggableScrollViewAct::class.java
        menuListMap["中间刷新的List"] = CollapsingRecyclerActivity::class.java
        menuListMap["仿QQ横向图片选择器"] = TestPictureSelectAct::class.java
        menuListMap["常见聊天界面例子——可滚动缩放的编辑器"] = SpecialEditLayoutAct::class.java
        menuListMap["礼物动画框架例子SVGA"] = TestGiftAnimAct::class.java
        menuListMap["自定义组件中的shader应用和圆角ViewGroup"] = TestShaderAct::class.java
        menuListMap["自定义年月日时分秒选择器"] = CreateInfoAct::class.java
        menuListMap["万年历"] = TestNCalendarAct::class.java
        menuListMap["定位、地图显示、第三方调用等"] = TestMapAct::class.java
        menuListMap["FlipperView测试"] = TestFlipperActivity::class.java
        menuListMap["RecyclerView特效"] = TestRecyclerViewAct::class.java
        menuListMap["RecyclerView实现的日历"] = DateMsgLogAct::class.java
        menuListMap["测试启动模式"] = ActivityA::class.java
        menuListMap["测试绘画相关属性"] = TestCanvasActivity::class.java
        menuListMap["测试涂鸦相关功能"] = TestDoodleAct::class.java
        menuListMap["Constraint动画"] = ConstraintAnimAct::class.java
        menuListMap["DataBinding"] = TestDataBindingAct::class.java
        menuListMap["DialogActivity"] = DialogActivity::class.java
        menuListMap["NineView"] = TestNineViewAct::class.java
        menuListMap["多人聊天室"] = TestAVChatActivity::class.java
        menuListMap["所有动画在此--->"] = AnimationSetsAct::class.java
        menuListMap["一些绘制相关(draw、canvas)--->"] = AnimationSetsAct::class.java

        classNameList.addAll(menuListMap.keys)

        adapter.setNewData(classNameList)
    }

    private fun initAnimData() {

    }
}