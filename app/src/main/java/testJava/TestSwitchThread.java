package testJava;

import java.util.LinkedList;

/**
 * 为了模仿、验证Handler消息机制中的从工作线程回到主线程的原理
 */
public class TestSwitchThread {

    public static void main(String[] args) {
        new App();
    }

}

class App {
    public App() {
        new MyThread().start();
        MyLooper myLooper = new MyLooper();
        myLooper.loop();
    }

    private LinkedList<Handler> messageQueue = new LinkedList<>();

    class Handler {
        public void onHandleMessage() {
        }
    }

    class MyLooper {
        //模仿消息队列，线程任务完成后转回到主线程的过程
        public void loop() {
            int allowedIdleSecond = 7;
            int idleSecond = 0;
            while (true) {
                if (idleSecond == allowedIdleSecond) {
                    break;
                }
                Handler handler;
                if ((handler = messageQueue.poll()) != null) {
                    handler.onHandleMessage(); // 调用时是否回到主线程？
                    idleSecond = 0;
                } else {
                    idleSecond++;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }
    }

    class MyThread extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                System.out.println("MyThread add message = " + Thread.currentThread());
                messageQueue.add(new Handler() { // 在工作线程中添加消息Message对象，这里直接用自己定义的Handler类代替它
                    @Override
                    public void onHandleMessage() {
                        super.onHandleMessage();
                        System.out.println("onHandleMessage = " + Thread.currentThread());
                    }
                });
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
            }
        }

    }
}
