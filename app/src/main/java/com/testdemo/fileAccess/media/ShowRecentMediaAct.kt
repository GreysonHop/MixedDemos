package com.testdemo.fileAccess.media

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Toast
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.bumptech.glide.Glide
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.testdemo.BaseBindingActivity
import com.testdemo.databinding.ActRecentMediaBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

/**
 * Create by Greyson on 2021/05/12
 */
class ShowRecentMediaAct : BaseBindingActivity<ActRecentMediaBinding>() {
    companion object {
        // private const val ImageLoaderID = 0x110
        private const val MediaLoaderID = 0x111

        private val MEDIA_PROJECTION = arrayOf( //查询图片需要的数据列
            MediaStore.MediaColumns._ID,  // 高版本访问视频文件好像要求使用Uri加ID
            MediaStore.MediaColumns.DISPLAY_NAME,  //图片的显示名称  aaa.jpg
            MediaStore.MediaColumns.DATA,  //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.MediaColumns.SIZE,  //图片的大小，long型  132492
            MediaStore.MediaColumns.WIDTH,  //图片的宽度，int型  1920
            MediaStore.MediaColumns.HEIGHT,  //图片的高度，int型  1080
            MediaStore.MediaColumns.MIME_TYPE,  //图片的类型     image/jpeg
            MediaStore.MediaColumns.DATE_ADDED,  //图片被添加的时间，long型  1450518608
            MediaStore.MediaColumns.DATE_TAKEN  //图片被添加的时间，long型  1450518608
        )

        // 试着缓存列索引
        private val DATA_BASE_COLUMN = arrayOfNulls<Int>(MEDIA_PROJECTION.size)
    }

    override fun getViewBinding(): ActRecentMediaBinding {
        return ActRecentMediaBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.btnShowPic.setOnClickListener {
            searchMedia { list ->
                if (list.isNotEmpty()) {
                    val media = list[0]
                    // media.pictureType

                    binding.ivRecentVideoTip.visibility =
                            if (media.pictureType.startsWith(PictureConfig.VIDEO)) View.VISIBLE else View.INVISIBLE

                    Glide.with(this).load(media.path)
                        .into(binding.ivRecentMedia)

                    binding.clRecentMedia.apply {
                        visibility = View.VISIBLE

                        val min = ViewConfiguration.get(this@ShowRecentMediaAct).scaledTouchSlop
                        var downX = 0f
                        var downY = 0f
                        var moving = false
                        setOnTouchListener { _, event ->
                            val totalXOffset = event.rawX - downX
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    downX = event.rawX
                                    downY = event.rawY
                                    // return@setOnTouchListener false
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    if (moving) {
                                        translationX = 0f.coerceAtLeast(totalXOffset)

                                    } else if (totalXOffset > min
                                        && abs(event.rawY - downY) < min) {
                                        moving = true
                                        translationX = 0f.coerceAtLeast(totalXOffset)
                                    }
                                }

                                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                    if (moving) {
                                        if (totalXOffset > measuredWidth / 2) {
                                            animate().translationX(measuredWidth.toFloat()).setDuration(100)
                                                .setListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator?) {
                                                        visibility = View.GONE
                                                        translationX = 0f
                                                    }
                                                }).start()

                                        } else {
                                            animate().translationX(0f).setDuration(150).setListener(null).start()
                                        }

                                    } else if (abs(totalXOffset) < min && abs(event.rawY - downY) < min) {
                                        performClick()
                                        translationX = 0f
                                    }
                                }
                            }

