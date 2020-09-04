package com.testdemo.webrtc.webrtclib.bean

/**
 * Created by Greyson on 2020/07/30
 */
enum class CallState(val value: Int) {
    UNKNOWN(-1),
    CONNECTING(0), //等待呼出的操作传达到任一方，参考拨号中的状态
    CALLING(1), //呼出：等待任一方接听，参考等待接听的状态；呼入：显示被邀请通话的界面
    INCALL(2), //正在通话中
    CANCEL(3),
    HANGUP(4),
    TIMEOUT(5);


    /*CALLING_OUTGOING(0),//呼叫方（群聊语音通话发起者）正在建立语音通话连接且连接请求未达到任一被呼叫方时的状态。
    WAITING(1),//当语音通话的请求成功下发至任一被呼叫方的手机用户端时，呼叫方的“拨号中”状态切换至“等待接听”状态
    CALL_CANCELED(2),//取消呼叫，呼叫未到达任何一方之前挂断吗？？
    CALL_FAILED(3),//呼叫失败，出现网络错误等情况
    CALL_NONE(4),//从进入“等待接听”状态时起，等待接听时长60s，如果60s内全部被呼叫方未接听语音通话，或所有人拒绝

    CALLING_INCOMING(5),//来电呼叫中
    HANGUP(6),//被呼叫者挂断电话，或呼叫者有人接通之后挂断？？

    CONNECTING(7),//至少有一个人接通了的通话中状态；或者接听者接听中状态*/

    companion object {
        @JvmStatic
        fun create4Value(value: Int): CallState {
            return when (value) {
                0 -> CONNECTING
                1 -> CALLING
                2 -> INCALL
                3 -> CANCEL
                4 -> HANGUP
                5 -> TIMEOUT
                else -> UNKNOWN
            }
        }
    }
}