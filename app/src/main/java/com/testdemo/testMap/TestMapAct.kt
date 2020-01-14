package com.testdemo.testMap

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.act_test_map.*
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.testdemo.R
import com.testdemo.broken_lib.Utils
import com.testdemo.testMap.places.StringUtil
import com.testdemo.testNCalendar.RecyclerViewAdapter
import com.testdemo.testStartMode.ActivityA
import com.testdemo.util.PermissionUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


/**
 * Create by Greyson
 */
class TestMapAct : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mPlacesClient: PlacesClient
    private lateinit var mMap: GoogleMap
    private var mLocateMyGoogle = false
    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.view?.isClickable = false
        mapFragment.getMapAsync(this)*/
        mv_map_google.isClickable = false
        mv_map_google.getMapAsync(this)
        mv_map_google.onCreate(savedInstanceState)

        iv_map_locate.setOnClickListener { locateMyLocation(true) }

        tv_show_location.setOnClickListener { startActivity(Intent(this, ActivityA::class.java)) }

        //下面是跳转第三方地图应用的例子代码
        btn_start_map.setOnClickListener {
            val stringBuffer = StringBuffer()
            val pageStringList = packageManager.getInstalledPackages(0)
            pageStringList.forEach {
                val pageName = it.packageName
                Log.i("greyson", "包名有：$pageName")
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

        Thread {
            val client = OkHttpClient()
            val request = Request.Builder().url("http://ip.taobao.com/service/getIpInfo.php?ip=myip").get().build()
            try {
                val response = client.newCall(request).execute()
                Log.i("greyson", "淘宝接口调用：${response.body().string()}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()

        rv_map_search_list.adapter = RecyclerViewAdapter(this)
        refreshLayout.isEnabled = false
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
        mPlacesClient = Places.createClient(this)
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-33.8666199, 151.1958527)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/


        mMap.moveCamera(CameraUpdateFactory.zoomTo(14f))
        mMap.uiSettings.isRotateGesturesEnabled = false//不能旋转地图
        mMap.uiSettings.isCompassEnabled = false//不显示指南针
        mMap.uiSettings.isMyLocationButtonEnabled = false
//        mMap.isMyLocationEnabled = true//todo 必须有定位权限
        if (PermissionUtils.isGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION
                        , android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            mLocateMyGoogle = true
        }

        /*val googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)*/

        mMap.setOnCameraMoveCanceledListener {
            Log.i("greyson", "OnCameraMoveCanceled")
        }

        mMap.setOnCameraIdleListener {
            Log.i("greyson", "nCameraIdle position = ${mMap.cameraPosition}")
            searchRound(mMap.cameraPosition.target)
        }

        mMap.setOnMyLocationChangeListener {
            if (mLocateMyGoogle) {
                mLocateMyGoogle = !locateMyLocation(false)
            }
        }
    }


    private fun searchRound(latLng: LatLng) {
        refreshLayout.isRefreshing = true

        Thread {
            val client = OkHttpClient()
            val request1 = Request.Builder().url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${latLng.latitude},${latLng.longitude}&rankby=distance&type=food&key=${getString(R.string.google_search_api_key)}").get().build()
            try {
                val response = client.newCall(request1).execute()
                val bodyStr = response.body().string()
                if (bodyStr.contains("error")) {//请求有错误
                    Toast.makeText(this, "请求出现问题！", Toast.LENGTH_SHORT).show()
                    Log.i("greyson", "谷歌周围API调用：${bodyStr}")

                } else {//响应正常
                    val mapResult = Gson().fromJson<MapSearchResult>(bodyStr, MapSearchResult::class.java)
                    Log.d("greyson", "response:  $bodyStr")
                    mapResult.results.forEach {
                        Log.d("greyson", "name=${it.name}, address=${it.plusCode?.compound_code}")
//                        it.place_id?.let { getPlaceById(it) }
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                runOnUiThread {
                    refreshLayout.isRefreshing = false
                }
            }
        }.start()

    }

    private fun getPlaceById(id: String) {
        val request = FetchPlaceRequest.newInstance(id, arrayListOf(*Place.Field.values())
                /*arrayListOf(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.ID
                , Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS)*/)
        val placeTask = mPlacesClient.fetchPlace(request)

        placeTask.addOnSuccessListener {
            Log.i("greyson", "fetch place success : ${StringUtil.stringify(it, true)}")
        }

        placeTask.addOnFailureListener {
            Log.e("greyson", "fetch failed!!! ${it}")
        }

    }

    private fun locateMyLocation(animate: Boolean): Boolean {
        var result = false
        if (PermissionUtils.isGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION
                        , android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMap.myLocation?.let {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
                result = true
            }
        }


        /*mMap?.let { gMap ->
            if (gMap.isMyLocationEnabled) {
                gMap.myLocation?.let {
                    val latLng = CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 14f)
                    if (animate) {
                        gMap.animateCamera(latLng)
                    } else {
                        gMap.moveCamera(latLng)
                    }
                    result = true
                }
            }
        }*/
        return result
    }

    override fun onResume() {
        super.onResume()
        mv_map_google.onResume()
    }

    override fun onStart() {
        super.onStart()
        mv_map_google.onStart()
    }

    override fun onPause() {
        super.onPause()
        mv_map_google.onPause()
    }

    override fun onStop() {
        super.onStop()
        mv_map_google.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mv_map_google.onDestroy()
    }

    override fun finish() {
        super.finish()
        //注释掉activity本身的过渡动画
//        overridePendingTransition(0, R.anim.bottom_menu_out)//TODO 不在这里设置的话，退出的动画会变成<Application>设置的Theme里面的动画
    }
}