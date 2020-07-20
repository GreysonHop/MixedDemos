package com.testdemo.architecture

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

/**
 * Created by Greyson on 2020/06/30
 */
class UserBean : BaseObservable, Parcelable {

    @get:Bindable
    var id = -1
        set(value) {
            field = value
            notifyPropertyChanged(BR.id)
        }

    @get:Bindable
    var name = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }

    constructor(source: Parcel) {
        id = source.readInt()
        name = source.readString() ?: ""
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserBean> = object : Parcelable.Creator<UserBean> {
            override fun createFromParcel(source: Parcel): UserBean = UserBean(source)
            override fun newArray(size: Int): Array<UserBean?> = arrayOfNulls(size)
        }
    }
}