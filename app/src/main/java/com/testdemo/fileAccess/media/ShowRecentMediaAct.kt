package com.testdemo.fileAccess.media

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.luck.picture.lib.entity.LocalMedia
import com.testdemo.BaseBindingActivity
import com.testdemo.databinding.ActRecentMediaBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Create by Greyson on 2021/05/12
 */
class ShowRecentMediaAct : BaseBindingActivity<ActRecentMediaBinding>() {
    companion object {
        private const val ImageLoaderID = 0x110
        private const val VideoLoaderID = 0x111

        private val IMAGE_PROJECTION = arrayOf<String>( //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,  //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,  //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,  //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,  //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,  //图片的高度，int型  1080
            MediaStore.Images.Media.MIME_TYPE,  //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED,  //图片被添加的时间，long型  1450518608
            MediaStore.Images.ImageColumns.DATE_TAKEN  //图片被添加的时间，long型  1450518608
        )
    }

    override fun getViewBinding(): ActRecentMediaBinding {
        return ActRecentMediaBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.btnShowPic.setOnClickListener {
            LoaderManager.getInstance(this).apply {
                if (getLoader<Cursor>(ImageLoaderID) == null) {
                    initLoader(ImageLoaderID, null, loaderCallback)
                } else {
                    restartLoader(ImageLoaderID, null, loaderCallback)
                    // getLoader<CursorLoader>(ImageLoaderID)?.reset()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LoaderManager.getInstance(this).destroyLoader(ImageLoaderID)
    }

    private val loaderCallback = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            //TODO greyson_2021/5/16 为了测试方便，先把时间调成72小时内
            val timeLimit = System.currentTimeMillis() - 72 * 60 * 60 * 1000 // 一分钟前

            Log.d("greyson", "onCreateLoader: id=$id")
            val cursorLoader: CursorLoader?
            cursorLoader = CursorLoader(
                this@ShowRecentMediaAct,
                if (id == ImageLoaderID)
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                else
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                IMAGE_PROJECTION,
                /*null*/"${MediaStore.Images.ImageColumns.DATE_TAKEN} > $timeLimit",
                null,
                IMAGE_PROJECTION[7] + " DESC"
            )
            return cursorLoader
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            if (data == null) return

            val allImages: ArrayList<LocalMedia> = ArrayList() // 所有图片的集合,不分文件夹
            val currentTime: Long = System.currentTimeMillis()

            while (data.moveToNext()) {

                val imageName: String = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                val imagePath: String = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))
                val imageSize: Long = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]))
                val imageWidth: Int = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]))
                val imageHeight: Int = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]))
                val imageMimeType: String = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]))
                val imageAddTime: Long = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]))
                val imageTakenTime: Long = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7]))
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

                Log.d("greyson", "onLoadFinished 遍历图片列表: \n\t$imageName, takeTime=${dateFormat.format(imageTakenTime)}" +
                        ", \n\taddTime=${dateFormat.format(imageAddTime)} _ ${imageAddTime / 1000}"
                        + ", \n\tsize=$imageSize, $imageMimeType")


                // if (currentTime - imageTakenTime < 60 * 1000) {

                val imageItem = LocalMedia()
                // imageItem.setFolderName(imageName)
                imageItem.setPath(imagePath)
                // imageItem.si = imageSize
                imageItem.setWidth(imageWidth)
                imageItem.setHeight(imageHeight)
                // imageItem.setMime(imageMimeType)
                // imageItem.setAddTime(imageAddTime)
                allImages.add(imageItem)
                // }
            }
            Log.d("greyson", "onLoadFinished 最终的结果列表: " + allImages.size)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            // Loader被reset时调用，它所读取的数据要取消引用
            Log.d("greyson", "onLoaderReset: ${loader.id}")
        }
    }
}