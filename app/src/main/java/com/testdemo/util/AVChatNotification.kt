package com.testdemo.util

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.testdemo.MainActivity
import com.testdemo.R
import com.testdemo.testView.nineView.TestNineViewAct

/**
 * 音视频聊天通知栏
 * Created by huangjun on 2015/5/14.
 */
class AVChatNotification(private val context: Context) {
    companion object {
        private const val BG_CHANNEL_ID = "avchat_tip_channel_001"
        private const val BG_CHANNEL_NAME = "音视频通话相关的通知"
        private const val BG_CHANNEL_DESC = "如果不打开可能会收不到音视频通话的通知"
        public const val CALLING_NOTIFY_ID = 0x101
        private const val MISS_CALL_NOTIFY_ID = 0x102
    }

    private var notificationManager: NotificationManager? = null
    private var callingNotification: Notification? = null
    private var missCallNotification: Notification? = null
    private var displayName: String? = null

    fun init(displayName: String?) {
        this.displayName = displayName
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        createMessageNotificationChannel()
    }

    fun activeCallingNotification(active: Boolean) {
        notificationManager?.apply {
            if (active) {
                buildCallingNotification()
                notify(CALLING_NOTIFY_ID, callingNotification)
            } else {
                cancel(CALLING_NOTIFY_ID)
            }
        }
    }

    fun activeMissCallNotification(active: Boolean) {
        notificationManager?.apply {
            if (active) {
                buildMissCallNotification()
                notify(MISS_CALL_NOTIFY_ID, missCallNotification)
            } else {
                cancel(MISS_CALL_NOTIFY_ID)
            }
        }
    }

    /**
     *  [Intent.setFlags()] 方法可帮助保留用户在通过通知打开应用后的预期导航体验。但您是否要使用这一方法取决于您要启动的 Activity 类型，类型可能包括：

     * 专用于响应通知的 Activity。用户在正常使用应用时不会无缘无故想导航到这个 Activity，因此该 Activity 会启动
     * 一个新任务，而不是添加到应用的现有任务和返回堆栈。这就是以上示例中创建的 Intent 类型。
     * 应用的常规应用流程中存在的 Activity。在这种情况下，启动 Activity 时应创建返回堆栈，以便保留用户对返回和向上按钮的预期。
     */
    fun buildCallingNotification(): Notification? {
        if (callingNotification == null) {
            val localIntent = Intent().apply {
                setClass(context, TestNineViewAct::class.java)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val title = displayName
            val content = "测试通知的内容！。。。"
            val iconId = R.drawable.call_icon_gift
            val pendingIntent = PendingIntent.getActivity(context, 0, localIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            callingNotification = makeNotification(pendingIntent, title, content, title, iconId, false, vibrate = false)
                .apply {
                    flags = flags or Notification.FLAG_NO_CLEAR
                }
        }
        return callingNotification
    }

    private fun buildMissCallNotification() {
        if (missCallNotification == null) {
            val notifyIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                action = Intent.ACTION_VIEW
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val content = "语音通话"
            val tickerText = "$displayName: 【网络通话】"
            val iconId = R.drawable.call_icon_gift
            missCallNotification = makeNotification(pendingIntent, displayName, content, tickerText, iconId, true, vibrate = true)
        }
    }

    private fun makeNotification(
        pendingIntent: PendingIntent, title: String?, content: String, tickerText: String?,
        iconId: Int, ring: Boolean, vibrate: Boolean
    ): Notification {
        var defaults = Notification.DEFAULT_LIGHTS
        if (vibrate) {
            defaults = defaults or Notification.DEFAULT_VIBRATE
        }
        if (ring) {
            defaults = defaults or Notification.DEFAULT_SOUND
        }
        return NotificationCompat.Builder(context, BG_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(iconId).setDefaults(defaults)
            .setTicker(tickerText)
            .setAutoCancel(false)
            .setOngoing(true)
            // .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, false)
            .setPriority(NotificationCompat.PRIORITY_MAX) //官方表示必须有，兼容7.1及以下
            // .setCategory()
            .build()
    }

    private fun createMessageNotificationChannel() {
        /*
         * 适配关键：只有8.0+的机器才能创建NotificationChannel，否则会找不到类。target 8.0+才需要去创建一个channel，否则就用默认通道即null
         */
        if (!isBuildAndTargetO()) {
            return
        }

        notificationManager?.apply {
            var channel = getNotificationChannel(BG_CHANNEL_ID) // 已经存在就不要再创建了，无法修改通道配置
            if (channel == null) {
                channel = buildMessageChannel()
                createNotificationChannel(channel)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun buildMessageChannel(): NotificationChannel {
        return NotificationChannel(BG_CHANNEL_ID, BG_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            description = BG_CHANNEL_DESC
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
            /*val uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE)
            if (uri != null) {
                setSound(uri, AudioAttributes.Builder().setHapticChannelsMuted())
            }*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setAllowBubbles(true)
            }
        }
    }

    private fun isBuildAndTargetO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O
    }
}