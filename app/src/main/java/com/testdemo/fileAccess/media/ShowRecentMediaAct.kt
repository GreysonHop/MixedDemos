package com.testdemo.fileAccess.media

import android.R
import android.database.Cursor
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.luck.picture.lib.entity.LocalMedia
import com.testdemo.BaseBindingActivity
import com.testdemo.databinding.ActRecentMediaBinding


/**
 * Create by Greyson on 2021/05/12
 */
class ShowRecentMediaAct : BaseBindingActivity<ActRecentMediaBinding>() {
    private val IMAGE_PROJECTION = arrayOf<String>( //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,  //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,  //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,  //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,  //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,  //图片的高度，int型  1080
            MediaStore.Images.Media.MIME_TYPE,  //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED  //图片被添加的时间，long型  1450518608
    )
    override fun getViewBinding(): ActRecentMediaBinding {
        return ActRecentMediaBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.btnShowPic.setOnClickListener {
            LoaderManager.getInstance(this).initLoader(0, null, object : LoaderManager.LoaderCallbacks<Cursor> {
                override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
                    var cursorLoader: CursorLoader? = null
                    //扫描所有图片
                    //扫描所有图片
                    if (id == 0) //时间逆序
                        cursorLoader = CursorLoader(this@ShowRecentMediaAct, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[6] + " DESC")
                    return cursorLoader as Loader<Cursor>
                }

                override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                    if (data == null) return

                    val allImages: ArrayList<LocalMedia> = ArrayList() //所有图片的集合,不分文件夹

                    val currentTime: Long = SystemClock.currentThreadTimeMillis()

                    while (data.moveToNext()) {
                        val imageAddTime: Long = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]))
                        Log.d("TAG", "onLoadFinished 遍历图片列表: " + (currentTime - imageAddTime) / 1000)
                        if (currentTime - imageAddTime < 60 * 1000) {
                            //查询数据
                            val imageName: String = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                            val imagePath: String = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))
                            val imageSize: Long = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]))
                            val imageWidth: Int = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]))
                            val imageHeight: Int = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]))
                            val imageMimeType: String = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]))

                            val imageItem = LocalMedia()
//                            imageItem.setFolderName(imageName)
                            imageItem.setPath(imagePath)
                            // imageItem.si = imageSize
                            imageItem.setWidth(imageWidth)
                            imageItem.setHeight(imageHeight)
//                            imageItem.setMime(imageMimeType)
//                            imageItem.setAddTime(imageAddTime)
                            allImages.add(imageItem)
                        }
                    }
                    //回调接口，通知图片数据准备完成
                    //  ImagePicker.getInstance().setImageFolders(imageFolders)
                    // loadedListener.onImagesLoaded(imageItem)
                    //回调接口，通知图片数据准备完成
                    //  ImagePicker.getInstance().setImageFolders(imageFolders)
                    // loadedListener.onImagesLoaded(imageItem)
                    Log.d("TAG", "onLoadFinished 最终的结果列表: " + allImages.size)
                }

                override fun onLoaderReset(loader: Loader<Cursor>) {
                }
            })
        }
    }
}