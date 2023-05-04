package com.testdemo.architecture.viewmodel

import android.content.ClipData
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.testdemo.architecture.UserBean

/**
 * Create by Greyson on 2020/05/05
 */
class MyViewModel : ViewModel() {

    var rememberMe = false

    /*val userPwd: LiveData<String>

    init {
        val result = Repository.userPwd //这是模拟从远程或数据库中获取数据的LiveData
        userPwd = Transformations.map(result) { result -> result.value }
    }*/


    private val user: MutableLiveData<UserBean> by lazy {
        MutableLiveData<UserBean>().also {
            loadUsers()
        }
    }

    fun getUser(): LiveData<UserBean> {
        return user
    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }

    fun rememberMeChanged() {
        Log.d("greyson", "rememberMeChanged: $rememberMe")
    }


    // 官方推荐实例代码(用 LiveData 更新数据的方式)
    /*private val itemId = MutableLiveData<String>()
    val itemData: LiveData<ClipData.Item> = Transformations.switchMap(itemId) {
        dataSource.getItem(it)
    }

    fun loadData(id: String) {
        itemId.value = id
    }*/
}