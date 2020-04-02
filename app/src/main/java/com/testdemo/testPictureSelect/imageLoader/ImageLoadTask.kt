package com.testdemo.testPictureSelect.imageLoader

import android.content.Context
import android.util.SparseArray
import java.util.*

/**
 * 媒体库扫描任务（图片）
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 下午12:31
 * Email: lichenwei.me@foxmail.com
 */
class ImageLoadTask(private val context: Context, private val mediaLoadCallback: MediaLoadCallback?) : Runnable {
    private val mImageScanner: ImageScanner?
    private val mVideoScanner: VideoScanner?

    init {
        mImageScanner = ImageScanner(context)
        mVideoScanner = VideoScanner(context)
    }

    override fun run() {
        try { //存放所有照片
            val imageFileList = ArrayList<MediaBean>()
            if (mImageScanner != null) {
                imageFileList.addAll(mImageScanner.queryMedia())
            }
            if (mVideoScanner != null) {
                imageFileList.addAll(mVideoScanner.queryMedia())
            }
            if (mediaLoadCallback != null) {
                MediaHandler.getMediaFolderMap(context, mImageScanner?.queryMedia(), mVideoScanner?.queryMedia()
                ) { mediaFolderMap, mediaFolderList ->
                    mediaLoadCallback.loadMediaSuccess(mediaFolderMap, mediaFolderList)
                }

                synchronized(ImageLoadTask::class.java) {
                    mediaFolderList.clear()
//                    mediaFolderList.addAll(list)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val mediaFolderList: MutableList<MediaFolder> = ArrayList()
        fun getMediaFolderById(folderId: Int): MediaFolder? {
            var mediaFolder: MediaFolder? = null
            synchronized(ImageLoadTask::class.java) {
                for (folder in mediaFolderList) {
                    if (folder.folderId == folderId) {
                        mediaFolder = folder
                    }
                }
            }
            return mediaFolder
        }

        fun clearMediaFolder() {
            synchronized(ImageLoadTask::class.java) { mediaFolderList.clear() }
        }

        val allSelectPic: List<MediaBean>
            get() {
                val list: MutableList<MediaBean> = ArrayList()
                synchronized(ImageLoadTask::class.java) {
                    val folder = getMediaFolderById(MediaHandler.ALL_MEDIA_FOLDER)
                    if (folder != null) {
                        for (pictureBean in folder.mediaFileList) {
                            if (pictureBean.isItemPicIsChecked) {
                                list.add(pictureBean)
                            }
                        }
                    }
                }
                return list
            }
    }

}