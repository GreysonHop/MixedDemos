package com.testdemo.testAnim

import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import com.testdemo.R
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.transition.TransitionManager
import com.testdemo.BaseCommonActivity
import kotlinx.android.synthetic.main.act_constraintanim.*


/**
 * Create by Greyson
 */
class ConstraintAnimAct : BaseCommonActivity() {

    private lateinit var constraintLayout: ConstraintLayout
    private var isDetail = false

    override fun getLayoutResId(): Int {
        return R.layout.act_constraintanim
    }

    override fun initView() {
        constraintLayout = findViewById(R.id.cl_root)

        val constraintSet1 = ConstraintSet()
        val constraintSet2 = ConstraintSet()

        constraintSet2.clone(this, R.layout.act_constraintanim_detail)
        constraintSet1.clone(constraintLayout)

        iv_poster.setOnClickListener {
            TransitionManager.beginDelayedTransition(constraintLayout)
            isDetail = if (!isDetail) {
                constraintSet2.applyTo(constraintLayout)
                true
            } else {
                constraintSet1.applyTo(constraintLayout)
                false
            }
        }

        findViewById<TextView>(R.id.tv_desc)?.let { tv ->
            tv.setOnClickListener {
                Toast.makeText(this, tv.text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}