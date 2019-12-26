package com.testdemo.testRecyclerViewType

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.testdemo.R
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.ItemMoveMainActivity
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
}