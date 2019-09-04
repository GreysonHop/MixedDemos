package com.testdemo.testShader

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.testdemo.R

/**
 * Created by Greyson on 2018/12/10.
 */
class TestView : View {
    private var bmp1: Bitmap? = null
    private lateinit var bmp2: Bitmap
    private lateinit var bmp3: Bitmap
    private var paint: Paint? = null
    private var mXfermode: PorterDuffXfermode? = null

    private var startX = 0f
    private var startY = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initRes()
    }

    private fun initRes() {
        // 获取源图
        bmp1 = BitmapFactory.decodeResource(resources, R.drawable.img3)
        bmp2 = BitmapFactory.decodeResource(resources, R.drawable.img2)
        bmp3 = BitmapFactory.decodeResource(resources, R.drawable.img1)
    }

    override fun onDraw(canvas: Canvas) {
//        val canvas = sfh.lockCanvas()
//        canvas.drawColor(Color.BLACK)
        bmp1?.let {
            canvas.drawBitmap(it, 0f, 0f, paint)//这里必须用it，如果直接用bmp1还是会有警告，要你用bmp1!!
        }

        canvas.save()

        canvas.scale(2f, 2f)
        canvas.drawBitmap(bmp2, 0f, 0f, paint)

        canvas.restore()

        canvas.drawBitmap(bmp3, 0f, 0f, null)

//        sfh.unlockCanvasAndPost(canvas)
    }

}