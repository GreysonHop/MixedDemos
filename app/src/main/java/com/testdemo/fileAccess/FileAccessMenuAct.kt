package com.testdemo.fileAccess

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.BaseCommonActivity
import com.testdemo.fileAccess.media.ShowRecentMediaAct
import com.testdemo.testCenterRefresh.MyAdapter

/**
 * Create by Greyson on 2021/05/13
 */
class FileAccessMenuAct : BaseCommonActivity() {
    private val classNameList = mutableListOf<String>()
    private val menuListMap = linkedMapOf<String, Class<out Activity>>()

    lateinit var actList: RecyclerView

    override fun getLayoutView(): View? {
        actList = RecyclerView(this)
        return actList
    }

    override fun initView() {
        actList.apply {
            layoutManager = LinearLayoutManager(this@FileAccessMenuAct)
        }
    }

    override fun initData() {
        menuListMap["仿微信提示最新图片悬浮窗"] = ShowRecentMediaAct::class.java

        classNameList.addAll(menuListMap.keys)
        actList.adapter = MyAdapter(classNameList).also {
            it.clickCallBack = { _, pos ->
                startActivity(Intent(this, menuListMap[classNameList[pos]] as Class<*>))
            }
        }
    }

}