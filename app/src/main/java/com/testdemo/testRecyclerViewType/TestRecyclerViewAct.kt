package com.testdemo.testRecyclerViewType

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R
import com.testdemo.testPictureSelect.imageLoader.MediaBean
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.ItemMoveMainActivity
import com.testdemo.testRecyclerViewType.layoutManager.NineAdapter
import com.testdemo.testRecyclerViewType.layoutManager.NineLayoutManager
import kotlinx.android.synthetic.main.act_test_recyclerview.*

/**
 * Create by Greyson on 2019/12/09
 */
class TestRecyclerViewAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_recyclerview)

        val itemTouchHelper = ItemTouchHelper(SimpleItemTouchCallback())
        itemTouchHelper.attachToRecyclerView(rv_list)

        btn_goto_other_demo.setOnClickListener { startActivity(Intent(this, ItemMoveMainActivity::class.java)) }

        initListView()
    }

    inner class SimpleItemTouchCallback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            Log.i("greyson:", "")
            return ItemTouchHelper.DOWN
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }
    }

    fun initListView() {
        val userList = ArrayList<MediaBean>()

        userList.add(MediaBean().apply {
            folderName = "444"
            type = R.drawable.img4
        })
        userList.add(MediaBean().apply {
            folderName = "555"
            type = R.drawable.img5
        })
        userList.add(MediaBean().apply {
            folderName = "666"
            type = R.drawable.img6
        })
        /*userList.add(MediaBean().apply {
            type = R.drawable.ic_launcher
        })
        userList.add(MediaBean().apply {
            type = R.drawable.galata
        })

        for (i in 0..6) {
            userList.add(MediaBean().apply {
                type = R.drawable.call_icon_gift
            })
        }*/
        rv_list.layoutManager = NineLayoutManager().apply { gap = 30 }
        rv_list.adapter = NineAdapter().apply { setDataList(userList) }
    }
}