package com.testdemo.architecture

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.testdemo.BaseActivity
import com.testdemo.R
import com.testdemo.databinding.ActTestDatabindingBinding

/**
 * Create by Greyson on 2020/03/29
 */
class TestDataBindingAct : BaseActivity(), View.OnClickListener {
    var mBinding: ActTestDatabindingBinding? = null
    var time = 0

    override fun initView() {
        Log.d("greyson", "TestDataBindingAct-initView: task = $taskId")
        mBinding = DataBindingUtil.setContentView<ActTestDatabindingBinding>(this, R.layout.act_test_databinding)
            .apply {
                listener = this@TestDataBindingAct
                isVisible = true
                title = "还没点击"
                observableInput = ObservableField()//TODO greyson_7/20/20 ObservableField类用什么用？
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_test_add -> {
                mBinding?.title = "第" + ++time + "次点击"
            }
            R.id.btn_test_hide -> {
                mBinding?.apply { isVisible = !isVisible }
            }
            else -> {
                startActivity(Intent(this, SaveStateAct::class.java))
                //overridePendingTransition(R.anim.bottom_menu_in, R.anim.activity_hold)
            }
        }
    }
}