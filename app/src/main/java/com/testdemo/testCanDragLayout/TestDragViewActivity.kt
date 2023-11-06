package com.testdemo.testCanDragLayout

import android.annotation.SuppressLint
import android.content.ClipData
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.testdemo.BaseBindingActivity
import com.testdemo.R
import com.testdemo.databinding.TestDragViewActivityBinding

/**
 * Created by Greyson on 2019/9/14.
 * 将View从一个容器拖曳到另一个容器中
 */
@SuppressLint("ClickableViewAccessibility")
class TestDragViewActivity : BaseBindingActivity<TestDragViewActivityBinding>() {

    override fun getViewBinding(): TestDragViewActivityBinding {
        return TestDragViewActivityBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.apply {
            myimage1.setOnTouchListener(MyTouchListener())
            myimage2.setOnTouchListener(MyTouchListener())
            myimage3.setOnTouchListener(MyTouchListener())
            myimage4.setOnTouchListener(MyTouchListener())
            myimage5.setOnTouchListener(MyTouchListener())

            topleft.setOnDragListener(MyDragListener())
            topright.setOnDragListener(MyDragListener())
            bottomleft.setOnDragListener(MyDragListener())
            bottomright.setOnDragListener(MyDragListener())
        }
    }

    inner class MyTouchListener : View.OnTouchListener {
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    val data = ClipData.newPlainText("", "")
                    val shadowBuilder = View.DragShadowBuilder(view)
                    view?.startDrag(data, shadowBuilder, view, 0)
                    view?.visibility = View.INVISIBLE
                    return true
                }

                else -> return false
            }
        }
    }

    inner class MyDragListener : View.OnDragListener {
        private var enterShape = ContextCompat.getDrawable(this@TestDragViewActivity, R.drawable.shape_droptarget)
        private val normalShape = ResourcesCompat.getDrawable(resources, R.drawable.shape, theme)

        override fun onDrag(v: View?, event: DragEvent?): Boolean {
            when (event?.action) {
                DragEvent.ACTION_DRAG_STARTED ->
                    // Do nothing
                    Log.i(TAG, "started v = ${v?.id}")

                DragEvent.ACTION_DRAG_ENTERED ->
                    v?.background = enterShape

                DragEvent.ACTION_DRAG_EXITED ->
                    v?.background = normalShape

                DragEvent.ACTION_DROP -> {
                    Log.i(TAG, "drop v = ${v?.id}")
                    // Dropped, reassign View to ViewGroup
                    val view = event.localState as View
                    val owner = view.parent as ViewGroup
                    if (owner !== v) {
                        owner.removeView(view)
                        val container = v as LinearLayout
                        container.addView(view)
                    }
                }


                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.i(TAG, "Ended v = ${v?.id}")
                    v?.background = normalShape
                    (event.localState as View).visibility = View.VISIBLE
                }
            }
            return true
        }
    }
}