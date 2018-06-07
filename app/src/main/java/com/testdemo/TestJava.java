package com.testdemo;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/2/1.
 */
public class TestJava {

    public static void main(String[] args) {
        System.out.println("hello java!\\!!" + Math.pow(2, 3));

        String cardNo = "12098915703";
        System.out.println(cardNo + " - " + isPhoneNumber(cardNo));

        ArrayList<String> a = new ArrayList<>();
        a.add("ea");
        a.add("ea");
        System.out.println("a = " + a + " - " + a.subList(0, 4));
    }

    public static boolean isPhoneNumber(String str) {
        Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static String getStrFromNumber(int number) {
        String numberStr;
        if (number < 9999) {
            return String.valueOf(number);
        }
        return null;
    }
}
