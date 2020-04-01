package com.testdemo.testPictureSelect.imageLoader.thumbnail;

import android.content.Context;
import android.util.Log;

public class RealmKey {
    private static RealmKey reamlKey;

    private Context context;

    private RealmKey(Context c){
        this.context = c;
    }

    public static void init(Context c){
        reamlKey = new RealmKey(c);
    }

    public static RealmKey getInstance(){
        return reamlKey;
    }

    public byte[] getKeyByAid(String aid){
        String key = "realm_key" + aid;//todo greyson 如何写

        if (key.length() < 64){
            while (key.length()<64){
                key += "0";
            }
        }else{
            key = key.substring(0,64);
        }

        Log.d("RealmKey","getKeyByAid key="+key);

        return key.getBytes();
    }
}