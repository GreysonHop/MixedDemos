package com.testdemo

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import com.testdemo.databinding.ActTestOutlineBinding


/**
 * Create by Greyson on 2022/02/21
 */
class OutlineAct : BaseBindingActivity<ActTestOutlineBinding>() {


    override fun getViewBinding(): ActTestOutlineBinding {
        return ActTestOutlineBinding.inflate(layoutInflater)
    }

    override fun initView() {
//        binding.ivPic.outlineProvider = object: Outline
        binding.ivPic2.setBackground(buildBackground())
    }

    private fun buildBackground(): Drawable {
        return ShapeDrawable(object : RectShape() {
            override fun draw(canvas: Canvas, paint: Paint) {
                paint.setColor(Color.WHITE)
                paint.setStyle(Paint.Style.FILL)
                canvas.drawPath(buildConvexPath(), paint)
            }

            override fun getOutline(outline: Outline) {
                outline.setConvexPath(buildConvexPath())
            }

            private fun buildConvexPath(): Path {
                val path = Path()
                path.lineTo(rect().left, rect().top)
                path.lineTo(rect().right - rect().width() / 2, rect().top)
                path.lineTo(rect().right, rect().bottom - rect().height() / 3)
                path.lineTo(rect().right, rect().bottom)
                path.lineTo(rect().left, rect().bottom)
                path.close()
                return path
            }
        })
    }
}