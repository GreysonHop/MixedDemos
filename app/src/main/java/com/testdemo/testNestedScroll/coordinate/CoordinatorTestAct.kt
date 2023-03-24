package com.testdemo.testNestedScroll.coordinate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.DrawableCompat
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
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView


class CoordinatorTestAct : BaseCommonActivity() {
    val APP_PRIMARY_COLOR = Color.WHITE
    val APP_SECONDARY_COLOR = Color.parseColor("#00c853")
    val SPECIAL_COLOR = Color.parseColor("#FD9001")

    val contentView: View by lazy { findViewById(R.id.rl_root) }
    val coordinator: CoordinatorLayout by lazy { findViewById(R.id.coordinator) }
    val appBar: AppBarLayout by lazy { findViewById(R.id.appBar) }
    val mainViewPage: ViewPager by lazy { findViewById(R.id.vp_main) }
    val magicIndicator: MagicIndicator by lazy { findViewById(R.id.tl_title) }
    val ivSearch: ImageView by lazy { findViewById(R.id.iv_search) }
    val ivTabShadow: ImageView by lazy { findViewById(R.id.iv_tab_shadow) }

    val tabTitleList = arrayListOf<SimplePagerTitleView>()

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
                        contentView.setBackgroundColor(SPECIAL_COLOR)
                        SystemUiUtils.switchLightStatusBar(window.decorView, false)
                        tabTitleList.forEach {
                            it.normalColor = Color.WHITE
                            it.selectedColor = Color.WHITE
                            it.setTextColor(Color.WHITE)
                        }

                    } else {
                        switchNormalPage(APP_PRIMARY_COLOR) // 模拟从列表获取背景色
                    }

                } else if (position == titleList.lastIndex - 1) { // 倒数第二个页面在滑动时
                    colorTranslate(positionOffset)

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
                    textSize = 15f
                    normalColor = Color.GRAY
                    selectedColor = Color.BLACK
                    setOnClickListener { mainViewPage.currentItem = index }

                    if (index == count - 1) {
                        lastTabView = this
                    }

                    tabTitleList.add(this)
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
                    val normalColor = APP_SECONDARY_COLOR
                    val specialColor = APP_PRIMARY_COLOR
                    setColors(normalColor, normalColor, normalColor, normalColor, normalColor, specialColor)

                    lastIndicator = this
                }
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, mainViewPage)

    }

    var lastTabView: View? = null
    var lastIndicator: LinePagerIndicator? = null

    override fun onDestroy() {
        super.onDestroy()
        ivSearch.animation?.cancel()
    }

    // 颜色渐变的组件
    private fun colorTranslate(percent: Float) {
        val endSpecColor = SPECIAL_COLOR
        val startSpecColor = APP_PRIMARY_COLOR
        val curSpecColor = ArgbEvaluatorHolder.eval(percent, startSpecColor, endSpecColor)
        contentView.setBackgroundColor(curSpecColor)
        SystemUiUtils.switchLightStatusBar(window.decorView, false)

        ivTabShadow.background.let {
            DrawableCompat.setTint(it, curSpecColor)
            ivTabShadow.setImageDrawable(it)
        }

        val endColor = APP_PRIMARY_COLOR
        val startColor = APP_SECONDARY_COLOR
        val curColor = ArgbEvaluatorHolder.eval(percent, startColor, endColor)
        ivSearch.drawable.let {
            DrawableCompat.setTint(it, curColor)
            ivSearch.setImageDrawable(it)
        }

    }

    private fun switchNormalPage(color: Int) {
        contentView.setBackgroundColor(color)
        SystemUiUtils.switchLightStatusBar(window.decorView, true)
        ivTabShadow.background.let {
            DrawableCompat.setTint(it, color)
            ivTabShadow.setImageDrawable(it)
        }

        tabTitleList.forEach {
            it.normalColor = Color.GRAY
            it.selectedColor = Color.BLACK
            it.setTextColor(Color.GRAY)
        }
    }

    private fun coordinatorListener() {
        ivSearch.setOnClickListener { Toast.makeText(this, "点击搜索", Toast.LENGTH_SHORT).show() }

        appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            searchBtnShow(appBarLayout, verticalOffset)

            /*val curX = ivSearch.translationX
            if (ivSearch.visibility == View.GONE && verticalOffset <= (-appBarLayout.totalScrollRange / 2f).toInt()) { // 显示
                switchSearchBtn()
            } else if (ivSearch.visibility == View.VISIBLE && curX <= 0 && verticalOffset >= 0) { // 隐藏
                switchSearchBtn()
            }*/

        })
    }

    private fun shadowShow() {
//        ivTabShadow.animate().translationX() // 阴影应该是跟着 TabLayout 显示 的。即Tab太短不足以显示 时，就会出现阴影，而不是因为有放大镜就有阴影！
    }

    var isShowingSearchBtn = false

    private fun searchBtnShow(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val curX = ivSearch.translationX
        Log.i("greyson", "appBarLayout's offset=$verticalOffset, ${appBarLayout.totalScrollRange}, curX=${curX}")
        if (curX >= ivSearch.width && verticalOffset <= (-appBarLayout.totalScrollRange / 2f).toInt() && !isShowingSearchBtn) { // 显示

            Log.w("greyson", "要显示放大镜了！")
            isShowingSearchBtn = true

            ivSearch.animate().translationX(0f).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    ivSearch.visibility = View.VISIBLE
                    ivTabShadow.visibility = View.VISIBLE

                }

                override fun onAnimationEnd(animation: Animator?) {
                    isShowingSearchBtn = false

                    val navigator = (magicIndicator.navigator as CommonNavigator)
                    navigator.mScrollView.let {
                        Log.d("greyson", "显示时-onAnimationEnd: scrollView.width=${it.width}, magicIndicator.width=${magicIndicator.width}")

                        val backup = it.scrollX
                        it.setPadding(1, 0, MeasureUtil.dp2px(baseContext, 40f), 0)
                        it.clipToPadding = false

                        it.postDelayed({
                            Log.w("greyson", "自己滚动: x=$backup")
//                        it.scrollTo(backup, it.scrollY)
                            it.scrollX = backup
                        }, 300)
                    }

                }
            }).start()
            ivSearch.animate()

        } else if (curX <= 0 && verticalOffset >= 0) { // 隐藏
            Log.w("greyson", "要隐藏放大镜了！")
            isShowingSearchBtn = false

            ivSearch.animate().translationX(ivSearch.width.toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    ivSearch.visibility = View.GONE
                    ivTabShadow.visibility = View.GONE
                    (magicIndicator.navigator as CommonNavigator).mScrollView.let {
                        Log.d("greyson", "隐藏时onAnimationEnd: scrollView.width=${it.width}, magicIndicator.width=${magicIndicator.width}")

                        it.setPadding(0, 0, 0, 0)
//                        it.clipToPadding = false
                    }
                }
            }).start()

        }
    }

    private fun switchSearchBtn() {
        // 补间动画版
        if (ivSearch.visibility == View.GONE) { // to show
            val translateInAnimation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f
            )
            translateInAnimation.duration = 400
            ivSearch.animation = translateInAnimation
            ivTabShadow.animation = translateInAnimation

            ivSearch.visibility = View.VISIBLE
            ivTabShadow.visibility = View.VISIBLE

        } else {
            val translateOutAnimation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            ).apply {
                duration = 400
                setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        ivSearch.visibility = View.GONE
                        ivTabShadow.visibility = View.GONE
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }
            ivSearch.animation = translateOutAnimation
            ivTabShadow.animation = translateOutAnimation

            translateOutAnimation.startNow()

        }

        /*val animator: Animator = ViewAnimationUtils.createCircularReveal(myView, x, y, 0, radius)
        animator.duration = 1000*/ // 有没有线性的揭露动画？而不是圆圈的
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