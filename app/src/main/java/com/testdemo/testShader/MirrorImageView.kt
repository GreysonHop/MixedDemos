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
class MirrorImageView : View {
    private var mSrcBitmap: Bitmap? = null
    private var mRefBitmap: Bitmap? = null
    private var mPaint: Paint? = null
    private var mXfermode: PorterDuffXfermode? = null

    private var startX = 0f
    private var startY = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initRes(context)
    }

    fun initRes(context: Context) {
        // 获取源图
        mSrcBitmap = BitmapFactory.decodeResource(resources, R.drawable.img4)

        // 实例化一个矩阵对象
        val matrix = Matrix()
        matrix.setTranslate(0f, 20f)
        matrix.setScale(1f, -1f)

        mSrcBitmap?.let {
            // 生成倒影图
            mRefBitmap = Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)

            val screenW = ScreenUtils.getScreenWidth(context)
            val screenH = ScreenUtils.getScreenHeight(context)
            Log.d("greyson", "width = $screenW  height = $screenH")

            startX = (screenW / 2 - it.width / 2).toFloat()
            startY = (screenH / 2 - it.height / 2).toFloat()

            mPaint = Paint()
            mPaint?.shader = LinearGradient(
                    startX
                    , (startY + it.height)
                    , startX
                    , (startY + it.height + it.height / 3)
                    , 0xAA000000.toInt()
                    , Color.TRANSPARENT
                    , Shader.TileMode.CLAMP)

            mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        canvas.drawBitmap(mSrcBitmap, startX, startY, null)

        mSrcBitmap?.let {
            val sc = canvas.saveLayer(startX, startY + it.height, startX + mRefBitmap!!.width, startY + it.height * 2, null, Canvas.ALL_SAVE_FLAG)

            canvas.drawBitmap(mRefBitmap, startX, startY + it.height, null)

            mPaint!!.xfermode = mXfermode

            canvas.drawRect(startX, startY + it.height, startX + mRefBitmap!!.width, startY + it.height * 2, mPaint)

            mPaint!!.xfermode = null

            canvas.restoreToCount(sc)
        }
    }

}