                            true
                        }

                        setOnClickListener {
                            Toast.makeText(this@ShowRecentMediaAct, "click!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.clRecentMedia.visibility = View.GONE
                }
            }
            /*LoaderManager.getInstance(this).apply {
                if (getLoader<Cursor>(MediaLoaderID) == null) {
                    initLoader(MediaLoaderID, null, loaderCallback)
                } else {
                    restartLoader(MediaLoaderID, null, loaderCallback)
                    // getLoader<CursorLoader>(ImageLoaderID)?.reset()
                }
            }*/
        }
    }

    private fun searchMedia(callback: (List<LocalMedia>) -> Unit) {
        val timeLimit = System.currentTimeMillis() - 172 * 60 * 60 * 1000 // 一分钟前
        // query() 应该在工作线程中被调用
        contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            MEDIA_PROJECTION,
            /*null*/"${MediaStore.MediaColumns.DATE_TAKEN} > $timeLimit",
            null,
            MEDIA_PROJECTION[7] + " DESC"
        )?.use { cursor ->
            cacheColumn(cursor)
            val allMedias: ArrayList<LocalMedia> = ArrayList()
            while (cursor.moveToNext() && allMedias.size < 9) {
                allMedias.add(convert(cursor))
            }
            callback.invoke(allMedias)
            cursor.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LoaderManager.getInstance(this).destroyLoader(MediaLoaderID)
    }

    private fun cacheColumn(cursor: Cursor) {
        if (DATA_BASE_COLUMN[0] != null) return // 缓存过

        for (i in MEDIA_PROJECTION.indices) {
            DATA_BASE_COLUMN[i] = cursor.getColumnIndexOrThrow(MEDIA_PROJECTION[i])
        }
    }

    private fun convert(data: Cursor): LocalMedia {
        val id = data.getLong(DATA_BASE_COLUMN[0] ?: -1)
        val mediaName: String = data.getString(DATA_BASE_COLUMN[1] ?: -1)
        val mediaPath: String = data.getString(DATA_BASE_COLUMN[2] ?: -1)
        val mediaSize: Long = data.getLong(DATA_BASE_COLUMN[3] ?: -1)
        val mediaWidth: Int = data.getInt(DATA_BASE_COLUMN[4] ?: -1)
        val mediaHeight: Int = data.getInt(DATA_BASE_COLUMN[5] ?: -1)
        val mediaMimeType: String = data.getString(DATA_BASE_COLUMN[6] ?: -1)
        val mediaAddTime: Long = data.getLong(DATA_BASE_COLUMN[7] ?: -1)
        val mediaTakenTime: Long = data.getLong(DATA_BASE_COLUMN[8] ?: -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

        val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id
        ) // 官方对Uri的要求！

        Log.d("greyson", "onLoadFinished 遍历图片列表: \n\t$mediaName, takeTime=${dateFormat.format(mediaTakenTime)}" +
                ", \n\taddTime=${dateFormat.format(mediaAddTime)} _ ${mediaAddTime / 1000}"
                + ", \n\tsize=$mediaSize, $mediaMimeType")

        val mediaItem = LocalMedia()
        // mediaItem.setFolderName(mediaName)
        mediaItem.path = mediaPath
        // mediaItem. = mediaSize
        mediaItem.width = mediaWidth
        mediaItem.height = mediaHeight
        mediaItem.pictureType = mediaMimeType
        // mediaItem.mimeType = mediaMimeType
        return mediaItem
    }

    private val loaderCallback = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            //TODO greyson_2021/5/16 为了测试方便，先把时间调成72小时内
            val timeLimit = System.currentTimeMillis() - 272 * 60 * 60 * 1000 // 一分钟前

            Log.d("greyson", "onCreateLoader: id=$id")
            val cursorLoader: CursorLoader?
            cursorLoader = CursorLoader(
                this@ShowRecentMediaAct,
                MediaStore.Files.getContentUri("external"),
                MEDIA_PROJECTION,
                /*null*/"${MediaStore.MediaColumns.DATE_TAKEN} > $timeLimit",
                null,
                MEDIA_PROJECTION[7] + " DESC"
            )
            return cursorLoader
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            if (data == null) return

            val allMedias: ArrayList<LocalMedia> = ArrayList() // 所有图片的集合,不分文件夹
            while (data.moveToNext()) {
                allMedias.add(convert(data))
            }
            Log.d("greyson", "onLoadFinished 最终的结果列表: " + allMedias.size)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            // Loader被reset时调用，它所读取的数据要取消引用
            Log.d("greyson", "onLoaderReset: ${loader.id}")
        }
    }
}