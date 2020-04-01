package com.testdemo.testPictureSelect.imageLoader.thumbnail;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class ThumbnailMigration implements RealmMigration {
    public static final String TAG = "ThumbnailMigration";

    public static final int Version = 2;

    @Override
    public void migrate(DynamicRealm dynamicRealm, long oldVersion, long newVersion) {
        RealmSchema schema = dynamicRealm.getSchema();

    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return TAG.hashCode();
    }
}
