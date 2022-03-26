package com.testdemo.testView

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.text.*
import android.widget.TextView.BufferType
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import java.lang.StringBuilder

/**
 *
 */
class FileEllipsesTextView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle), TextWatcher {

    init {
        addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun afterTextChanged(s: Editable) {
        if (s.isEmpty()) {
            return
        }


        val layout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width).build()
        } else {
            StaticLayout(
                text, paint, width, Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false
            )
        }

        val realLines = layout.lineCount
        val realLength = text.length
        val maxLine = maxLines
//        val sb = StringBuilder(text)
        if (realLines > maxLine && realLength > 4) { // to ellipse，至少预留三个字长度
            val maxTextLength = layout.getLineEnd(maxLine - 1)
            val pointIndex = text.toString().lastIndexOf(".")

            // 省略号后面的文本长度
            val suffixLength = if (pointIndex < 0) 3 else realLength - pointIndex + 3
            if (suffixLength + 1 >= maxTextLength) { // +1 是预留一个省略号长度
                return
            }

            // 省略号前面的文本长度
            val prefixLength = maxTextLength - suffixLength - 1
            s.delete(prefixLength, realLength - suffixLength).insert(prefixLength, "...")
//            sb.delete(prefixLength, realLength - suffixLength).insert(prefixLength, "...")
//            super.setText(sb.toString(), type)

            return
        }
    }

    /*var addTextWatcher = false

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (!addTextWatcher) {
            addTextWatcher = true
            addTextChangedListener(this)
        }
        super.setText(text, type)
    }*/

    /*override fun setText(text: CharSequence, type: BufferType) {
        if (text.isNotEmpty() && maxLines != Int.MAX_VALUE && maxLines >= 1 && width != 0) {
            val layout = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(text, 0, text.length, paint, width).build()
            } else {
                StaticLayout(
                    text, paint, width, Layout.Alignment.ALIGN_NORMAL,
                    1.0f, 0.0f, false
                )
            }
            val realLines = layout.lineCount
            val realLength = text.length
            val maxLine = maxLines
            val sb = StringBuilder(text)
            if (realLines > maxLine && realLength > 4) { // to ellipse，至少预留三个字长度
                val maxTextLength = layout.getLineEnd(maxLine - 1)
                val pointIndex = text.toString().lastIndexOf(".")

                // 省略号后面的文本长度
                val suffixLength = if (pointIndex < 0) 3 else realLength - pointIndex + 3
                if (suffixLength + 1 >= maxTextLength) { // +1 是预留一个省略号长度
                    super.setText(text, type)
                    return
                }

                // 省略号前面的文本长度
                val prefixLength = maxTextLength - suffixLength - 1
                sb.delete(prefixLength, realLength - suffixLength).insert(prefixLength, "...")
                super.setText(sb.toString(), type)
                return
            }
        }
        super.setText(text, type)
    }*/

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.i("greyson", "onDraw: ")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.i("greyson", "onMeasure: ")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i("greyson", "onLayout: ")
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        Log.i("greyson", "onSizeChanged: $width-$height, $oldWidth-$oldHeight")
        if (width > 0 && oldWidth != width) {
            text = text
        }
    }
}