package com.testdemo.testView.doodle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Create by Greyson on 2021/01/06
 */
class IMView : FrameLayout { //如果在布局文件中不设置背景色，那么当前类的 onDraw 方法里就会没效果。原因不明

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private val path = Path()
    private var paint = Paint()

    fun initialize() {
        paint.apply {
            color = Color.GREEN
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Kotlin 同时给两个变量赋值
        val (xPos: Int, yPos: Int) = event.actionMasked.let { action ->
            Log.d("IMView", "The action is ${actionToString(action)}")
            // Get the index of the pointer associated with the action.
            event.actionIndex.let { index ->
                // The coordinates of the current screen contact, relative to
                // the responding View or Activity.
                event.getX(index).toInt() to event.getY(index).toInt()
            }
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                invalidate()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                path.reset()
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        canvas.drawPath(path, paint)
    }

    //判断点击点是否在Path路径上。这是网上代码还待考证
    private fun pointIsInPath(x: Float, y: Float, path: Path): Boolean {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val region = Region()
        region.setPath(
            path,
            Region(
                Rect(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.right.toInt(),
                    bounds.bottom.toInt()
                )
            )
        )

        return region.contains(x.toInt(), y.toInt())
    }

    fun actionToString(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "Down"
            MotionEvent.ACTION_MOVE -> "Move"
            MotionEvent.ACTION_POINTER_DOWN -> "Pointer Down"
            MotionEvent.ACTION_UP -> "Up"
            MotionEvent.ACTION_POINTER_UP -> "Pointer Up"
            MotionEvent.ACTION_OUTSIDE -> "Outside"
            MotionEvent.ACTION_CANCEL -> "Cancel"
            else -> ""
        }
    }
}