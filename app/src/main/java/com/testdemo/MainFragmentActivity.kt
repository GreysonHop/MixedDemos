package com.testdemo

import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.google.android.libraries.places.api.Places

/**
 * Create by Greyson on 2019/9/14
 */
class MainFragmentActivity : BaseCommonActivity() {


    private val observer = object : ContentObserver(Handler()) {
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
    }

    override fun getLayoutView(): View {
        return FrameLayout(this).also {
            it.id = R.id.anim_sets_frag
        } // 主页Activity显示所有fragment，所以只需要一个显示容器作为主页的布局
    }

    override fun initView() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.anim_sets_frag, MainListFragment().apply {
                arguments = Bundle().also {
                    it.putString(
                        MainListFragment.ARGUMENT_TYPE,
                        MainListFragment.TYPE_MAIN
                    )
                }
            })
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("greyson", "MainActivity-onCreate: savedInstanceState = $savedInstanceState, task = $taskId")

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_map_api_key))
        }

        // 注册监听系统时间12/24小时制的变动
        contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.TIME_12_24), true, observer
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(observer)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("greyson", "newConfig: ${newConfig}")
    }
}