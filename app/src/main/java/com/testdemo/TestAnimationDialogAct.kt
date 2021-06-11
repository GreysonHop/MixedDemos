package com.testdemo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.View.LAYER_TYPE_SOFTWARE
import android.view.ViewGroup
import android.widget.*
import androidx.core.animation.addListener
import com.testdemo.broken_lib.Utils
import com.testdemo.databinding.ActTestAnimationdialogBinding
import com.testdemo.testView.popmenu.PopMenu
import com.testdemo.testView.popmenu.PopMenuItem
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Create by Greyson on 2019/09/15
 * 通过动画做一个从屏幕下外面慢慢向上移动的组件，和可上下拉的组件
 */
class TestAnimationDialogAct : BaseBindingActivity<ActTestAnimationdialogBinding>(), View.OnClickListener {
    private val TAG = "greyson_Test2"

    private var isSee = false

    private lateinit var popupWindow: PopupWindow
    private var popupMenuView: ListView? = null
    private lateinit var popMenu: PopMenu

    override fun getViewBinding(): ActTestAnimationdialogBinding {
        return ActTestAnimationdialogBinding.inflate(layoutInflater)
    }

    override fun initView() {
        setContentView(R.layout.act_test_animationdialog)

        binding.chronometer.base = SystemClock.elapsedRealtime()
        binding.chronometer.setOnChronometerTickListener {
            Log.i(TAG, "base = ${binding.chronometer.base} - ${binding.chronometer.text}" +
                    " - ${timeTick2Second(binding.chronometer.text.toString())}")
        }
        binding.chronometer.setOnClickListener {
            binding.chronometer.base = SystemClock.elapsedRealtime() - (9 * 3600 + 59 * 60 + 55) * 1000
            binding.chronometer.start()
        }

        //倒计时
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        try {
            val date = sdf.parse("2019-10-09 08:29:00")//pay attention on the expire time
            val expireMilliseconds = date.time
            val currentMilliseconds = System.currentTimeMillis()
            if (expireMilliseconds > currentMilliseconds) {
                val countDownTimer = object : CountDownTimer(expireMilliseconds - currentMilliseconds, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        binding.popupTV.text = second2Minute(millisUntilFinished / 1000)
                    }

                    override fun onFinish() {
                        Toast.makeText(this@TestAnimationDialogAct, "倒计时完成！！！", Toast.LENGTH_SHORT).show()
                        binding.popupTV.text = "已过期"
                    }
                }
                countDownTimer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popMenu = PopMenu.Builder().attachToActivity(this)
                .horizontalPadding(10)
                .verticalPadding(0)
                .addMenuItem(
                    PopMenuItem(
                        getString(R.string.picture_take_picture),
                        resources.getDrawable(R.mipmap.ic_launcher),
                        resources.getColor(R.color.textBlack)
                    )
                )
                .addMenuItem(
                    PopMenuItem(
                        getString(R.string.picture_camera),
                        resources.getDrawable(R.mipmap.refresh_loading01),
                        resources.getColor(R.color.textBlack)
                    )
                )
                .addMenuItem(
                    PopMenuItem(
                        getString(R.string.picture_done),
                        resources.getDrawable(R.mipmap.ic_launcher),
                        resources.getColor(R.color.textBlack)
                    )
                )
                .setOnItemClickListener { _, position ->
                    when (position) {
                        0 -> {
                            Log.d("greyson", "TestAnimationDialogAct-onCreate: click0")
                        }

                        1 -> Log.d("greyson", "TestAnimationDialogAct-onCreate: click1")

                        2 -> {
                            Log.d("greyson", "TestAnimationDialogAct-onCreate: click2")
                        }
                    }
                }
                .build()

        binding.dragTV.setOnClickListener(this)

        binding.blackBgIV.setOnClickListener(this)
        binding.blackBgIV.isClickable = false //TODO greyson_2021/6/11 这句是否有用？？
        binding.animBtn.setOnClickListener(this)
        binding.popupTV.setOnClickListener(this)
        binding.shareLayout.setOnClickListener(this)
        binding.btnShowPopMenu.setOnClickListener {
            popMenu.show()
        }

        val scaleAnim = ObjectAnimator.ofFloat(binding.dragLayout, "scaleX", 0f, 1f)
        val scaleAnim2 = ObjectAnimator.ofFloat(binding.dragLayout, "scaleY", 0f, 1f)
        val set = AnimatorSet()
        set.duration = 800
        set.setTarget(binding.dragLayout)
        set.playTogether(scaleAnim, scaleAnim2)
        set.addListener({
            val paint = Paint()
            paint.color = Color.RED
            paint.setShadowLayer(1f, 10f, 10f, Color.GREEN)
            binding.popupTV.setLayerType(LAYER_TYPE_SOFTWARE, null)
            binding.popupTV.setLayerPaint(paint)
        })
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
                binding.blackBgIV.isClickable = false
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
                Log.i(TAG, "click button dragLayout's y=${binding.dragLayout.y} - top=${binding.dragLayout.top}")
                binding.animBtn.isSelected = !binding.animBtn.isSelected

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
                            Toast.makeText(this@TestAnimationDialogAct, "you click $position", Toast.LENGTH_SHORT).show()
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
                }
                popupWindow.showAsDropDown(binding.popupTV)
            }
        }
    }

    private fun showShareLayout(show: Boolean) = if (show) {
        println("greyson, height=${binding.shareLayout.height}")
        binding.shareLayout.animate().setDuration(300).translationY(0f)
                .withEndAction { binding.blackBgIV.isClickable = true }
                .start()
        binding.blackBgIV.animate().setDuration(300).alpha(0.9f).start()

        binding.dragLayout.animate().setDuration(300).scaleX(0.0f).alpha(0.2f).start()
        binding.popupTV.animate().setDuration(300).scaleY(-1f).start()
    } else {
        binding.shareLayout.animate().setDuration(300).translationY(binding.shareLayout.height.toFloat()).start()
        binding.blackBgIV.animate().setDuration(300).alpha(0.0f).start()

        binding.dragLayout.animate().setDuration(300).scaleX(1f).alpha(1f).start()
        binding.popupTV.animate().setDuration(300).scaleY(1f).start()
    }

    /*private fun showShareLayout(show: Boolean) {
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
    }*/

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

    override fun onDestroy() {
        super.onDestroy()
        popMenu.destroy()
    }
}