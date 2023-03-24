package com.testdemo.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.testdemo.BaseFragment
import com.testdemo.R
import com.testdemo.testDatePicker.datepicker.utils.MeasureUtil
import kotlinx.coroutines.*

class CommonListFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.act_common_list_fragment, container, false)
        /*return RecyclerView(inflater.context).apply {
            layoutManager = LinearLayoutManager(inflater.context)
            setPadding(0, MeasureUtil.dp2px(activity, 30f), 0, 0)
            clipToPadding = false
        }*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_list)

        val listData = ArrayList<String>()
        for (i in 0..27) {
            listData.add("item$i")
        }
        recyclerView.adapter = CommonTextAdapter(listData)

        refreshLayout.setOnRefreshListener { // 下拉刷新
            Toast.makeText(activity, "开始加载...", Toast.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.IO).launch {
                delay(2 * 1000) // 模拟加载数据

                withContext(Dispatchers.Main) {
                    // 数据加载完了
                    Toast.makeText(activity, "加载结束！", Toast.LENGTH_SHORT).show()
                    refreshLayout.isRefreshing = false
                }
            }
        }

    }

}