package com.testdemo.testAnim.activityAnim

import android.view.View
import android.widget.ImageView
import androidx.annotation.LayoutRes
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
        if (item.type == MediaBean.TYPE_IMAGE) {
            Glide.with(mContext)
                    .load(item.path)
                    .into(helper.getView<View>(R.id.iv_picture) as ImageView)
        } else if (item.type == MediaBean.TYPE_VIDEO) {
            /*Glide.with(mContext)
                    .load(new VideoUrl(item.getPath()))
                    .into((ImageView) helper.getView(R.id.iv_picture));*/
        }
        helper.setChecked(R.id.item_pic_cb, item.isItemPicIsChecked)
        helper.setGone(R.id.group_video_sign, item.type == MediaBean.TYPE_VIDEO)
        //helper.setText(R.id.video_msg_duration, ChatTimeUtils.videoDurationToStr(item.getDuration()));
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}