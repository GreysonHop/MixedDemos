package com.testdemo.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.BaseFragment
import com.testdemo.testDatePicker.datepicker.utils.MeasureUtil

class CommonListFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return RecyclerView(inflater.context).apply {
            layoutManager = LinearLayoutManager(inflater.context)
            setPadding(0, MeasureUtil.dp2px(activity, 30f), 0, 0)
            clipToPadding = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listData = ArrayList<String>()
        for (i in 0..27) {
            listData.add("item$i")
        }
        if (view is RecyclerView) {
            view.adapter = CommonTextAdapter(listData)
        }
    }

}