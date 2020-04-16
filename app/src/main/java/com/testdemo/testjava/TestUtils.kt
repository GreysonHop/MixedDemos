@file:JvmName("TestUtils")

package com.testdemo.testjava

import java.util.*

/**
 * Create by Greyson
 */
fun printMy() {
    println("my name")
}


fun getDayBetweenDates(start: Calendar, end: Calendar): Int {
    val startDayOfYear = start.get(Calendar.DAY_OF_YEAR)
    var endDayOfYear = end.get(Calendar.DAY_OF_YEAR)
    val year1 = start.get(Calendar.YEAR)
    val year2 = end.get(Calendar.YEAR)
    if (year1 < year2) {
        val yearGap = year2 - year1
        for (i in 1..yearGap) {
            start.add(Calendar.YEAR, i - 1)
            endDayOfYear += start.getActualMaximum(Calendar.DAY_OF_YEAR)
            println("greyson: endDay=$endDayOfYear")
        }
    } else if (year1 > year2) {

    }
    return endDayOfYear - startDayOfYear
}