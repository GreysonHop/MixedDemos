package com.testdemo.test_ipc;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * Create by Greyson on 2023/03/30
 */
class MessengerService extends Service {
    //TODO greyson_2023/3/30 如果是同一个 App 里面的跨进程访问，可能需要下面的配置（https://zhuanlan.zhihu.com/p/63333559）：
    // 配置文件中，service 的 android:process 属性用于在一个单独进程中启动 service: <service android:name=".MessengerService" android:process=":custom_process"/>
    private static final String TAG = MessengerService.class.getSimpleName();
    private Messenger mMessenger = new Messenger(new MessengerHandler());
    private static final int MSG_FROM_CLIENT = 0;
    private static String MSG_KEY = "msg_key";
    private static final int MSG_FROM_SERVICE = 1;

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_CLIENT:
                    Log.d(TAG, "receive msg from client: msg = [" + msg.getData().getString(MSG_KEY) + "]");
                    Toast.makeText(MessengerService.this, "receive msg from client: msg = [" + msg.getData().getString(MSG_KEY) + "]", Toast.LENGTH_SHORT).show();
                    Messenger client = msg.replyTo;
                    Message replyMsg = Message.obtain(null, MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString(MSG_KEY, "收到消息");
                    replyMsg.setData(bundle);
                    try {
                        client.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
