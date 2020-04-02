package com.testdemo.testPictureSelect.imageLoader;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class MediaBean implements Parcelable {
    public static final int TYPE_IMAGE = 1000;
    public static final int TYPE_VIDEO = 1001;

    public boolean itemPicIsChecked;

    private String path;
    private String mime;
    private Integer folderId;
    private String folderName;
    private long duration;
    private long dateToken;
    private int type;
    private long addTime;
    private int width;
    private int height;
    private long selectTime;

    public MediaBean(){

    }

    protected MediaBean(Parcel in) {
        itemPicIsChecked = in.readByte() != 0;
        path = in.readString();
        mime = in.readString();
        if (in.readByte() == 0) {
            folderId = null;
        } else {
            folderId = in.readInt();
        }
        folderName = in.readString();
        duration = in.readLong();
        dateToken = in.readLong();
        type = in.readInt();
        addTime = in.readLong();
        width = in.readInt();
        height = in.readInt();
        selectTime = in.readLong();
    }

    public static final Creator<MediaBean> CREATOR = new Creator<MediaBean>() {
        @Override
        public MediaBean createFromParcel(Parcel in) {
            return new MediaBean(in);
        }

        @Override
        public MediaBean[] newArray(int size) {
            return new MediaBean[size];
        }
    };

    public boolean isItemPicIsChecked() {
        return itemPicIsChecked;
    }

    public void setItemPicIsChecked(boolean itemPicIsChecked) {
        if(!this.itemPicIsChecked && itemPicIsChecked){
            selectTime = System.currentTimeMillis();
        }

        this.itemPicIsChecked = itemPicIsChecked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDateToken() {
        return dateToken;
    }

    public void setDateToken(long dateToken) {
        this.dateToken = dateToken;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getSelectTime() {
        return selectTime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (itemPicIsChecked ? 1 : 0));
        dest.writeString(path);
        dest.writeString(mime);
        if (folderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(folderId);
        }
        dest.writeString(folderName);
        dest.writeLong(duration);
        dest.writeLong(dateToken);
        dest.writeInt(type);
        dest.writeLong(addTime);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeLong(selectTime);
    }

    public static Comparator<MediaBean> getComparator(){
        Comparator t = new Comparator<MediaBean>() {
            @Override
            public int compare(MediaBean o1, MediaBean o2) {
                int ret = 0;
                long del = o1.getSelectTime() - o2.getSelectTime();
                if(del > 0){
                    ret = 1;
                }else if(del < 0){
                    ret = -1;
                }
                return ret;
            }
        };
        return t;
    }
}
