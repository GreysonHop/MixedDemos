package com.testdemo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import com.testdemo.broken_lib.Utils
import kotlinx.android.synthetic.main.test_act2.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Create by Greyson on 2019/09/15
 * 通过动画做一个从屏幕下外面慢慢向上移动的组件，和可上下拉的组件
 */
class TestActivity2 : Activity(), View.OnClickListener {
    private val TAG = "greyson_Test2"

    private var isSee = false

    private lateinit var popupWindow: PopupWindow
    private var popupMenuView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_act2)

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.setOnChronometerTickListener {
            Log.i(TAG, "base = ${chronometer.base} - ${chronometer.text}" +
                    " - ${timeTick2Second(chronometer.text.toString())}")
        }
        chronometer.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime() - (9 * 3600 + 59 * 60 + 55) * 1000
            chronometer.start()
        }

        //倒计时
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        try {
            val date = sdf.parse("2019-10-09 08:29:00")//TODO pay attention on the expire time
            val expireMilliseconds = date.time
            val currentMilliseconds = System.currentTimeMillis()
            if (expireMilliseconds > currentMilliseconds) {
                val countDownTimer = object : CountDownTimer(expireMilliseconds - currentMilliseconds, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        popupTV.text = second2Minute(millisUntilFinished / 1000)
                    }

                    override fun onFinish() {
                        Toast.makeText(this@TestActivity2, "倒计时完成！！！", Toast.LENGTH_SHORT).show()
                        popupTV.text = "已过期"
                    }
                }
                countDownTimer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        dragTV.setOnClickListener(this)

        blackBgIV.setOnClickListener(this)
        anim_btn.setOnClickListener(this)
        popupTV.setOnClickListener(this)
        shareLayout.setOnClickListener(this)

        val scaleAnim = ObjectAnimator.ofFloat(dragLayout, "scaleX", 0f, 1f)
        val scaleAnim2 = ObjectAnimator.ofFloat(dragLayout, "scaleY", 0f, 1f)
        val set = AnimatorSet()
        set.duration = 800
        set.setTarget(dragLayout)
        set.playTogether(scaleAnim, scaleAnim2)
        set.start()
    }

    private fun timeTick2Second(time: String): Int {
        var h: String?
        var m: String?
        var s: String?
        val index1 = time.indexOf(":")
        val index2 = time.lastIndexOf(":")

        if (index2 == -1) {
            return 0
        }
        if (index1 == index2) {//时间不到1小时,00:00
            h = "0"
            m = time.substring(0, index2)
        } else {//格式 1:00:00 或 111:00:00
            h = time.substring(0, index1)
            m = time.substring(index1 + 1, index2)
        }
        s = time.substring(index2 + 1)
        return h.toInt() * 3600 + m.toInt() * 60 + s.toInt()
    }

    private fun second2Minute(secondParam: Long): String {
        var second = secondParam
        val hour: Int = (second / 3600).toInt()
        second -= hour * 3600 //可以换成 second % 3600

        val minute: Int = (second / 60).toInt()
        second -= minute * 60

        val minuteStr = if (minute < 10) "0$minute" else minute.toString()
        val secondStr = if (second < 10) "0$second" else second.toString()

        if (hour == 0) {
            return "$minuteStr:$secondStr"
        } else {
            return "$hour:$minuteStr:$secondStr"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dragTV -> {
                Toast.makeText(this, "drag TV said Hello !!!!!", Toast.LENGTH_SHORT).show()
                (application as TestApplication).activity = this
                startActivity(Intent(this, TestActivity2BlurBg::class.java))
            }

            R.id.blackBgIV -> {
                isSee = !isSee
                showShareLayout(isSee)
            }

            R.id.bgIV -> {
                Toast.makeText(this, "you click picture bg bg!-------", Toast.LENGTH_SHORT).show()
            }

            R.id.shareLayout -> {
                Toast.makeText(this, "you click shareLayout!!!!!", Toast.LENGTH_SHORT).show()
                getInviteCode()
            }

            R.id.anim_btn -> {
                Log.i(TAG, "click button dragLayout's y=${dragLayout.y} - top=${dragLayout.top}")
                anim_btn.isSelected = !anim_btn.isSelected

                isSee = !isSee
                showShareLayout(isSee)
            }

            R.id.popupTV -> {
                if (popupMenuView == null) {
                    popupMenuView = ListView(this).also {
                        //it.setBackgroundResource(R.drawable.menu);
                        val menuList = ArrayList<String>()
                        menuList.add("日榜单")
                        menuList.add("周榜单")
                        menuList.add("月榜单")
                        it.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, menuList)
                        it.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                            Toast.makeText(this@TestActivity2, "you click $position", Toast.LENGTH_SHORT).show()
                        }

                        popupWindow = PopupWindow(this)
                        popupWindow.contentView = it
                        popupWindow.width = Utils.dp2px(86)
                        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        popupWindow.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_popup))
                        popupWindow.isOutsideTouchable = true
                        popupWindow.isFocusable = true
                        //popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
                    }
                    popupWindow.showAsDropDown(popupTV)
                }
            }
        }
    }

    private fun showShareLayout(show: Boolean) {
        if (show) {
            val alphaInAnimation = AlphaAnimation(0.0f, 0.9f)
            alphaInAnimation.duration = 300
            val translateInAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
            translateInAnimation.duration = 300

            blackBgIV.animation = alphaInAnimation
            shareLayout.animation = translateInAnimation

            shareLayout.visibility = View.VISIBLE
            blackBgIV.visibility = View.VISIBLE

        } else {
            val alphaOutAnimation = AlphaAnimation(0.9f, 0.0f)
            alphaOutAnimation.duration = 300

            val translateOutAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
            translateOutAnimation.duration = 300

            blackBgIV.animation = alphaOutAnimation
            shareLayout.animation = translateOutAnimation

            shareLayout.visibility = View.GONE
            blackBgIV.visibility = View.GONE

        }
    }

    private fun getInviteCode() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboardManager.hasText()) {
            val text = clipboardManager.text.toString()
            val pattern = Pattern.compile("(?<=station_invite_code:).{6}")
            val matcher = pattern.matcher(text)

            Log.i("greyson", "clip content = $text - groupCount=${matcher.groupCount()}")
            if (matcher.find()) {
                val mInviteCode = matcher.group()
                Log.i("greyson", " group=$mInviteCode")
            }
        }
    }
}