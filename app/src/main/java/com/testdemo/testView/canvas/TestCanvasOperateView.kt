package com.testdemo.testView.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.testdemo.R

/**
 * Created by Greyson on 2018/12/10.
 * 测试绘制组件时的画布缩放
 */
class TestCanvasOperateView : View {
    private var bmp1: Bitmap? = null
    private lateinit var bmp2: Bitmap
    private lateinit var bmp3: Bitmap
    private var paint: Paint? = null
    private var mXfermode: PorterDuffXfermode? = null

    private var startX = 0f
    private var startY = 0f

    constructor(context: Context) : this(context, null) {
        println("TestView no-params constructor")
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        println("TestView two-params constructor")
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        println("TestView three-params constructor")
        initRes()
    }

    private fun initRes() {
        // 获取源图
        bmp1 = BitmapFactory.decodeResource(resources, R.drawable.img3)
        bmp2 = BitmapFactory.decodeResource(resources, R.drawable.img2)
        bmp3 = BitmapFactory.decodeResource(resources, R.drawable.img1)
    }

    override fun onDraw(canvas: Canvas) {
        bmp1?.let {
            canvas.drawBitmap(it, 0f, 0f, paint)//这里必须用it，如果直接用bmp1还是会有警告，要你用bmp1!!
        }

        canvas.save()

        canvas.scale(2f, 2f)
        canvas.drawBitmap(bmp2, 0f, 0f, paint)

        canvas.restore()

        canvas.drawBitmap(bmp3, 0f, 0f, null)
    }

}