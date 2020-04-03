package com.testdemo.testAnim.activityAnim

import android.Manifest
import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.tabs.TabLayout
import com.luck.picture.lib.permissions.RxPermissions
import com.testdemo.BaseActivity
import com.testdemo.R
import com.testdemo.testPictureSelect.imageLoader.ImageLoadTask
import com.testdemo.testPictureSelect.imageLoader.MediaBean
import com.testdemo.testPictureSelect.imageLoader.MediaFolder
import com.testdemo.testPictureSelect.imageLoader.MediaLoadCallback
import kotlinx.android.synthetic.main.act_dialog.*

/**
 * Create by Greyson
 */
class DialogActivity : BaseActivity() {

    var rxPermissions: RxPermissions? = null
    var scanThread: Thread? = null

    var mediaPageAdapter = MediaPageAdapter()
    var folderListAdapter = FolderListAdapter()

    private val folderList = ArrayList<MediaFolder>()
    private val videoFolderList = ArrayList<MediaFolder?>()
    private val imageFolderList = ArrayList<MediaFolder?>()

    private val videoCheckedList = ArrayList<String>(1)
    private val picCheckedList = ArrayList<String>(1) //被选中的图片集合
    private var isOriginPick = false //是否原图
    private val mMaxSelectedCount = 9 //最多能选择9张图片

    override fun initialize() {
        super.initialize()
        window.decorView.setBackgroundColor(resources.getColor(R.color.transparent))

    }

    override fun getLayoutResId(): Int {
        return R.layout.act_dialog
    }

    override fun initView() {
        iv_close.setOnClickListener { finish() }
        vp_media.adapter = mediaPageAdapter
        mediaPageAdapter.setNewData(arrayOfNulls(2))
        vp_media.currentItem = 0
        rv_album_list.adapter = folderListAdapter

        tab_mediaType.setupWithViewPager(vp_media)
        val videoTab = LayoutInflater.from(this).inflate(R.layout.tab_item_media, null)
        (videoTab.findViewById(R.id.tv_tb_item_title) as TextView).text = "视频"
        val imageTab = LayoutInflater.from(this).inflate(R.layout.tab_item_media, null)
        (imageTab.findViewById(R.id.tv_tb_item_title) as TextView).text = "图片"
        tab_mediaType.getTabAt(0)?.customView = videoTab
        tab_mediaType.getTabAt(1)?.customView = imageTab

        val switchFolderList = {
            if (rv_album_list.visibility == View.GONE) {//to show
                val translateInAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        -1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
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
                        0.0f, Animation.RELATIVE_TO_SELF, -1.0f)
                translateOutAnimation.duration = 300
                rv_album_list.animation = translateOutAnimation

                rv_album_list.visibility = View.GONE
//                rv_album_list.animate().setDuration(300).translationY(-rv_album_list.height.toFloat()).start()
                iv_arrow.animate().setDuration(300).scaleY(1f).start()

            }
        }
        iv_arrow.setOnClickListener { switchFolderList() }
        tv_title.setOnClickListener { switchFolderList() }

        folderListAdapter.setOnFolderCheckedChangeListener(object : FolderListAdapter.OnFolderCheckedChangeListener {
            override fun onFolderCheckedChange(view: View, position: Int) {
                if (rv_album_list.visibility == View.VISIBLE) {
                    switchFolderList()
                }
                changeSelectedFolder(position)
            }
        })

        mediaPageAdapter.setOnMediaCheckedListener(object : MediaPageAdapter.OnMediaCheckedListener {
            override fun onMediaChecked(page: Int, adapter: BaseQuickAdapter<MediaBean, BaseViewHolder>, view: View, checkedPosition: Int) {
                handleMediaChecked(page, adapter, view, checkedPosition)
            }
        })

        tv_ok.setOnClickListener {
//            setResult()
            finish()
        }
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

        imageFolderList.clear()
        videoFolderList.clear()
        folderList.clear()
        mediaFolderMap.forEach { (folderId, mediaFolderArray) ->
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

    /**
     * 处理最多选择9张的图片方法
     *
     * @param adapter
     * @param view1
     * @param position
     */
    private fun handleMediaChecked(page: Int, adapter: BaseQuickAdapter<MediaBean, BaseViewHolder>
                                   , view1: View, position: Int) {
        val mediaBean = adapter.data[position]
        val checkBox = view1.findViewById<CheckBox>(R.id.item_pic_cb)
        val isImageType = mediaBean.type == MediaBean.TYPE_IMAGE

        if (isImageType) {
            if (videoCheckedList.size > 0) {
                //有已选的视频，只能选择图片或者视频
                checkBox.isChecked = false
                return
            }

            if (mediaBean.isItemPicIsChecked) {
                mediaBean.isItemPicIsChecked = false
                picCheckedList.remove(mediaBean.path)

            } else {
                if (picCheckedList.size >= mMaxSelectedCount) {//大于9
                    checkBox.isChecked = false
//                BGToast.showRed(R.string.most_select_pic_conut)
                } else {
                    mediaBean.isItemPicIsChecked = true
                    picCheckedList.add(mediaBean.path)
                }
            }


        } else {
            if (picCheckedList.size > 0) {
                //只能选择图片或视频
                checkBox.isChecked = false
                return
            }

            if (mediaBean.isItemPicIsChecked) {
                mediaBean.isItemPicIsChecked = false
                videoCheckedList.remove(mediaBean.path)
            } else {
                if (videoCheckedList.size >= 1) {
                    checkBox.isChecked = false
                    //todo toast
                } else {
                    mediaBean.isItemPicIsChecked = true
                    videoCheckedList.add(mediaBean.path)
                }
            }
        }

        tv_ok.visibility = if (picCheckedList.size == 0 && videoCheckedList.size == 0) View.GONE else View.VISIBLE

    }
}