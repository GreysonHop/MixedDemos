package com.testdemo.testAnim

import android.R as androidR
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import com.testdemo.TestAnimationDialogAct

/**
 * Create by Greyson on 2022/02/14
 */
class AnimationSetsFrag : ListFragment() {

    private val classNameList = mutableListOf<String>()
    private val menuListMap = linkedMapOf<String, Class<out Activity>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// TODO: 2022/2/14 换成真正的Activity
        menuListMap["Motion System（动画系统）"] = TestAnimationDialogAct::class.java
        menuListMap["使用过渡为布局变化添加动画效果"] = TestAnimationDialogAct::class.java


        classNameList.addAll(menuListMap.keys)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            listAdapter = ArrayAdapter<String>(it, androidR.layout.simple_list_item_1, classNameList)
        }

    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        activity?.let {
            val intent = Intent()
            intent.setClass(it, menuListMap[classNameList[position]] as Class<*>)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }
}