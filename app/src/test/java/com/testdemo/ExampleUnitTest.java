package com.testdemo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.testdemo.webrtc.webrtclib.bean.MyIceServer;

import org.junit.Test;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    private TestClass testClass = new TestClass();

    class TestClass {
        private void printSomeThing(String author) {
            System.out.println(author + " invoke printSomeThing. " + getClass().getName() + " thread = " + Thread.currentThread());
        }
    }

    @Test
    public void testGson() {
        HashMap<String, String> map = new HashMap<>();
        map.put("time", "1287349827");
        map.put("text", "hello");
        final String json = new Gson().toJson(map);
        System.out.println("json: \n" + json);
        HashMap<String, String> map1 = new Gson().fromJson(json, HashMap.class);
        HashMap<String, String> map2 = new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {}.getType());
        System.out.println("map2:" + map1.get("text"));


        ArrayList<Float> list = new ArrayList<>();
        list.add(10.1f);
        list.add(12.9f);
        list.add(22.9f);
        list.add(24.9f);
        // String jsonForList = new Gson().toJson(list);
        String jsonForList =/*putForList(list);*/new Gson().toJson(list);

        try {
            List<Float> list1 = new Gson().fromJson(jsonForList, new TypeToken<List<Float>>() {}.getType());
            // List<Float> list1 = getForList(jsonForList, Float.class);
            for (Float s : list1) {
                System.out.println("解析： " + s);
            }
        } catch (Exception e) {e.printStackTrace();}

        List<MyIceServer> ipList = new ArrayList<>();
        ipList.add(new MyIceServer("192.3.1.10"));
        ipList.add(new MyIceServer("184.4.13.9"));
        final String ipListStr = new Gson().toJson(ipList);

        List<MyIceServer> ipList1 = getForList(ipListStr, MyIceServer.class);
        System.out.println("ipList： " + ipListStr); // 没法直接解析成自定义类，只能像上面的 String 一样的才行！
        for (MyIceServer myIceServer : ipList1) {
            System.out.println("--: " + myIceServer.uri);
        }
    }

    private <K, V> HashMap<K, V> getForMap(String jsonStr, Class<K> clazzK, Class<V> clazzV) {
        return new Gson().fromJson(jsonStr, new TypeToken<HashMap<K, V>>() {}.getType());
    }

    public <T> String putForList(List<T> value) {
        Type listType = new TypeToken<List<T>>() {}.getType();
        String data = new Gson().toJson(value, value.getClass());
        return data;
    }

    private <T> List<T> getForList(String jsonForList, Class<T> clazz) {
        try {
            new Gson().fromJson(jsonForList, clazz);
            List<T> ret = new Gson().fromJson(jsonForList, new TypeToken<List<T>>() {}.getType());
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        System.out.println((2 << 30) + " _ " + (1 << 30) + " _ " + (0x3 << 30) + " _ " + 0x3 + " _ "
                + Integer.MAX_VALUE + "_ " + Integer.MIN_VALUE);
    }

    @Test
    public void testDate() throws Exception {
        Calendar cal = Calendar.getInstance();
        System.out.println("greyson: " + cal.getTimeInMillis());
        cal.set(Calendar.SECOND, 0);
        System.out.println("greyson: " + cal.getTimeInMillis());
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println("greyson: " + cal.getTimeInMillis());

        Date date = new SimpleDateFormat("a hh:mm", Locale.CHINESE).parse("下午 00:05");
        SimpleDateFormat dateFormat = new SimpleDateFormat("'Today': EEEE, yyyy MMM d, hh:m,a", Locale.CHINA);
        System.out.println("date = " + dateFormat.format(date));
        System.out.println("date = " + dateFormat.format(new Date(1586025874L)));
    }

    @Test
    public void testRegex() {
        String regex = "^((0[1-9])|(1[0-9])|(2[0-4])):((00)|(15)|(30)|(45))$";
        System.out.println("19:05".matches(regex));
        System.out.println("19:00".matches(regex));
        System.out.println("10:30".matches(regex));
        System.out.println("09:49".matches(regex));
        System.out.println("01:10".matches(regex));
    }

    @Test
    public void testAnalyzeIP() {
        assertEquals("cn.bing.com", extractIPFromUrl("https://cn.bing.com/file/search?q=httpurlconnection+header&qs=CT"));
        assertEquals("cn.bing.com", extractIPFromUrl("http://cn.bing.com/file/search?q=httpurlconnection+header&qs=CT"));
        assertEquals("10.0.2.101:8887", extractIPFromUrl("https://10.0.2.101:8887/file/search?q=httpurlconnection+header&qs=CT"));
        assertEquals("10.0.2:8887", extractIPFromUrl("http://10.0.2:8887/file/search?q=httpurlconnection+header&qs=CT"));
        assertEquals("10.0.2.101", extractIPFromUrl("https://10.0.2.101/file/search?q=httpurlconnection+header&qs=CT"));
        assertEquals(
                "43.255.214.50:10000",
                extractIPFromUrl("https://43.255.214.50:10000/xim/file/6/5991d6d50ffe4c205ec5c80c626bac05/46c15795ac568b0b552d52c67a588eea/file.apk?uid=5C98F1701EBBB7F33C328D5117CD540E&token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRUeXBlIjoyLCJjdGltZSI6MTY1Mzk4OTY3NjkwOSwiZGJfa2V5IjoiY2U2YTMyYWRhNjgxNGI3NjQ1MjRjYjcwZjBiNWZkZWUiLCJkZXZpY2VJRCI6ImIxYzYyOWUyYTI2MzhmZThkZDRmOGUzNzU2MGRhNzQ4IiwiZXhwIjowLCJ1aWQiOiI1Qzk4RjE3MDFFQkJCN0YzM0MzMjhENTExN0NENTQwRSJ9.1kEbwPVBwPthWejXfX8Zy06hTzFyY5Tb9055_F5G2U0&appType=2&version=2.2.9&osVersion=30&deviceID=b1c629e2a2638fe8dd4f8e37560da748")
        );
    }

    private String extractIPFromUrl(String url) {
        String regex = "(?<=http(s?)://).+?(?=/)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        String result = null;
        while (matcher.find()) {
            String s = matcher.group();
            System.out.println("greyson, extractIPFromUrl: " + s);
            if (result == null) result = s;
        }
        return result;
    }


    @Test
    public void testThread() {
        new MyThread("thread1111").start();
        new MyThread("thread2222").start();
        printSomeThing("main");
    }

    private void printSomeThing(String author) {
        System.out.println(author + " invoke printSomeThing. " + getClass().getName() + " thread = " + Thread.currentThread());
    }

    volatile int value = 0;

    class MyThread extends Thread {

        public MyThread() {
        }

        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            printSomeThing(getName());
            for (int i = 0; i < 200; i++) {
                synchronized (this) {
                    value++;
                    System.out.println(getName() + " = " + value);
                }
            }

            System.out.println(getName() + " final = " + value);
        }
    }

}
