package com.testdemo.testDatePicker.datepicker.bizs.languages

/**
 * Create by Greyson on 2020/02/18
 */
interface ILanguage {


    /**
     * 月份标题显示
     * Titles of month
     *
     * @return 长度为12的月份标题数组 Array in 12 length of month titles
     */
    fun titleMonth(): Array<String>

    /**
     * 确定按钮文本
     * Text of ensure button
     *
     * @return Text of ensure button
     */
    fun titleEnsure(): String

    /**
     * 公元前文本
     * Text of B.C.
     *
     * @return Text of B.C.
     */
    fun titleBC(): String

    /**
     * 星期标题显示
     * Titles of week
     *
     * @return 长度为7的星期标题数组 Array in 7 length of week titles
     */
    fun titleWeek(): Array<String>

    /**
     * 获取日历的显示格式，如“2017-09-01”，“Jul 2, 2017”
     *
     * @return
     */
    fun getDateFormatStr(): String
}