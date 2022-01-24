package com.testdemo.testView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Created by Greyson on 2022/01/24
 * 自定义Drawable，自定义ColorDrawable
 */
class NameAvatarDrawable extends ColorDrawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String name;
    private Context context;
    private float textSize;

    public NameAvatarDrawable(float textSize, String name) {
        this.name = name;
        this.textSize = textSize;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawColor(Color.parseColor("#3E82FB"));
        super.draw(canvas);
        Rect drawableRect = getBounds();
        Rect textRect = new Rect();
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(name, 0, 1, textRect);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();

        int textX = drawableRect.centerX() - textRect.width() / 2;
        int textY = (int) (drawableRect.centerY() - (fontMetrics.top + fontMetrics.bottom) / 2f);
        Log.i("greyson", "draw: drawableRect=" + drawableRect + ", textRect=" + textRect + ", fontMetrics="
                + fontMetrics + ", textX=" + textX + ", textY=" + textY);
        canvas.drawText(name, 0, 1, textX, textY, mPaint);
    }

}
