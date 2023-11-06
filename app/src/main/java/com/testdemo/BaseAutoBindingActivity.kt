package com.testdemo

import android.util.Log
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * Create by Greyson
 */
abstract class BaseAutoBindingActivity<T : ViewBinding> : BaseActivity() {
    protected lateinit var binding: T

    final override fun setContentView() {
        val b = getBindingObj()
        if (b == null) {
            this.finish()
            return // 如果有一步失败，那就关闭页面。
        } else {
            binding = b
        }
        Log.d(TAG, "reflect binding=$binding")

//        val tClass = Class.forName(genericTypeName.toString()).kotlin
//        Log.d(TAG, "t class: ")


        setContentView(binding.root)
    }

    @Suppress("unchecked_cast")
    private fun getBindingObj(): T? {
        try {
            val genericClass = this.javaClass.genericSuperclass as ParameterizedType
            val genericType = genericClass.actualTypeArguments[0]
            val genericTypeName = genericType.toString().replace("class ", "")
            Log.d(
                TAG,
                "this: ${genericClass.rawType} - $genericTypeName - ${genericType::class.java.name}"
            )

            val method =
                Class.forName(genericTypeName).getMethod("inflate", LayoutInflater::class.java)
            return method.invoke(null, layoutInflater) as T  // 如果类型错误，让界面无法正常使用

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}