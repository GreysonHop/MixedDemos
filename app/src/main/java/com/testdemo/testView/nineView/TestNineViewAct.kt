package com.testdemo.testView.nineView

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import com.testdemo.BaseActivity
import com.testdemo.R
import com.testdemo.webrtc.webrtclib.utils.Utils

/**
 * Created by Greyson on 2020/07/01
 */
class TestNineViewAct : BaseActivity() {

    lateinit var nineView: NineView

    override fun getLayoutView(): View? {
        nineView = NineView(this)
        nineView.gap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, resources.displayMetrics).toInt()
        nineView.setBackgroundColor(Color.GREEN)
        nineView.layoutParams = ViewGroup.MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            .apply {
                topMargin = Utils.dip2px(this@TestNineViewAct, 43f)
                leftMargin = Utils.dip2px(this@TestNineViewAct, 20f)
                rightMargin = Utils.dip2px(this@TestNineViewAct, 20f)
            }
        return nineView
    }

    override fun initView() {
        val imageView = ImageView(this)
        imageView.setBackgroundColor(Color.BLUE)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setImageResource(R.drawable.img4)
        imageView.layoutParams = LayoutParams(150, 200)

        val imageView2 = ImageView(this)
        imageView2.setBackgroundColor(Color.BLACK)
        imageView2.setImageResource(R.drawable.img5)
        imageView2.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val imageView3 = ImageView(this)
        imageView3.setBackgroundColor(Color.RED)
        imageView3.setImageResource(R.drawable.img6)
        imageView3.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

        val imageView4 = ImageView(this)
        imageView4.setBackgroundColor(Color.BLUE)
        imageView4.scaleType = ImageView.ScaleType.CENTER
        imageView4.setImageResource(R.drawable.ic_sport_man)
        imageView4.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        val imageView5 = ImageView(this)
        imageView5.setBackgroundColor(Color.CYAN)
        imageView5.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView5.setImageResource(R.drawable.galata)
        imageView5.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        nineView.addView(imageView)
        nineView.addView(imageView2)
        nineView.addView(imageView3)
        nineView.addView(imageView4)
        nineView.addView(imageView5)

        for (i in 0..6) {
            val imgView = ImageView(this)
            imgView.setBackgroundColor(Color.GRAY)
            imgView.scaleType = ImageView.ScaleType.CENTER
            imgView.setImageResource(R.drawable.call_icon_gift)
            // imgView.layoutParams = LayoutParams(150, 100)
            nineView.addView(imgView)
        }
    }
}