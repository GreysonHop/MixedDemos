package com.testdemo.testPictureSelect.imageLoader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.testdemo.testPictureSelect.imageLoader.thumbnail.RealmThumbHelper;

import java.io.File;

import io.realm.Realm;

/**
 * 媒体库扫描类(图片)
 * Create by: chenWei.li
 * Date: 2018/8/21
 * Time: 上午1:01
 * Email: lichenwei.me@foxmail.com
 */
public class VideoScanner extends AbsMediaScanner<ChatPictureBean> {

    Realm realm;

    public VideoScanner(Context context) {
        super(context);
    }

    public VideoScanner(Context context, long limit) {
        super(context, limit);
    }

    @Override
    protected Uri getScanUri() {
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DURATION
        };
    }

    @Override
    protected String getSelection() {
        return MediaStore.Video.Media.MIME_TYPE + "=? or " + MediaStore.Video.Media.MIME_TYPE + "=? or " + MediaStore.Video.Media.MIME_TYPE + "=?";
    }

    @Override
    protected String[] getSelectionArgs() {
        return new String[]{"video/mp4", "video/mpeg", "video/3gpp"};
    }

    @Override
    protected String getOrder() {
        String order = MediaStore.Video.Media.DATE_TAKEN + " desc";
        if (limit > 0) {
            order += " LIMIT " + limit;
        }
        return order;
    }

    /**
     * 构建媒体对象
     *
     * @param cur
     * @return
     */
    @Override
    protected ChatPictureBean parse(Cursor cur) {
        ChatPictureBean chatPictureBean = null;

        String path = cur.getString(cur.getColumnIndex(MediaStore.Video.Media.DATA));
        if (!isFileAvail(path)) {
            return null;
        }

        long videoId = cur.getLong(cur.getColumnIndex(MediaStore.Video.Media._ID));
        long duration = cur.getLong(cur.getColumnIndex(MediaStore.Video.Media.DURATION));
        Integer folderId = cur.getInt(cur.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
        String folderName = cur.getString(cur.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
        long dateToken = cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
        String mineType = cur.getString(cur.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));

        Log.d("greyson", "mineType=" + mineType + ",path=" + new File(path).getName());

        chatPictureBean = new ChatPictureBean();
        chatPictureBean.setDateToken(dateToken);
        chatPictureBean.setFolderId(folderId);
        chatPictureBean.setFolderName(folderName);
        chatPictureBean.setPath(path);
        chatPictureBean.setDuration(duration / 1000);
        chatPictureBean.setType(ChatPictureBean.TYPE_VIDEO);

//        String thumbPath = null;
//
//        RealmThumbnail realmThumbnail = RealmThumbHelper.getThumbnailByVideoId(getRealm(),videoId);
//        if(realmThumbnail != null ){
//            thumbPath = realmThumbnail.path;
//        }
//
//        if(!FileUtils.isFileAvail(thumbPath)){
//            Bitmap coverBitmap = VideoMsgUtils.getVideoCoverFrame(path);
//            if (coverBitmap != null) {
//                thumbPath = FileUtil.saveBitmapWithPath(MsgFileUtils.getExternalTempPathWithSrcExt("xx.jpg"), coverBitmap);
//                if(!TextUtils.isEmpty(thumbPath)) {
//                    ImageCompress.CompressResult compressResult = ImageCompress.CompressResult.buildFromPath(thumbPath);
//                    RealmThumbnail tmp = new RealmThumbnail();
//                    tmp.videoId = videoId;
//                    tmp.path = thumbPath;
//                    tmp.width = compressResult.width;
//                    tmp.height = compressResult.height;
//                    realmThumbnail = RealmThumbHelper.saveRealmThumbnail(getRealm(), tmp);
//                }
//            }
//        }
//
//        if (FileUtils.isFileAvail(thumbPath)){
//            chatPictureBean = new ChatPictureBean();
//            chatPictureBean.setDateToken(dateToken);
//            chatPictureBean.setFolderId(folderId);
//            chatPictureBean.setFolderName(folderName);
//            chatPictureBean.setPath(path);
//            chatPictureBean.setDuration(duration/1000);
//            chatPictureBean.setWidth(realmThumbnail.width);
//            chatPictureBean.setHeight(realmThumbnail.height);
//            chatPictureBean.setType(ChatPictureBean.TYPE_VIDEO);
//        }

        return chatPictureBean;
    }


    public Realm getRealm() {
        if (realm == null) {
            realm = RealmThumbHelper.getRealm("videoThumb");
        }
        return realm;
    }

    public void closeRealm() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        closeRealm();
    }

}
