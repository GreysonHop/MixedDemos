package com.testdemo.testShader

import android.app.Activity
import android.os.Bundle
import com.testdemo.R

/**
 * Created by Greyson on 2018/12/10.
 */
class TestShaderAct: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_shader)

        println("greyson this phone's factor: " +
                "\ndensity = ${resources.displayMetrics.density}" +
                "\nwidthPixel = ${resources.displayMetrics.widthPixels}" +
                "\nheightPixel = ${resources.displayMetrics.heightPixels}" +
                "\ndensityDpi = ${resources.displayMetrics.densityDpi} " +
                "\nscaledDensity = ${resources.displayMetrics.scaledDensity}" +
                "\nxdpi = ${resources.displayMetrics.xdpi}" +
                "\nydpi = ${resources.displayMetrics.ydpi}")
    }

}