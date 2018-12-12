package com.testdemo.testShader

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.luck.picture.lib.tools.ScreenUtils
import com.testdemo.R

/**
 * Created by Greyson on 2018/12/10.
 */
class TestView : View {
    private var bmp1: Bitmap? = null
    private var bmp2: Bitmap? = null
    private var bmp3: Bitmap? = null
    private var paint: Paint? = null
    private var mXfermode: PorterDuffXfermode? = null

    private var startX = 0f
    private var startY = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initRes(context)
    }

    fun initRes(context: Context) {
        // 获取源图
        bmp1 = BitmapFactory.decodeResource(resources, R.drawable.img3)
        bmp2 = BitmapFactory.decodeResource(resources, R.drawable.img2)
        bmp3 = BitmapFactory.decodeResource(resources, R.drawable.img1)
    }

    override fun onDraw(canvas: Canvas) {
//        val canvas = sfh.lockCanvas()
//        canvas.drawColor(Color.BLACK)
        canvas.drawBitmap(bmp1, 0f, 0f, paint)

        canvas.save()

        canvas.scale(2f, 2f)
        canvas.drawBitmap(bmp2, 0f, 0f, paint)

        canvas.restore()

        canvas.drawBitmap(bmp3, 0f, 0f, null)

//        sfh.unlockCanvasAndPost(canvas)
    }

}