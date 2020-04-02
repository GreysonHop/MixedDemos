package com.testdemo.testAnim.activityAnim

import android.Manifest
import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.luck.picture.lib.permissions.RxPermissions
import com.testdemo.BaseActivity
import com.testdemo.R
import com.testdemo.testPictureSelect.imageLoader.MediaBean
import com.testdemo.testPictureSelect.imageLoader.ImageLoadTask
import com.testdemo.testPictureSelect.imageLoader.MediaFolder
import com.testdemo.testPictureSelect.imageLoader.MediaLoadCallback
import kotlinx.android.synthetic.main.act_dialog.*
import kotlin.collections.ArrayList

/**
 * Create by Greyson
 */
class DialogActivity : BaseActivity() {

    var rxPermissions: RxPermissions? = null
    var scanThread: Thread? = null

    var mediaPageAdapter = MediaPageAdapter()
    var folderListAdapter = FolderListAdapter()
    //    var mediaFolderMap: Map<Int, Array<MediaFolder?>>? = null
    var mediaIdList = ArrayList<Int>()


    private val folderList = ArrayList<MediaFolder>()
    private val videoFolderList = ArrayList<MediaFolder?>()
    private val imageFolderList = ArrayList<MediaFolder?>()

    var allAlbumPathList = ArrayList<MediaBean?>() //所有图库图片的集合
    private var picCheckedList = ArrayList<String>() //被选中的集合
    private var isOriginPick = false //是否原图
    private val mMaxSelectedCount = 9 //最多能选择9张图片
    //文件夹数据源
    private var mMediaFolderList: List<MediaFolder>? = null
    private val selectMediaFolder: MediaFolder? = null

    override fun getLayoutResId(): Int {
        return R.layout.act_dialog
    }

    override fun initView() {
        vp_media.adapter = mediaPageAdapter
        mediaPageAdapter.setNewData(arrayOfNulls(2))
        vp_media.currentItem = 0
        rv_album_list.adapter = folderListAdapter

        tab_mediaType.setupWithViewPager(vp_media)
        val videoTab = LayoutInflater.from(this).inflate(R.layout.tab_item_media, null)
        (videoTab.findViewById(R.id.tv_tb_item_title) as TextView).text = "视频"
        val imageTab = LayoutInflater.from(this).inflate(R.layout.tab_item_media, null)
        (imageTab.findViewById(R.id.tv_tb_item_title) as TextView).text = "图片"
        /*val tab1 = tab_mediaType.newTab()
        tab1.customView = videoTab
        tab_mediaType.addTab(tab1, true)
        val tab2 =  tab_mediaType.newTab()
        tab2.customView = imageTab
        tab_mediaType.addTab(tab2, false)*/
        tab_mediaType.getTabAt(0)?.customView = videoTab
        tab_mediaType.getTabAt(1)?.customView = imageTab
        tab_mediaType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(p0: TabLayout.Tab?) {

            }
        })
        folderListAdapter.setOnFolderCheckedChangeListener(object : FolderListAdapter.OnFolderCheckedChangeListener {
            override fun onFolderCheckedChange(view: View, position: Int) {
                rv_album_list.visibility = View.GONE
                changeSelectedFolder(position)
            }
        })

        val showFolderList = {
            if (rv_album_list.visibility == View.GONE) {//to show
                val translateInAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
                translateInAnimation.duration = 300
                rv_album_list.animation = translateInAnimation

                /*val rv_height = rv_album_list.height.toFloat()
                if (rv_album_list.translationY == 0f) {
                    rv_album_list.translationY = -rv_height
                }*/
                rv_album_list.visibility = View.VISIBLE
//                rv_album_list.animate().setDuration(300).translationY(0f).start()
                iv_arrow.animate().setDuration(300).scaleY(-1f).start()

            } else {
                val translateOutAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
                translateOutAnimation.duration = 300
                rv_album_list.animation = translateOutAnimation

                rv_album_list.visibility = View.GONE
//                rv_album_list.animate().setDuration(300).translationY(-rv_album_list.height.toFloat()).start()
                iv_arrow.animate().setDuration(300).scaleY(1f).start()

            }
        }
        iv_arrow.setOnClickListener { showFolderList() }
        tv_title.setOnClickListener { showFolderList() }
    }

    override fun initData() {
        checkPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanThread?.takeIf { it.isAlive }?.interrupt()
    }

    @SuppressLint("CheckResult")
    private fun checkPermission() {
        if (rxPermissions == null) {
            rxPermissions = RxPermissions(this)
        }
        rxPermissions!!.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe { granted ->
            if (granted) {
                loadMedia()
            } else {
                Toast.makeText(this, "没权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMedia() {
        val mediaLoadTask: Runnable = ImageLoadTask(this, object : MediaLoadCallback {
            override fun loadMediaSuccess(mediaFolderMap: Map<Int, Array<MediaFolder?>>, mediaFolderList: SparseArray<MediaFolder>) {
                runOnUiThread {
                    if (mediaFolderMap.isNotEmpty()) { //图片文件夹数据
                        dealList(mediaFolderMap, mediaFolderList)

                        changeSelectedFolder(0)

                        folderListAdapter.setNewData(folderList)
                    }
                }
            }
        })

        scanThread = Thread(mediaLoadTask)
        scanThread?.start()
    }

    //将图片与视频的分开
    private fun dealList(mediaFolderMap: Map<Int, Array<MediaFolder?>>, mediaFolderList: SparseArray<MediaFolder>) {
        if (mediaFolderMap.isEmpty()) {
            return
        }

        mediaIdList.clear()
        imageFolderList.clear()
        videoFolderList.clear()
        folderList.clear()
        mediaFolderMap.forEach { (folderId, mediaFolderArray) ->
            mediaIdList.add(folderId)
            videoFolderList.add(mediaFolderArray[0])
            imageFolderList.add(mediaFolderArray[1])
            folderList.add(mediaFolderList.get(folderId))
        }
    }

    /**
     * 切换文件夹、目录
     * @param position
     */
    private fun changeSelectedFolder(position: Int) {
        if (folderList.size > position) {
            tv_title.text = folderList[position].folderName
        }
        val array = arrayOfNulls<List<MediaBean>?>(2)
        array[0] = videoFolderList[position]?.mediaFileList//?.toList()
        array[1] = imageFolderList[position]?.mediaFileList
        println("Greyson, 数组长度： ${array.size}")
        mediaPageAdapter.setNewData(array)
    }
}