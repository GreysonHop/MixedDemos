package com.testdemo.testDatePicker.datepicker.bizs.languages;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 语言对象抽象父类
 * DatePicker暂且支持中文和英文两种显示语言
 * 如果你需要定义更多的语言可以新建自己的语言类并继承Language重写其方法即可
 * 同时你需要在Language的单例方法{@link #getInstance()}的分支语句中添加自己的语言类判断
 * <p>
 * The current language only two support chinese and english in DatePicker.
 * If you need more language you want,you can define your own language class and extends Language
 * override all method.
 * Also you must add a judge of your language in branching statement of single case method{@link #getInstance()}
 *
 * @author AigeStudio 2015-03-26
 */
public class DPLManager implements ILanguage {
    private static DPLManager mManager;
    private SimpleDateFormat mSimpleDateFormat;
    protected Locale mLocale;
    private ILanguage mLanguage;
    private boolean isLocaleLocked;//是否锁定语言，不根据系统改变

    private DPLManager(boolean isLocaleLocked, Locale locale) {
        this.isLocaleLocked = isLocaleLocked;
        this.mLocale = locale;
        String language = locale.getLanguage().toLowerCase();
        if (language.equals("zh")) {
            mLanguage = new CN();
        } else {
            mLanguage = new EN();
        }
    }

    /**
     * 获取日历语言管理器
     * <p>
     * Get DatePicker language manager
     *
     * @return 日历语言管理器 DatePicker language manager
     */
    public static DPLManager getInstance() {
        if (null == mManager) {
            getInstance(false, null);
        }
        return mManager;
    }

    /**
     * 如果已经存在Manager对象，则直接返回；如果没有则按照参数生成新的Manager。
     * 如果需要指定Locale而不跟随系统变化可用{@link #setLocaleLocked(boolean)}配合{@link #setLocale(Locale)}
     * @param localeLocked
     * @param locale
     * @return
     */
    public static DPLManager getInstance(boolean localeLocked, Locale locale) {
        if (null == mManager) {
            if (localeLocked) {
                if (locale == null) {
                    throw new InvalidParameterException("\'locale\' can not be null when \'localeLocked\' is true");
                } else {
                    mManager = new DPLManager(true, locale);
                }
            } else {
                mManager = new DPLManager(false, Locale.getDefault());
            }
        }
        return mManager;
    }

    @NotNull
    @Override
    public String[] titleMonth() {
        return mLanguage.titleMonth();
    }

    @NotNull
    @Override
    public String titleEnsure() {
        return mLanguage.titleEnsure();
    }

    @NotNull
    @Override
    public String titleBC() {
        return mLanguage.titleBC();
    }

    @NotNull
    @Override
    public String[] titleWeek() {
        return mLanguage.titleWeek();
    }

    @NotNull
    @Override
    public String getDateFormatStr() {
        return mLanguage.getDateFormatStr();
    }

    /**
     * 获取日历的显示格式，如“2017-09-01”，“Jul 2, 2017”
     *
     * @return
     */
    public DateFormat getDateFormat() {
        if (mSimpleDateFormat == null) {
            mSimpleDateFormat = new SimpleDateFormat(getDateFormatStr(), mLocale);
        }
        return mSimpleDateFormat;
    }

    public boolean isSameLanguage(Locale locale) {
        return TextUtils.equals(locale.getDisplayLanguage(), mLocale.getDisplayLanguage());
    }

    public boolean isLocaleLocked() {
        return isLocaleLocked;
    }

    public void setLocaleLocked(boolean locked) {
        isLocaleLocked = locked;
    }

    /**
     * 设置新的Locale
     * @param locale
     * @return 跟原来的Locale是否一样，是则更新Manager中的语言相关内容并返回true；否则为false
     */
    public boolean setLocale(Locale locale) {
        if (isSameLanguage(locale)) {
            return false;
        }

        String language = locale.getLanguage().toLowerCase();
        if (language.equals("zh")) {
            mLanguage = new CN();
        } else {
            mLanguage = new EN();
        }

        mLocale = locale;
        mSimpleDateFormat = new SimpleDateFormat(getDateFormatStr(), locale);

        return true;
    }

    /**
     * 检查Locale是否需要更新，是则更新Manager对象并返回true，否则不更新并且返回false
     * @return 是否更新
     */
    public boolean checkLocale() {
        if (!isLocaleLocked) {
            return setLocale(Locale.getDefault());
        }
        return false;
    }
}
