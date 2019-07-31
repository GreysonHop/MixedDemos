package com.testdemo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String regex = "^((0[1-9])|(1[0-9])|(2[0-4])):((00)|(15)|(30)|(45))$";
        System.out.println("19:05".matches(regex));
        System.out.println("19:00".matches(regex));
        System.out.println("10:30".matches(regex));
        System.out.println("09:49".matches(regex));
        System.out.println("01:10".matches(regex));
        assertEquals(4, 2 + 2);
    }
}