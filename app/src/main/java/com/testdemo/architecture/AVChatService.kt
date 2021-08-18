package com.testdemo.architecture

import android.annotation.TargetApi
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.testdemo.R
import com.testdemo.testView.nineView.TestNineViewAct
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
        private const val FOREGROUND_CHANNEL_ID = "foreground_tip_channel_001"
        private const val FOREGROUND_CHANNEL_NAME = "foreground tip channel"
        private const val FOREGROUND_CHANNEL_DESC = "foreground tip notification"
    }

    private var notifier = AVChatNotification(this)
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        // createMessageNotificationChannel()
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
        /*val pendingIntent: PendingIntent =
                Intent(this, TestNineViewAct::class.java).let { notificationIntent ->
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }

        val notification: Notification = NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContentTitle(getText(R.string.picture_empty_title))
            .setContentText(getText(R.string.error_alert_message_invalid_bounds))
            .setSmallIcon(R.drawable.video_icon)
            // .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.app_name))
            .setFullScreenIntent(pendingIntent, false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()*/
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
        return super.onStartCommand(intent, flags, startId)
    }


    /*private fun createMessageNotificationChannel() {
        *//*
         * 适配关键：只有8.0+的机器才能创建NotificationChannel，否则会找不到类。target 8.0+才需要去创建一个channel，否则就用默认通道即null
         *//*
        if (!isBuildAndTargetO()) {
            return
        }

        notificationManager?.apply {
            var channel = getNotificationChannel(FOREGROUND_CHANNEL_ID) // 已经存在就不要再创建了，无法修改通道配置
            if (channel == null) {
                channel = buildMessageChannel()
                createNotificationChannel(channel)
            }
        }
    }*/

    @TargetApi(Build.VERSION_CODES.O)
    private fun buildMessageChannel(): NotificationChannel {
        return NotificationChannel(FOREGROUND_CHANNEL_ID, FOREGROUND_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            description = FOREGROUND_CHANNEL_DESC
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableVibration(true)
            enableLights(true)
            setShowBadge(false)
        }
    }

    private fun isBuildAndTargetO(): Boolean {
        Log.d(TAG, "isBuildAndTargetO: SDK_INT=${Build.VERSION.SDK_INT}, targetSdkVersion=${applicationInfo.targetSdkVersion}")
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O //&& applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O
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