package com.testdemo.testPictureSelect.imageLoader.thumbnail;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmThumbHelper {

    public static Realm getRealm(String aid) {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(aid + ".thumbnail.realm")
                .modules(new ThumbnailModule())
                .encryptionKey(RealmKey.getInstance().getKeyByAid(aid))
                .schemaVersion(ThumbnailMigration.Version)
                .migration(new ThumbnailMigration())
                .deleteRealmIfMigrationNeeded()
                .build();

        //Realm.deleteRealm(config);

        Realm realm = Realm.getInstance(config);

        return realm;
    }

    public static RealmThumbnail getThumbnailByVideoId(Realm realm, String videoToken) {

        RealmThumbnail realmThumbnail = realm.where(RealmThumbnail.class).equalTo(RealmThumbnail.VIDEO_TOKEN, videoToken).findFirst();

        if (realmThumbnail != null) {
            realmThumbnail = realm.copyFromRealm(realmThumbnail);
        }

        return realmThumbnail;
    }

    public static RealmThumbnail saveRealmThumbnail(Realm realm, RealmThumbnail realmThumbnail) {
        RealmThumbnail r = getThumbnailByVideoId(realm, realmThumbnail.videoToken);

        realm.beginTransaction();
        if (r == null) {
            r = realm.copyToRealm(realmThumbnail);
        } else {
            r.path = realmThumbnail.path;
            r.width = realmThumbnail.width;
            r.height = realmThumbnail.height;
        }
        realm.commitTransaction();

        return realm.copyFromRealm(r);
    }

}
