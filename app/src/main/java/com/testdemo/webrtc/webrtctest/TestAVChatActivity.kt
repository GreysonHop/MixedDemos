package com.testdemo.webrtc.webrtctest

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.testdemo.R
import kotlinx.android.synthetic.main.activity_nodejs.*

/**
 * Created by dds on 2018/11/7.
 * android_shuai@163.com
 */
class TestAVChatActivity : AppCompatActivity() {
    private var videoEnable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nodejs)
        setSupportActionBar(toolbar)
        initView()
        initVar()
    }

    private fun initView() {
        rg_media_selection.setOnCheckedChangeListener { _, id ->
            videoEnable = id == R.id.rb_video
        }
    }

    private fun initVar() {
        //et_signal.setText("wss://47.93.186.97/wss");
        et_signal!!.setText(WebrtcUtil.WSS)
        et_room!!.setText("232343")
    }

    /*-------------------------- nodejs版本服务器测试--------------------------------------------*/
    /*public void JoinRoomSingleVideo(View view) {
        WebrtcUtil.callSingle(this,
                et_signal.getText().toString(),
                et_room.getText().toString().trim(),
                true);
    }*/

    private val ringtoneType = intArrayOf(RingtoneManager.TYPE_ALL, RingtoneManager.TYPE_ALARM, RingtoneManager.TYPE_RINGTONE, RingtoneManager.TYPE_NOTIFICATION)
    private val ringtoneStr = arrayOf("TYPE_ALL", "TYPE_ALARM", "TYPE_RINGTONE", "TYPE_NOTIFICATION")
    private var i = 0
    private var lastRingtone: Ringtone? = null

    fun joinRoom(view: View?) {
        WebrtcUtil.call(this,
            et_signal!!.text.toString(),
            et_room!!.text.toString().trim { it <= ' ' },
            videoEnable)
    }

    fun playRingtone(view: View?) {
        lastRingtone?.takeIf { it.isPlaying }?.stop()

        val uri = RingtoneManager.getActualDefaultRingtoneUri(this, ringtoneType[i])
        lastRingtone = RingtoneManager.getRingtone(this, uri).apply {
            play()
            tv_ringtone.text = ringtoneStr[i]
        }

        if (i == ringtoneType.size - 1) {
            i = 0
        } else {
            i++
        }
    }

    fun stopRingtone(view: View?) {
        lastRingtone?.takeIf { it.isPlaying }?.stop()
    }
}