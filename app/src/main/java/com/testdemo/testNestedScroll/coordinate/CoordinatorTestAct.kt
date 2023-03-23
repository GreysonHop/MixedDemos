package com.testdemo.testNestedScroll.coordinate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.tabs.TabLayout
import com.testdemo.BaseCommonActivity
import com.testdemo.R
import com.testdemo.testDatePicker.datepicker.utils.MeasureUtil
import com.testdemo.util.CommonFragment
import com.testdemo.util.CommonFragmentPageAdapter
import com.testdemo.util.CommonListFragment
import com.testdemo.util.SystemUiUtils
import kotlinx.android.synthetic.main.act_coordinator_test.*
import kotlinx.android.synthetic.main.act_dialog.*
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class CoordinatorTestAct : BaseCommonActivity() {

    val contentView: View by lazy { findViewById(R.id.rl_root) }
    val coordinator: CoordinatorLayout by lazy { findViewById(R.id.coordinator) }
    val appBar: AppBarLayout by lazy { findViewById(R.id.appBar) }
    val mainViewPage: ViewPager by lazy { findViewById(R.id.vp_main) }
    val magicIndicator: MagicIndicator by lazy { findViewById(R.id.tl_title) }
    val ivSearch: ImageView by lazy { findViewById(R.id.iv_search) }

    override fun getLayoutResId(): Int {
        return R.layout.act_coordinator_test
    }

    override fun initView() {
        setStatusBar()
        SystemUiUtils.switchLightStatusBar(window.decorView, true)

        coordinatorListener()

        val titleList = arrayListOf("推荐", "新游", "预约", "排行", "直播", "闪烁之光")
        val list = arrayListOf<Fragment>()
        for (i in 0..5) {
            val mo = i % 3
            if (mo == 1) {
                list.add(CommonFragment())
            } else {
                list.add(CommonListFragment())
            }
        }
        val adapter = CommonFragmentPageAdapter(supportFragmentManager, list)

        mainViewPage.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.i("greyson", "position=$position, offset=$positionOffset, pixel=$positionOffsetPixels")
                if (positionOffsetPixels == 0) { // 停在某一页面时
                    if (position == titleList.lastIndex) { // 最后一个页面
                        contentView.setBackgroundColor(Color.LTGRAY)
                        SystemUiUtils.switchLightStatusBar(window.decorView, false)
                    } else {
                        contentView.setBackgroundColor(Color.WHITE)
                        SystemUiUtils.switchLightStatusBar(window.decorView, true)
                    }

                } else if (position == titleList.lastIndex - 1) { // 倒数第二个页面在滑动时
                    val endColor = Color.LTGRAY
                    val startColor = Color.WHITE
                    val offset = endColor - startColor
                    val curColor = ArgbEvaluatorHolder.eval(positionOffset, startColor, endColor)
//                    val curColor = (offset * positionOffset + startColor).toInt()
                    contentView.setBackgroundColor(curColor)
                    SystemUiUtils.switchLightStatusBar(window.decorView, false)

                }
            }

            override fun onPageSelected(position: Int) {
                Log.i("greyson", "onPageSelected: position=$position")
            }
        })
        mainViewPage.adapter = adapter
//        setupSelfTabLayout(viewPagerMain)


        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return list.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                return ColorTransitionPagerTitleView(context).apply {
                    text = titleList[index]
                    normalColor = Color.GRAY
                    selectedColor = Color.BLACK
                    setOnClickListener { mainViewPage.currentItem = index }
                }
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                return LinePagerIndicator(context).apply {
                    roundRadius = MeasureUtil.dp2px(context, 3f).toFloat()
                    mode = LinePagerIndicator.MODE_EXACTLY
                    lineWidth = MeasureUtil.dp2px(context, 15f).toFloat()
                    lineHeight = MeasureUtil.dp2px(context, 4f).toFloat()
                    yOffset = MeasureUtil.dp2px(context, 5f).toFloat()
                    startInterpolator = AccelerateInterpolator()
                    endInterpolator = DecelerateInterpolator(1.6f)
                    val specialColor = Color.parseColor("#00c853")
                    val normalColor = Color.WHITE
                    setColors(normalColor,
                            normalColor,
                            Color.parseColor("#00c852"),
                            Color.parseColor("#00c853"),
                            Color.parseColor("#00c852"),
                            specialColor)
                }
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mainViewPage)

    }

    private fun coordinatorListener() {
        iv_search.setOnClickListener { Toast.makeText(this, "点击搜索", Toast.LENGTH_SHORT).show() }

        appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val curX = iv_search.translationX
            Log.i("greyson", "appBarLayout's offset=$verticalOffset, ${appBarLayout.totalScrollRange}, curX=${curX}")

            /*if (curX >= ivSearch.width && verticalOffset <= (-appBarLayout.totalScrollRange / 2f).toInt()) { // 显示

                iv_search.animate().translationX(0f).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        iv_search.visibility = View.VISIBLE
                    }
                }).start()

            } else if (curX <= 0 && verticalOffset >= 0) { // 隐藏

                iv_search.animate().translationX(iv_search.width.toFloat()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        iv_search.visibility = View.GONE
                    }
                }).start()

            }*/

            if (iv_search.visibility == View.GONE && verticalOffset <= (-appBarLayout.totalScrollRange / 2f).toInt()) { // 显示
                switchSearchBtn()
            } else if (iv_search.visibility == View.VISIBLE && curX <= 0 && verticalOffset >= 0) { // 隐藏
                switchSearchBtn()
            }

        })
    }

    private fun switchSearchBtn() {
        if (ivSearch.visibility == View.GONE) { // to show
            val translateInAnimation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f
            )
            translateInAnimation.duration = 300
            ivSearch.animation = translateInAnimation

            ivSearch.visibility = View.VISIBLE

        } else {
            val translateOutAnimation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            )
            translateOutAnimation.duration = 300
            ivSearch.animation = translateOutAnimation

            ivSearch.visibility = View.GONE

        }
    }

    private fun setupSelfTabLayout() {
        val tabLayout = findViewById<TabLayout>(R.id.tl_title)

        tabLayout.setupWithViewPager(mainViewPage)

        /*for (i in 0..5) {
            tabLayout.let {
                val tab = it.newTab() // 如果没有绑定 VP，就得自己创建
                tab.text = "text$i"
                it.addTab(tab)
            }
        }*/
        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.text = "text$i"
        }
    }
}