package com.testdemo.testAnim.activityAnim

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.testdemo.R
import com.testdemo.testPictureSelect.imageLoader.MediaBean
import com.testdemo.testPictureSelect.imageLoader.MediaFolder

/**
 * Create by Greyson
 */
class FolderListAdapter : BaseQuickAdapter<MediaFolder, BaseViewHolder> {

    private var currentCheckedIndex = 0

    constructor() : super(R.layout.item_folder_list, null)

    override fun convert(helper: BaseViewHolder, item: MediaFolder?) {
        if (item == null) {
            return
        }
        val position = helper.adapterPosition

        Glide.with(mContext)
                .load(item.folderCover)
                .into(helper.getView<ImageView>(R.id.iv_item_imageCover))
        helper.setText(R.id.tv_item_folderName, item.folderName)
        helper.setText(R.id.tv_item_imageSize, String.format("%d张", item.mediaFileList.size))
        helper.setGone(R.id.iv_item_check, currentCheckedIndex == position)

        when (item.coverType) {
            MediaBean.TYPE_IMAGE -> {

            }
            else -> {
            }
        }

        helper.itemView.setOnClickListener {
            if (currentCheckedIndex != position) {
                val oldChecked = currentCheckedIndex
                currentCheckedIndex = position
                notifyItemChanged(position)
                notifyItemChanged(oldChecked)
                onFolderCheckedChangeListener?.onFolderCheckedChange(it, position)
            }
        }
    }

    private var onFolderCheckedChangeListener: OnFolderCheckedChangeListener? = null
    fun setOnFolderCheckedChangeListener(listener: OnFolderCheckedChangeListener) {
        this.onFolderCheckedChangeListener = listener
    }

    interface OnFolderCheckedChangeListener {
        fun onFolderCheckedChange(view: View, position: Int)
    }
}