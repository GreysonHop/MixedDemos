package com.testdemo.testCanDragScrollView

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.testdemo.R
import kotlinx.android.synthetic.main.act_draggable_scroll.*

class DraggableScrollViewAct : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_draggable_scroll)
        
        cardLayout.setOnClickListener{
            Log.i("DraggableScrollViewAct_Old", "cardLayout is clicked!!!!!")
            userContactLayout.visibility = if (userContactLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }
}