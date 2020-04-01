package com.testdemo.testPictureSelect.imageLoader.thumbnail;

import io.realm.RealmObject;

public class RealmThumbnail extends RealmObject {
    public static final String  VIDEO_TOKEN = "videoToken";

    public String videoToken;

    public String path;

    public int width;

    public int height;

}
