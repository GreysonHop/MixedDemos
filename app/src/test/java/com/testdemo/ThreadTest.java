package com.testdemo;

import org.junit.Test;

/**
 * Created by Greyson on 2021/07/23
 */

public class ThreadTest {

    @Test
    public void testThread() throws InterruptedException {
        One one = new One();

        Someone someone = new Someone();
        someone.test(one);

        Thread.sleep(1500);
        one.stop = true;
        Thread.sleep(1500);
    }
}

class Someone {

    void test(One one) {
        System.out.println("main's one=" + one);
        new Thread(() -> {
            while(!one.stop) {
                System.out.println("t1's one=" + one);
            }
        }).start();

        /*new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2's one=" + one);
            one.stop = true;
        }).start();*/
    }
}
class One {
    boolean stop = false;
}

