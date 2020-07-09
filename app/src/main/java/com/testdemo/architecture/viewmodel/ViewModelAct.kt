package com.testdemo.architecture.viewmodel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * Create by Greyson on 2020/05/05
 */
class ViewModelAct: AppCompatActivity() {

    private lateinit var model: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        ViewModelProvider(null) //TODO greyson_2020/7/9 听说推荐用此方式
        model = ViewModelProviders.of(this).get(MyViewModel::class.java)

        val user = model.getCurrentUser()
    }
}