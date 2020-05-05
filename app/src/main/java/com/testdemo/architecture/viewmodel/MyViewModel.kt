package com.testdemo.architecture.viewmodel

import androidx.lifecycle.ViewModel

/**
 * Create by Greyson on 2020/05/05
 */
class MyViewModel : ViewModel() {
    private lateinit var currentUser: String

    fun getCurrentUser(): String {
        return currentUser
    }

}