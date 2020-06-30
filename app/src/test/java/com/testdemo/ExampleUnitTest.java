package com.testdemo;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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