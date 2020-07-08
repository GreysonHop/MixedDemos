package com.testdemo.webrtc.webrtclib.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.testdemo.BaseActivity
import com.testdemo.R
import com.testdemo.webrtc.webrtclib.IViewCallback
import com.testdemo.webrtc.webrtclib.ProxyVideoSink
import com.testdemo.webrtc.webrtclib.WebRTCManager
import com.testdemo.webrtc.webrtclib.bean.MemberBean
import com.testdemo.webrtc.webrtclib.utils.PermissionUtil
import kotlinx.android.synthetic.main.act_group_avchat.iv_groupAvChat_minimize as ivMinimize
import kotlinx.android.synthetic.main.act_group_avchat.iv_groupAvChat_addMember as ivAddMember
import org.webrtc.*
import kotlin.concurrent.thread
import kotlinx.android.synthetic.main.act_group_avchat.nv_groupAvChat_userStateList as userStateList
import kotlinx.android.synthetic.main.act_group_avchat.tv_groupAvChat_mute as tvMute
import kotlinx.android.synthetic.main.act_group_avchat.tv_groupAvChat_speaker as tvSpeaker
import kotlinx.android.synthetic.main.act_group_avchat.tv_groupAvChat_hangup as tvHangup
import kotlinx.android.synthetic.main.act_group_avchat.tv_avchat_state as tvState

/**
 * Created by Greyson on 2020/07/02
 * WebRTC的多人聊天室
 */
class GroupAVChatActivity : BaseActivity(), IViewCallback {

    private var webRTCManager: WebRTCManager? = null
    private val videoViews = mutableMapOf<String, SurfaceViewRenderer>()
    private val videoSinks = mutableMapOf<String, ProxyVideoSink>()
    private val memberList = mutableListOf<MemberBean>()
    private var localVideoTrack: VideoTrack? = null
    private lateinit var rootEglBase: EglBase

    companion object {
        fun openActivity(activity: Activity) {
            activity.startActivity(Intent(activity, GroupAVChatActivity::class.java))
        }
    }

    override fun initialize() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    override fun getLayoutResId(): Int {
        return R.layout.act_group_avchat
    }

    override fun initView() {
        tvState.text = "拨号中..."
        /*tvMute.setTextImage(R.drawable.ic_avchat_mute_invalid) //todo 可以不用了？！！
        tvMute.isSelected = false*/
        tvMute.setOnClickListener {
            val state = !it.isSelected
            toggleMute(state)
            it.isSelected = state
        }

        tvSpeaker.setOnClickListener {
            it.isSelected = !it.isSelected
            toggleSpeaker(it.isSelected)
        }

        ivMinimize.setOnClickListener { }
        ivAddMember.setOnClickListener { }

        tvHangup.setOnClickListener { hangup() }
    }

    override fun initData() {
        rootEglBase = EglBase.create()

        webRTCManager = WebRTCManager.getInstance().apply {
            setCallback(this@GroupAVChatActivity)
            if (!PermissionUtil.isNeedRequestPermission(this@GroupAVChatActivity)) {
                joinRoom(applicationContext, rootEglBase)
            }
        }

        thread { //模拟数据加载后，按钮变成可点击
            Thread.sleep(2000)
            runOnUiThread {
                enableReaction(true)
                tvState.text = "等待接受邀请"
            }
        }
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        grantResults.forEach {
            // Log.i(PeerConnectionHelper.TAG, "[Permission] " + permissions[i] + " is " + if (grantResults[i] == PackageManager.PERMISSION_GRANTED) "granted" else "denied")
            if (it != PackageManager.PERMISSION_GRANTED) {
                finish()
                return@forEach
            }
        }
        webRTCManager?.joinRoom(applicationContext, rootEglBase)
    }

    override fun onSetLocalStream(stream: MediaStream?, socketId: String) {
        stream?.run {
            videoTracks?.apply {
                if (size > 0) {
                    localVideoTrack = get(0)
                }
            }
            runOnUiThread {
                toggleSpeaker(false)
                addUserView(socketId, this)
            }
        }
    }

    override fun onAddRemoteStream(stream: MediaStream, socketId: String) {
        runOnUiThread {
            addUserView(socketId, stream)
            tvState.text = ""
        }
    }

    override fun onCloseWithId(socketId: String) {
        runOnUiThread {
            removeUserView(socketId)
            if (videoViews.isEmpty()) {
                tvState.text = "等待接受邀请"
            }
        }
    }

    fun switchCamera() {
        webRTCManager?.switchCamera();
    }

    fun toggleMute(enable: Boolean) {
        webRTCManager?.toggleMute(enable)
    }

    fun toggleSpeaker(enable: Boolean) {
        webRTCManager?.toggleSpeaker(enable)
    }

    fun toggleCamera(enable: Boolean) {
        localVideoTrack?.setEnabled(enable)
    }

    private fun enableReaction(enable: Boolean) {
        tvSpeaker.isEnabled = enable
        tvMute.isEnabled = enable
        ivMinimize.isEnabled = enable
        ivAddMember.isEnabled = enable
    }

    fun hangup() {
        tvState.text = "已挂断"
        enableReaction(false)
        release()
        thread { //模拟挂断后显示状态，再关闭界面
            Thread.sleep(2000)
            runOnUiThread { finish() }
        }
    }

    private fun addUserView(id: String, stream: MediaStream) {
        val renderer = SurfaceViewRenderer(this)
        renderer.init(rootEglBase.eglBaseContext, null)
        renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        renderer.setMirror(true)
        // set render
        val sink = ProxyVideoSink()
        sink.setTarget(renderer)
        if (stream.videoTracks.size > 0) {
            stream.videoTracks[0].addSink(sink)
        }
        videoViews[id] = renderer
        videoSinks[id] = sink
        memberList.add(MemberBean(id))

        userStateList.addView(renderer, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private fun removeUserView(id: String) {
        val sink = videoSinks[id]
        val renderer = videoViews[id]
        sink?.setTarget(null)
        renderer?.release()

        videoSinks.remove(id)
        videoViews.remove(id)
        memberList.remove(MemberBean(id))

        userStateList.removeView(renderer)
    }

    private fun release() {
        webRTCManager?.exitRoom()
        videoViews.values.forEach { it.release() }
        videoSinks.values.forEach { it.setTarget(null) }
        videoViews.clear()
        videoSinks.clear()
        memberList.clear()
    }

    private fun <T : TextView> T.setTextImage(@DrawableRes resId: Int) {
        ContextCompat.getDrawable(context, resId)?.let { setCompoundDrawables(null, it, null, null) }
    }
}