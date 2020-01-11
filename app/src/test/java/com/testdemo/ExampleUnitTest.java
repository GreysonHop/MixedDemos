package com.testdemo;

import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

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
    public void addition_isCorrect() throws Exception {

//        new MyThread("thread1111").start();
//        new MyThread("thread2222").start();
        printSomeThing("main");


        String regex = "^((0[1-9])|(1[0-9])|(2[0-4])):((00)|(15)|(30)|(45))$";
        System.out.println("19:05".matches(regex));
        System.out.println("19:00".matches(regex));
        System.out.println("10:30".matches(regex));
        System.out.println("09:49".matches(regex));
        System.out.println("01:10".matches(regex));


        Calendar cal = Calendar.getInstance();
        System.out.println("greyson: " + cal.getTimeInMillis());
        cal.set(Calendar.SECOND, 0);
        System.out.println("greyson: " + cal.getTimeInMillis());
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println("greyson: " + cal.getTimeInMillis());

        assertEquals(4, 2 + 2);

        SimpleDateFormat dateFormat = new SimpleDateFormat("'Today': EEEE, yyyy MMM d, hh:m,a", Locale.ENGLISH);
        System.out.println("date = " + dateFormat.format(new Date()));

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