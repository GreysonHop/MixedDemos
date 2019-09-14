package com.testdemo.testPictureSelect;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Greyson on 2018/6/5.
 */

public class WrapWidthImageView extends AppCompatImageView {
    public WrapWidthImageView(Context context) {
        this(context, null);
    }

    public WrapWidthImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapWidthImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        Drawable imgDrawable = getDrawable();
        if (imgDrawable != null) {
            //获得ImageView中Image的真实宽高
            int dw = imgDrawable.getBounds().width();
            int dh = imgDrawable.getBounds().height();

            //获得ImageView中Image的变换矩阵
            Matrix m = getImageMatrix();
            float[] values = new float[10];
            m.getValues(values);

            //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数
            float sx = values[0];
            float sy = values[4];

            //计算Image在屏幕上实际绘制的宽高
            int width = (int) (dw * sx);
            int height = (int) (dh * sy);
            Log.w("greyson", "WrapWidth:  " + imgDrawable + " - " + dw + " - " + dh
                    + "\n" + width + " - " + height + " - " + sx + " - " + sy);
        }
    }
}
