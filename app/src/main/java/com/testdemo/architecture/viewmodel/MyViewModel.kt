package com.testdemo.architecture.viewmodel

import androidx.lifecycle.ViewModel
import com.testdemo.architecture.UserBean

/**
 * Create by Greyson on 2020/05/05
 */
class MyViewModel : ViewModel() {
    private lateinit var user: UserBean

    fun getCurrentUser(): UserBean {
        return user
    }

}