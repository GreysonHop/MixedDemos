package com.testdemo.architecture

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.testdemo.BaseActivity
import com.testdemo.R
import com.testdemo.architecture.viewmodel.ViewModelAct
import com.testdemo.databinding.ActTestDatabindingBinding
import com.testdemo.util.AVChatNotification

/**
 * Create by Greyson on 2020/03/29
 */
class TestDataBindingAct : BaseActivity(), View.OnClickListener {
    var mBinding: ActTestDatabindingBinding? = null
    var time = 0

    val liveData1 = MutableLiveData<String>() //TODO greyson_8/7/20 LiveData不能直接绑定到@={}吗？？
    val liveData2 = MutableLiveData<String>()
    val mediator = MediatorLiveData<String>()

    /*var data2: String? = null
        set(value) {
            liveData2.value = value
            field = value
        }*/
    // get() = field
    private var notifier = AVChatNotification(this)

    override fun initView() {
        Log.d("greyson", "TestDataBindingAct-initView: task = $taskId")
        mBinding = DataBindingUtil.setContentView<ActTestDatabindingBinding>(this, R.layout.act_test_databinding)
            .apply {
                lifecycleOwner = this@TestDataBindingAct // Specify the current activity as the lifecycle owner.
                listener = this@TestDataBindingAct
                isVisible = true
                title = "还没点击"
                observableInput = ObservableField() //TODO greyson_7/20/20 ObservableField类用什么用？
                userName = ObservableField("??")

                data1 = ObservableField<String>().apply {
                    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            println("greyson:data1=$data1")
                            liveData1.value = data1?.get()
                        }
                    })
                }
                data2 = ObservableField<String>().apply {
                    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            println("greyson:data2=$data2")
                            liveData2.value = data2?.get()
                        }
                    })
                }
                mediatorData = mediator
            }

        mediator.addSource(liveData1) { value -> mediator.setValue(value) }
        mediator.addSource(liveData2) { value -> mediator.setValue(value) }

        liveData1.observe(this, Observer {
            println("greyson:$it")
        })

        liveData2.observe(this, Observer {
            it.trim()
        })

        mediator.observe(this, Observer {
            println("greyson")
        })

        notifier.init("haha")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_test_add -> {
                mBinding?.title = "第" + ++time + "次点击"
            }
            R.id.btn_test_hide -> {
                mBinding?.apply { isVisible = !isVisible }
            }

            R.id.btn_view_model -> {
                startActivity(Intent(this, ViewModelAct::class.java))
            }

            R.id.btn_notify -> {
                mBinding?.root?.postDelayed({ notifier.activeCallingNotification(true) }, 2000)
            }
            R.id.btn_notify_cancel -> notifier.activeCallingNotification(false)

            else -> {
                startActivity(Intent(this, SaveStateAct::class.java))
                //overridePendingTransition(R.anim.bottom_menu_in, R.anim.activity_hold)
            }
        }
    }

}