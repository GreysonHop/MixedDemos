package com.testdemo.architecture.viewmodel

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.testdemo.R
import com.testdemo.databinding.ActViewModelBinding

/**
 * Create by Greyson on 2020/05/05
 */
class ViewModelAct : AppCompatActivity() {

    private lateinit var model: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = viewModelCreation(0)

        val binding = DataBindingUtil.setContentView<ActViewModelBinding>(this, R.layout.act_view_model)
            .apply {
                /**
                 * 要将 LiveData 对象与绑定类一起使用，您需要指定生命周期所有者来定义 LiveData 对象的范围。
                 * 以下示例在绑定类实例化后将 Activity 指定为生命周期所有者：
                 */
                lifecycleOwner = this@ViewModelAct
                viewmodel = model
            }

        /*model.userPwd.observe(this, Observer { name ->
            Toast.makeText(this, "receive name is $name", Toast.LENGTH_SHORT).show()
        })*/

        val user = model.getUser()
    }


    private fun viewModelCreation(method: Int): MyViewModel {
        return when (method) {
            0 -> ViewModelProviders.of(this).get(MyViewModel::class.java) //这是谷歌官方视频中演示的实现方式

            1 -> ViewModelProvider(this).get(MyViewModel::class.java)

            else -> ViewModelProvider(
                viewModelStore,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            ).get(MyViewModel::class.java)
        }

    }
}