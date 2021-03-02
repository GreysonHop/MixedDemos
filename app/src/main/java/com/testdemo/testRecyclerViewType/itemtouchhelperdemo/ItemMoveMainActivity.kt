package com.testdemo.testRecyclerViewType.itemtouchhelperdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.testdemo.R
import com.testdemo.testRecyclerViewType.gridpagersanphelper.vertical.VerticalRVActivity
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList.TestDragListAct
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.demochannel.ChannelActivity
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.demodrag.DragActivity
import kotlinx.android.synthetic.main.activity_main_item_move.*

/**
 * Created by YoKeyword on 16/1/4.
 */
class ItemMoveMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_item_move)

        btn_contact.setOnClickListener { startActivity(Intent(this, TestDragListAct::class.java)) }
        btn_drag.setOnClickListener { startActivity(Intent(this, DragActivity::class.java)) }
        btn_channel.setOnClickListener { startActivity(Intent(this, ChannelActivity::class.java)) }
        btn_grid_pager.setOnClickListener { startActivity(Intent(this, VerticalRVActivity::class.java)) }
    }
}