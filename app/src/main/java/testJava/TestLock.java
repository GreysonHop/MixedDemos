package testJava;

import java.math.BigDecimal;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Create by Greyson on 2022/12/25
 */
class TestLock {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch mainEndGate = new CountDownLatch(2);
        Account account = new Account();

        //int count = Runtime.getRuntime().availableProcessors();
        CyclicBarrier barrier = new CyclicBarrier(
                2,
                () -> {
                    System.out.println("所以栅栏都打开了（所有任务结束了）, account = " + account.getBalance());
                    // mainEndGate.countDown(); // 如果数量为1，在这里放行的话，主线程结束会在两个线程的栅栏通过之前。当然顺序不知是否固定
                }
        );


        Thread thread1 = new Thread() {
            @Override
            public void run() {

                try {
                    account.lock.lock();

                    Thread.sleep(5 * 1000); // 模拟耗时操作，拖延线程结束时间

                    System.out.println("线程1 睡眠结束");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("线程1 finally 释放锁");
                    account.lock.unlock();
                }

                try {
                    System.out.println("线程1 到达栅栏");
                    barrier.await();
                    System.out.println("线程1 通过栅栏");
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }

                mainEndGate.countDown();
            }
        };

        Thread thread2 = new Thread() {
            @Override
            public void run() {

                /*try {
                    while (true) {

                        System.out.println("线程2 尝试获取锁");
                        if (account.lock.tryLock()) {
                            account.addBalance(22.2);
                            break;
                        }
                        System.out.println("线程2 锁获取失败，继续循环");
                        Thread.sleep(1 * 1000);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("线程2 finally 释放锁");
                    // 因为此方法使用了死循环，也就说明最终肯定会得到锁，所以在这里才能去释放锁。
                    // 如果没获取到锁就去释放的话会抛异常
                    account.lock.unlock();
                }*/


                // 不用 while 的实现方式
                try {

                    System.out.println("线程2 尝试获取锁（有超时限制的）");
                    if (account.lock.tryLock(4, TimeUnit.SECONDS)) {
                        System.out.println("线程2 锁获取成功！设置值");
                        account.addBalance(22.2);
                        account.lock.unlock();
                    } else {
                        System.out.println("线程2 超时了都没有获取到锁");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                try {
                    System.out.println("线程2 到达栅栏");
                    barrier.await();
                    System.out.println("线程2 通过栅栏");
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }

                mainEndGate.countDown();
            }

        };

        thread1.start();
        thread2.start();

        mainEndGate.await();
        System.out.println("主线程结束");
    }
}

class Account {
    Lock lock = new ReentrantLock();

    private double balance = 0;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addBalance(double balance) {
        BigDecimal origin = BigDecimal.valueOf(this.balance);
        BigDecimal plus = BigDecimal.valueOf(balance);
        this.balance = origin.add(plus).doubleValue();
    }
}
