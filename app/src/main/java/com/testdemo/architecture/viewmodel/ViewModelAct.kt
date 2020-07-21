package com.testdemo.architecture.viewmodel

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.testdemo.R
import com.testdemo.databinding.ActViewModelBinding

/**
 * Create by Greyson on 2020/05/05
 */
class ViewModelAct : AppCompatActivity() {

    private val model: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO greyson_2020/7/9 听说推荐用此方式，但还不知道怎么实现？？
        //ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory.getInstance(application))

        //model = ViewModelProviders.of(this).get(MyViewModel::class.java)//这是谷歌官方视频中演示的实现方式
        // model = ViewModelProvider(this).get(MyViewModel::class.java)

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
}