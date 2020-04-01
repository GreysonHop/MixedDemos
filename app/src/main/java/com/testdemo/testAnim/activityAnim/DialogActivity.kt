package com.testdemo.testAnim.activityAnim

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import com.luck.picture.lib.permissions.RxPermissions
import com.testdemo.BaseActivity
import com.testdemo.R
import com.testdemo.testPictureSelect.imageLoader.ChatPictureBean
import com.testdemo.testPictureSelect.imageLoader.ImageLoadTask
import com.testdemo.testPictureSelect.imageLoader.MediaFolder
import com.testdemo.testPictureSelect.imageLoader.MediaLoadCallback
import kotlinx.android.synthetic.main.act_dialog.*
import java.util.*

/**
 * Create by Greyson
 */
class DialogActivity : BaseActivity() {

    var rxPermissions: RxPermissions? = null
    var scanThread: Thread? = null

    var mediaPageAdapter = MediaPageAdapter()
    var mediaFolderMap: Map<Int, Array<MediaFolder?>>? = null
    var mediaIdList = ArrayList<Int>()

    var allAlbumPathList = ArrayList<ChatPictureBean?>() //所有图库图片的集合
    var picCheckedList = ArrayList<String>() //被选中的集合
    var isOriginPick = false //是否原图
    private val mMaxSelectedCount = 9 //最多能选择9张图片
    //文件夹数据源
    private var mMediaFolderList: List<MediaFolder>? = null
    private val selectMediaFolder: MediaFolder? = null

    override fun getLayoutResId(): Int {
        return R.layout.act_dialog
    }

    override fun initView() {
        tab_mediaType
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
        rxPermissions!!.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {granted ->
            if (granted) {
                loadMedia()
            } else {
                Toast.makeText(this, "没权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMedia() {
        //只加载图片
        val mediaLoadTask: Runnable = ImageLoadTask(this, object : MediaLoadCallback {
            override fun loadMediaSuccess(mediaFolderList: Map<Int, Array<MediaFolder?>>) {
                //if (tvAlbumTitle == null) return
                runOnUiThread {
                    if (mediaFolderList.isNotEmpty()) { //图片文件夹数据
                        mediaFolderMap = mediaFolderList
                        mediaIdList.addAll(mediaFolderList.keys)

                        /*mImageFolderPopupWindow = ImageFolderPopupWindow(this@AlbumActivity, mMediaFolderList)
                        mImageFolderPopupWindow.setAnimationStyle(R.style.imageFolderAnimator)
                        mImageFolderPopupWindow.getAdapter().setOnImageFolderChangeListener(this@AlbumActivity)
                        mImageFolderPopupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
                            //旋转0度是复位ImageView
                            ivArrow.animate().setDuration(500).rotation(0f).start()
                        })*/

                        vp_media.adapter = mediaPageAdapter
                        vp_media.currentItem = 0
                        changeSelectedFolder(1)
                    }
                }
            }
        })
        //CommonExecutor.getInstance().execute(mediaLoadTask);

        //CommonExecutor.getInstance().execute(mediaLoadTask);
        scanThread = Thread(mediaLoadTask)
        scanThread?.start()
    }

    //将图片与视频的分开
    private fun dealList(mediaFolderList: List<MediaFolder>) {

    }

    /**
     * 切换文件夹、目录
     * @param position
     */
    private fun changeSelectedFolder(position: Int) {
        mediaFolderMap?.let {map ->
            val array = arrayOfNulls<List<ChatPictureBean>?>(2)
            map[mediaIdList[position]]?.let {
                var title: String? = null
                array[0] = it[0]?.let {mediaFolder ->
                    title = mediaFolder.folderName
                    mediaFolder.mediaFileList
                }
                array[1] = it[1]?.mediaFileList
                tv_title.text = title
            }

            mediaPageAdapter.setNewData(array)
        }
    }
}