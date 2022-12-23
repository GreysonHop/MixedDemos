package testJava;

import androidx.annotation.GuardedBy;

import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Create by Greyson on 2021/07/17
 * 展示一些线程安全类
 */
class ConcurrentExposition {

    public static void main(String[] args) {
        // @GuardedBy("this")

Thread.dumpStack();

        new Exception().printStackTrace();

        Callable<Integer> eval = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 0;
            }
        };
        FutureTask<Integer> ft = new FutureTask<>(eval);
        ft.run();
        try {
            ft.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new TimerTask() {
            @Override
            public void run() {
                // 参考SmoothScrollTimerTask类
            }
        };

        new ArrayBlockingQueue<Integer>(10);

        int cpuCount = Runtime.getRuntime().availableProcessors();
        new CyclicBarrier(cpuCount, () -> {

        });
    }
}
