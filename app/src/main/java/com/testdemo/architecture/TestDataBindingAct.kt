package com.testdemo.architecture

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.testdemo.BaseCommonActivity
import com.testdemo.R
import com.testdemo.architecture.viewmodel.ViewModelAct
import com.testdemo.databinding.ActTestDatabindingBinding
import com.testdemo.testView.nineView.TestNineViewAct
import com.testdemo.util.AVChatNotification
import com.testdemo.util.Utils


/**
 * Create by Greyson on 2020/03/29
 */
class TestDataBindingAct : BaseCommonActivity(), View.OnClickListener {
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
        Log.d(TAG, "TestDataBindingAct-initView: task = $taskId")
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


    private val handler = Handler(Looper.getMainLooper())
    private var runCount = 0

    private val callTimeout = object : Runnable {
        override fun run() {
            mBinding?.tvRunText?.text = "running: $runCount"
            runCount++
            Log.e(TAG, "callTimeout运行，current thread=${Thread.currentThread()}, 应用是否在前台：${Utils.isAppForeground()}, this=$this。")

            if (Utils.isAppForeground()) {
                startActivity(Intent(this@TestDataBindingAct, TestNineViewAct::class.java))
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(this@TestDataBindingAct, AVChatService::class.java))
                } else {
                    // 在安卓11的虚拟机上，可以在后台直接启动普通的后台服务啊，网上怎么说8.0之后不能？？？
                    startService(Intent(this@TestDataBindingAct, AVChatService::class.java))
                }
            }
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

            R.id.btn_view_model -> startActivity(Intent(this, ViewModelAct::class.java))

            R.id.btn_notify -> mBinding?.root?.postDelayed({
                if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    notifier.activeCallingNotification(true)
                } else {
                    Toast.makeText(this, "没有通知权限！！", Toast.LENGTH_SHORT).show()
                }
            }, 2000)

            R.id.btn_notify_cancel -> notifier.activeCallingNotification(false)

            R.id.btn_startRun -> {
                Log.e(TAG, "click startRun, post delayed callTimeout. current thread=${Thread.currentThread()}")
                handler.postDelayed(callTimeout, 3000)
            }

            R.id.btn_stopRun -> {
                Log.e(TAG, "click stopRun, remove callTimeout. current thread=${Thread.currentThread()}")
                handler.removeCallbacks(callTimeout)
                val intent = Intent(this@TestDataBindingAct, AVChatService::class.java)
                intent.putExtra("order", -1)
                Log.e(TAG, "stopRun stop AVChatService by startService().")
                startService(intent)
            }

            R.id.callLog -> { //插入本地通话记录
                val values = ContentValues()
                values.put(CallLog.Calls.NUMBER, "10322031")
                values.put(CallLog.Calls.DATE, System.currentTimeMillis())
                values.put(CallLog.Calls.DURATION, 75)
                values.put(CallLog.Calls.TYPE, 2)
                values.put(CallLog.Calls.NEW, 1)
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) !== PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_CALL_LOG), 1000)
                }
                contentResolver.insert(CallLog.Calls.CONTENT_URI, values)
            }

            else -> {
                startActivity(Intent(this, SaveStateAct::class.java))
                //overridePendingTransition(R.anim.bottom_menu_in, R.anim.activity_hold)
            }
        }
    }

}