package com.testdemo

import android.app.Activity
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

/**
 * Create by Greyson on 2023/10/10
 */
fun <T : ViewBinding> Activity.myInflate(inflater: (LayoutInflater) -> T) = lazy {
    inflater(layoutInflater).apply { setContentView(root) }
}
