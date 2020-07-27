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
            if (field == value) return //防止无限循环
            field = value
            notifyPropertyChanged(BR.id)
            notifyPropertyChanged(BR.unique)
        }

    @get:Bindable
    var name = ""
        set(value) {
            if (field == value) return //防止无限循环
            field = value
            notifyPropertyChanged(BR.name)
            notifyPropertyChanged(BR.unique)
        }

    @Bindable
    fun getUnique(): String { //可以不是属性，也可以没有setter方法
        return name + id
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