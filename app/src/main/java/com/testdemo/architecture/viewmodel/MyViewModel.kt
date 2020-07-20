package com.testdemo.architecture.viewmodel

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

    /*val userPwd: LiveData<String> //TODO greyson_7/20/20 数据绑定一栏中《将布局视图绑定到架构组件》

    init {
        val result = Repository.userPwd
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
}