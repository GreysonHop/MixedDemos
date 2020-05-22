package com.testdemo.testView.delayDialog

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView
import com.testdemo.R

/**
 * Created by Greyson on 2020/05/18
 * <BR/>
 * 支持延迟dismiss()和动画执行完再dismiss()。
 * 原生的Dialog就算在Style中设置动画和动画的Duration，如果所属的Activity关闭了，Dialog也会被马上关闭
 */
class DelayDialog(context: Context) : Dialog(context, R.style.GiftAnimDialog), Animator.AnimatorListener {

    lateinit var params: Params
    private lateinit var tvContent: TextView
    private var animating = false
    private var dismissDelayAction: Runnable? = null
    private var dismissCallback: ((DialogInterface) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvContent = TextView(context)
        tvContent.setBackgroundResource(R.drawable.bg_corner15_blue)
        tvContent.setPadding(dp2px(15f), dp2px(10f), dp2px(15f), dp2px(10f))
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        tvContent.setTextColor(context.resources.getColor(R.color.white))
        tvContent.text = params.content
        tvContent.gravity = Gravity.CENTER
        tvContent.minWidth = dp2px(160f)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        setContentView(tvContent, layoutParams)

        setOnDismissListener { dismissCallback?.invoke(it) }
    }

    override fun dismiss() {
        dismissDelay(params.dismissDelayMillis)
    }

    fun dismiss(callback: (DialogInterface) -> Unit) {
        dismissDelay(params.dismissDelayMillis, callback)
    }

    /**
     * (忽略动画和延迟Dismiss等属性，强制地)立即关闭Dialog
     */
    fun dismissForce() {
        tvContent.removeCallbacks(dismissDelayAction)
        if (animating) {
            tvContent.animate().cancel()
            animating = false
        }
        super.dismiss()
    }

    /**
     * 延迟指定毫秒时间后关闭Dialog，重复调用无效，并且要覆盖Builder中dismissDelayMillis变量的效果（但不影响其值）
     * @param callback OnDismissListener的回调。注意！如果当前dialog通过方法setOnDismissListener(OnDismissListener listener)自
     * 己设置了监听事件的话，那么该callback将不会被回调
     */
    fun dismissDelay(delayMillis: Long, callback: ((DialogInterface) -> Unit)? = null) {
        if (!isShowing || animating || dismissDelayAction != null) {
            return
        }

        dismissCallback = callback

        if (delayMillis > 0) {
            dismissDelayAction = Runnable { runAnimForDismiss(params.enableAnim) }
            tvContent.postDelayed(dismissDelayAction, delayMillis)

        } else {
            runAnimForDismiss(params.enableAnim)
        }
    }

    @JvmOverloads
    fun setContent(strId: Int, hasAnim: Boolean = true) {
        setContent(params.context.getString(strId), hasAnim)
    }

    @JvmOverloads
    fun setContent(newContent: String, hasAnim: Boolean = true) {
        if (hasAnim) {
            val enlargeAnim = ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            enlargeAnim.duration = 250
            tvContent.animation = enlargeAnim
        }
        tvContent.text = newContent
    }

    private fun runAnimForDismiss(run: Boolean) {
        if (run) {
            tvContent.animate()
                .scaleX(0.05f)
                .scaleY(0.05f)
                .alpha(0.1f)
                .setDuration(params.duration)
                .setListener(this)
                .start() //启动 关闭Dialog的动画

        } else {
            super.dismiss()
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {}
    override fun onAnimationStart(animation: Animator?) {
        animating = true
    }

    override fun onAnimationCancel(animation: Animator?) {
        animating = false
    }

    override fun onAnimationEnd(animation: Animator?) {
        animating = false
        super.dismiss()
    }


    class Builder(context: Context) {
        private var params = Params(context)

        /**
         * 设置Dialog显示的内容
         */
        fun setContent(content: String): Builder {
            params.content = content
            return this
        }

        fun setContent(strId: Int): Builder {
            return setContent(params.context.getString(strId))
        }

        fun setDismissDelayMillis(millis: Long): Builder {
            params.dismissDelayMillis = millis
            return this
        }

        /**
         * 执行动画后再dismiss
         */
        fun enableAnim(enable: Boolean): Builder {
            params.enableAnim = enable
            return this
        }

        fun setAnimDuration(duration: Long): Builder {
            params.duration = duration
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            params.cancelable = cancelable
            return this
        }

        fun create(): DelayDialog {
            val dialog = DelayDialog(params.context)
            params.apply(dialog)
            dialog.setCancelable(params.cancelable)
            return dialog
        }

        fun show(): DelayDialog {
            val dialog = create()
            dialog.show()
            return dialog
        }

    }

    class Params(var context: Context) {
        var content = ""
        var dismissDelayMillis = 0L //Dialog延迟消失的时长
        var enableAnim = false //如果启动动画，dismiss()会在动画结束后才被调用。
        var duration = 250L //默认关闭动画时长250毫秒
        var cancelable = false //可以通过返回键关闭窗口

        fun apply(dialog: DelayDialog) {
            dialog.params = this
        }
    }

    private fun dp2px(dipValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.resources.displayMetrics).toInt()
    }
}