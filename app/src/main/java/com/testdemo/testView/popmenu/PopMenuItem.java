package com.testdemo.testView.popmenu;

import android.graphics.drawable.Drawable;

/**
 * Created by Joe on 16/12/10
 */
public class PopMenuItem {

    private String title;
    private Drawable drawable;
    private int textColor;//颜色值，非ResId

    public PopMenuItem(String title, Drawable drawable, int textColor) {
        this.title = title;
        this.drawable = drawable;
        this.textColor = textColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
