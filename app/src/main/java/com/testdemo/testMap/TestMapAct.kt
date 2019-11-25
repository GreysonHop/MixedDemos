package com.testdemo.testMap

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.act_test_map.*
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.testdemo.R


/**
 * Create by Greyson
 */
class TestMapAct : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}