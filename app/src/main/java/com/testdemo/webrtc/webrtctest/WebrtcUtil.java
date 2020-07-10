package com.testdemo.webrtc.webrtctest;

import android.app.Activity;
import android.text.TextUtils;

import com.testdemo.webrtc.webrtclib.WebRTCManager;
import com.testdemo.webrtc.webrtclib.bean.MediaType;
import com.testdemo.webrtc.webrtclib.bean.MyIceServer;
import com.testdemo.webrtc.webrtclib.ui.GroupAVChatActivity;
import com.testdemo.webrtc.webrtclib.ws.IConnectEvent;

import java.lang.ref.SoftReference;


/**
 * Created by dds on 2019/1/7.
 * android_shuai@163.com
 */
public class WebrtcUtil {

    //外网信令地址
    public static final String HOST = "47.93.186.97";

    // turn and stun，内网测试时不需要
    private static MyIceServer[] iceServers = {
            new MyIceServer("stun:stun.l.google.com:19302"),

            // 测试地址1
            new MyIceServer("stun:" + HOST + ":3478?transport=udp"),
            new MyIceServer("turn:" + HOST + ":3478?transport=udp",
                    "ddssingsong",
                    "123456"),
            new MyIceServer("turn:" + HOST + ":3478?transport=tcp",
                    "ddssingsong",
                    "123456"),
    };

    // signalling
    // private static String WSS = "wss://" + HOST + "/wss";

    //本地测试信令地址
    public static String WSS = "ws://10.0.2.164:3000";

    // one to one
    /*public static void callSingle(Activity activity, String wss, String roomId, boolean videoEnable) {
        if (TextUtils.isEmpty(wss)) {
            wss = WSS;
        }
        WebRTCManager.getInstance().init(wss, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                ChatSingleActivity.openActivity(activity, videoEnable);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
        WebRTCManager.getInstance().connect(videoEnable ? MediaType.TYPE_VIDEO : MediaType.TYPE_AUDIO, roomId);
    }*/

    // Videoconferencing
    public static void call(Activity activity, String wss, String roomId) {
        if (TextUtils.isEmpty(wss)) {
            wss = WSS;
        }
        SoftReference<Activity> reference = new SoftReference<>(activity);
        WebRTCManager.getInstance().init(wss, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                if (reference.get() != null)
                    GroupAVChatActivity.Companion.openActivity(reference.get());
            }

            @Override
            public void onFailed(String msg) {

            }
        });
        WebRTCManager.getInstance().connect(MediaType.TYPE_MEETING, roomId);
    }


}
