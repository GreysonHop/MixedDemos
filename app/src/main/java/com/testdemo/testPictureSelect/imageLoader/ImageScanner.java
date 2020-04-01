package com.testdemo.testPictureSelect.imageLoader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 媒体库扫描类(图片)
 * Create by: chenWei.li
 * Date: 2018/8/21
 * Time: 上午1:01
 * Email: lichenwei.me@foxmail.com
 */
public class ImageScanner extends AbsMediaScanner<ChatPictureBean> {

    public ImageScanner(Context context) {
        super(context);
    }

    public ImageScanner(Context context, long limit) {
        super(context, limit);
    }


    @Override
    protected Uri getScanUri() {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };
    }

    @Override
    protected String getSelection() {
        return MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
    }

    @Override
    protected String[] getSelectionArgs() {
        return new String[]{"image/jpeg", "image/png"};
    }

    @Override
    protected String getOrder() {
        String order = MediaStore.Images.Media.DATE_TAKEN + " desc";
        if (limit > 0) {
            order += " LIMIT " + limit;
        }
        return order;
    }

    /**
     * 构建媒体对象
     *
     * @param cursor
     * @return
     */
    @Override
    protected ChatPictureBean parse(Cursor cursor) {

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        if (!isFileAvail(path)) {
            return null;
        }

        String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        Integer folderId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        String folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        long dateToken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

        ChatPictureBean chatPictrueBean = new ChatPictureBean();
        chatPictrueBean.setPath(path);
        chatPictrueBean.setMime(mime);
        chatPictrueBean.setFolderId(folderId);
        chatPictrueBean.setFolderName(folderName);
        chatPictrueBean.setDateToken(dateToken);
        chatPictrueBean.setType(ChatPictureBean.TYPE_IMAGE);

        return chatPictrueBean;
    }


}
