package com.testdemo.testPictureSelect.imageLoader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * 媒体库查询任务基类
 * Create by: chenWei.li
 * Date: 2019/1/21
 * Time: 8:35 PM
 * Email: lichenwei.me@foxmail.com
 */
public abstract class AbsMediaScanner<T> {

    /**
     * 查询URI
     *
     * @return
     */
    protected abstract Uri getScanUri();

    /**
     * 查询列名
     *
     * @return
     */
    protected abstract String[] getProjection();

    /**
     * 查询条件
     *
     * @return
     */
    protected abstract String getSelection();

    /**
     * 查询条件值
     *
     * @return
     */
    protected abstract String[] getSelectionArgs();

    /**
     * 查询排序
     *
     * @return
     */
    protected abstract String getOrder();

    /**
     * 对外暴露游标，让开发者灵活构建对象
     *
     * @param cursor
     * @return
     */
    protected abstract T parse(Cursor cursor);

    private Context mContext;
    protected long limit;

    public AbsMediaScanner(Context context) {
        this.mContext = context;
    }

    public AbsMediaScanner(Context context, long limit) {
        this.mContext = context;
        this.limit = limit;
    }

    /**
     * 根据查询条件进行媒体库查询，隐藏查询细节，让开发者更专注业务
     *
     * @return
     */
    public ArrayList<T> queryMedia() {
        ArrayList<T> list = new ArrayList<>();
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(getScanUri(), getProjection(), getSelection(), getSelectionArgs(), getOrder());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                T t = parse(cursor);
                if(t != null) {
                    list.add(t);
                }
                if(Thread.currentThread().isInterrupted()){
                    throw new RuntimeException("中断");
                }
            }
            cursor.close();
        }
        destroy();
        return list;
    }

    public Context getContext() {
        return mContext;
    }

    public void destroy(){}


    //todo greyson 替换成Utils的方法？
    public static boolean isFileAvail(String path){
        boolean ret = false;
        try {
            if(!TextUtils.isEmpty(path)) {
                File file = new File(path);

                if (file.exists() && file.isFile() && file.length() > 0){
                    ret = true;
                }
                if(!ret){
                    Log.e("myLog","file.exists()="+file.exists()+",file.isFile()="+file.isFile()+",file.length()="+file.length());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
