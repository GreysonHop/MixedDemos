package com.testdemo.testView.canvas

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import com.testdemo.R

/**
 * Create by Greyson
 */
class AvatarLayout : ConstraintLayout {

    private var clipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val scrBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_avatar_pattern, BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ALPHA_8
    })
    private val curRect = RectF(0f, 0f, 0f, 0f)
    private val curXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

    init {
        clipPaint.style = Paint.Style.FILL
    }

    constructor(context: Context) : this(context, null) //如何在构造器中调用其它构造器？（构造器委托的方式）

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    override fun dispatchDraw(canvas: Canvas) {
        val curWidth = measuredWidth.toFloat()
        val curHeight = measuredHeight.toFloat()
        curRect.right = curWidth
        curRect.bottom = curHeight

        val layerId = canvas.saveLayer(0f, 0f, curWidth, curHeight, null)

        super.dispatchDraw(canvas)

        clipPaint.xfermode = curXfermode
        canvas.drawBitmap(scrBitmap, null, curRect, clipPaint)
        clipPaint.xfermode = null
        canvas.restoreToCount(layerId)
    }

    /*override fun onDraw(canvas: Canvas) {
        val curWidth = measuredWidth.toFloat()
        val curHeight = measuredHeight.toFloat()
        curRect.right = curWidth
        curRect.bottom = curHeight

        val layerId = canvas.saveLayer(0f, 0f, curWidth, curHeight, null)

        super.onDraw(canvas) //TODO greyson_3/8/21 为什么写在onDraw()方法中不能用 xfermode

        clipPaint.xfermode = curXfermode
        canvas.drawBitmap(scrBitmap, null, curRect, clipPaint)
        clipPaint.xfermode = null
        canvas.restoreToCount(layerId)
    }*/

    //这种方式也可以实现裁剪效果，但是需要5.0以上
    private fun clipRoundView() { //网上的例子，还没测试过
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val viewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline) {
                    //修改outline为特定形状
                    outline.setRoundRect(0, 0, measuredWidth, measuredHeight, 30f)
                }
            }
            //重新设置形状
            outlineProvider = viewOutlineProvider
            //添加背景或者是ImageView的时候失效，添加如下设置
            clipToOutline = true
        }
    }
}