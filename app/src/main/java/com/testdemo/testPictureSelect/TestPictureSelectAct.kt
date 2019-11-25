package com.testdemo.testPictureSelect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.entity.LocalMedia
import com.testdemo.R
import kotlinx.android.synthetic.main.act_test_picture_select.*
import kotlinx.android.synthetic.main.nim_picture_panel.*

class TestPictureSelectAct : FragmentActivity(), PictureSelectPanel.OnSendClickListener {

    lateinit var panel : PictureSelectPanel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_picture_select)

        panel = PictureSelectPanel(this, pictureLayout)
        panel.setOnSendClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        panel.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSendImage(list: MutableList<LocalMedia>?) {
        val isCompress = panel.isCompressMode
        list?.forEach {
            if (isCompress) {
                Log.w("greyson", "send compress: ${it.compressPath}")
            } else {
                Log.w("greyson", "send origin: ${it.path}")
            }
        }
    }

    override fun onSendVideo(list: MutableList<LocalMedia>?) {
    }

    fun onClick(view: View) {
        if (pictureTV.isSelected) {
            pictureTV.isSelected = false
            panel.hide()
        } else {
            pictureTV.isSelected = true
            panel.show()
        }
    }
}