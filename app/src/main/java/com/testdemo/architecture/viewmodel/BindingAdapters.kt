package com.testdemo.architecture.viewmodel

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseMethod
import com.testdemo.R

/**
 * Created by Greyson on 2020/07/23
 */
@Suppress("unused")
object BindingAdapters {

    @BindingAdapter("inverseText")
    @JvmStatic
    fun setInverseText(v: TextView, name: String) {
        v.text = name
    }

    @InverseBindingAdapter(attribute = "inverseText")
    @JvmStatic
    fun getInverseText(v: TextView): String {
        return v.text.toString()
    }

}

object Convertor {

    @InverseMethod("tip2Name")
    @JvmStatic
    fun name2Tip(context: Context, name: String): String {
        return context.getString(R.string.viewmodel_name_tip) + ":" + name
    }

    @JvmStatic
    fun tip2Name(context: Context, tip: String): String {
        return tip.indexOf(":").let {
            if (it < 0) {
                ""
            } else {
                tip.substring(it)
            }
        }
    }
}

