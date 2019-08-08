package com.testdemo;

import org.junit.Test;

import java.util.LinkedList;

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

        new MyThread("thread1111").start();
        new MyThread("thread2222").start();
        printSomeThing("main");


        assertEquals(4, 2 + 2);

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