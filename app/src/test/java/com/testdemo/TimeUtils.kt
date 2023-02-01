package com.testdemo

import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {
    companion object {

        fun getDateStr(time: Long, formatStr: String = ""): String {
            val format = SimpleDateFormat(
                formatStr.ifEmpty { "yyyy-MM-dd HH:mm:ss sss" }
            )
            return format.format(time)
        }

        fun getCurTimeVisual(): String {
            return getDateStr(Date().time)
        }
    }
}