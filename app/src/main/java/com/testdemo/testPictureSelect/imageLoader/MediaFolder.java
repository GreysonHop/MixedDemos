package com.testdemo.testPictureSelect.imageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片文件夹实体类
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午12:56
 * Email: lichenwei.me@foxmail.com
 */
public class MediaFolder {

    private int folderId;
    private String folderName;
    private String folderCover;
    private int coverType;
    private boolean isCheck;
    private List<MediaBean> mediaFileList;

    public MediaFolder(int folderId, String folderName, String folderCover, int coverType, List<MediaBean> mediaFileList) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderCover = folderCover;
        this.mediaFileList = mediaFileList;
        this.coverType = coverType;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderCover() {
        return folderCover;
    }

    public void setFolderCover(String folderCover) {
        this.folderCover = folderCover;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public List<MediaBean> getMediaFileList() {
        return mediaFileList;
    }

    public void setMediaFileList(ArrayList<MediaBean> mediaFileList) {
        this.mediaFileList = mediaFileList;
    }

    public int getCoverType() {
        return coverType;
    }
}
