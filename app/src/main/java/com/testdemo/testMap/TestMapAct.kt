package com.testdemo.testMap

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.act_test_map.*
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.widget.Toast
import com.testdemo.R


/**
 * Create by Greyson
 */
class TestMapAct : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_map)

        btn_start_map.setOnClickListener {
            val stringBuffer = StringBuffer()
            val pageStringList = packageManager.getInstalledPackages(0)
            pageStringList.forEach {
                val pageName = it.packageName
                Log.d("greyson", "包名有：$pageName")
                stringBuffer.append(pageName).append(",")
            }

            stringBuffer.contains("com.baidu.BaiduMap")
            if (stringBuffer.contains("com.baidu.BaiduMap")
                    || stringBuffer.contains("com.autonavi.minimap")
                    || stringBuffer.contains("com.sougou.map.anroid.maps")
                    || stringBuffer.contains("com.google.android.apps.maps")
                    || stringBuffer.contains("com.tencent.map")) {
                val mUri = Uri.parse("geo:39.940409,116.355257?q=西直门")
                val mIntent = Intent(ACTION_VIEW, mUri)
                startActivity(mIntent)
            } else {
                Toast.makeText(this, "请安装地图软件,否则无法使用该软件", Toast.LENGTH_SHORT).show()
            }

        }
    }

}