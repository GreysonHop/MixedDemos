package com.testdemo.testAnim.activityAnim

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.testdemo.R
import com.testdemo.testPictureSelect.imageLoader.MediaBean

class GridMediaSelectAdapter : BaseQuickAdapter<MediaBean, BaseViewHolder> {
    constructor() : super(R.layout.item_grid_media, null)
    constructor(@LayoutRes layoutRes: Int) : super(layoutRes, null)

    override fun convert(helper: BaseViewHolder, item: MediaBean) {
        helper.addOnClickListener(R.id.item_pic_cb)

        val iv = helper.getView<ImageView>(R.id.iv_picture)
        iv.setColorFilter(
            Color.parseColor("#33000000"),
            android.graphics.PorterDuff.Mode.SRC_ATOP
        )
        Glide.with(mContext)
                .load(item.path)
                .into(iv)

        helper.setChecked(R.id.item_pic_cb, item.isItemPicIsChecked)
        helper.setGone(R.id.group_video_sign, item.type == MediaBean.TYPE_VIDEO)
        //helper.setText(R.id.video_msg_duration, ChatTimeUtils.videoDurationToStr(item.getDuration()));
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}