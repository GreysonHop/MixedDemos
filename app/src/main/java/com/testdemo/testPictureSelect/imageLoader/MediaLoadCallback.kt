package com.testdemo.testPictureSelect.imageLoader

import android.util.SparseArray

/**
 * 图片扫描数据回调接口
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 下午9:55
 * Email: lichenwei.me@foxmail.com
 */
interface MediaLoadCallback {
    fun loadMediaSuccess(mediaFolderMap: Map<Int, Array<MediaFolder?>>, mediaFolderList: SparseArray<MediaFolder>)
}