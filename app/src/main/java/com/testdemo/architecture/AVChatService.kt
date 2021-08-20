package com.testdemo.architecture

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.testdemo.util.AVChatNotification
import com.testdemo.util.Utils

/**
 * Created by Greyson on 2020/12/14
 *
 * 在Android11的虚拟机上发现：普通的后台服务（没有运行任何逻辑），当应用最小化、处于后台时，1分钟后会被销毁。
 */
class AVChatService : Service() {
    companion object {
        private const val TAG = "AVChatService"
    }

    private var notifier = AVChatNotification(this)

    override fun onCreate() {
        Log.d(TAG, "onCreate")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getIntExtra("order", 0) == -1) {
            stopForeground(true)
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        notifier.init("语音通话的前台服务")
        Log.d(TAG, "onStartCommand, flags=$flags, startId=$startId, foreground=${Utils.isAppForeground()}")

        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Log.d(TAG, "onStartCommand: 有通知权限将启动前台服务，当前应用在前台=${Utils.isAppForeground()}")
            val notification = notifier.buildCallingNotification()

            // Notification ID cannot be 0.
            // 通知被关掉（NotificationManager.cancel()）的时候，前台服务也会跟着销毁
            startForeground(AVChatNotification.CALLING_NOTIFY_ID, notification)
        } else {
            Log.d(TAG, "onStartCommand: 没有通知权限将stopSelf(); 当前应用在前台=${Utils.isAppForeground()}")
            stopSelf()
        }
        // Log.d(TAG, "AVChatService-foregroundServiceType: $foregroundServiceType")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.w(TAG, "onLowMemory(),")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Activity.TRIM_MEMORY_UI_HIDDEN
        Log.w(TAG, "onTrimMemory(), level=$level")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.w(TAG, "onTaskRemoved(), rootIntent=$rootIntent")
    }

    override fun onDestroy() {
        Log.w(TAG, "onDestroy(), ")
        stopForeground(true)
    }
}