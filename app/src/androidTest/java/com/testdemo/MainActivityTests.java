package com.testdemo;

/**
 * Create by Greyson on 2022/08/01
 */
public class MainActivityTests extends ActivityInstrumentationTestCase2<MainActivity> {



private MainActivity mMainActivity;

private Instrumentation mInstrumentation;

private View mToLoginView;

        privatestaticinti = 0;



// 此构造函数一定要提供

public MainActivityTests() {

        super(MainActivity.class);

        }



@Override

   protectedvoid setUp() throws Exception {

           super.setUp();

           // 如果要发送键盘事件一定要关闭触摸模式。不然键盘事件将被忽略。

           setActivityInitialTouchMode(false);

           // 可以发送键盘事件的对象

           mInstrumentation = getInstrumentation();

           // 启动avtivity，此函数每执行一次，activity就会启动一次。

           mMainActivity = getActivity();

           // 输入框

           mUsernameView = (EditText) mMainActivity.findViewById(R.id.username);

           // 提交按钮

           mToLoginView = mMainActivity.findViewById(R.id.to_login);

           }



           publicvoid testPreConditions() {

           assertTrue(mToLoginView != null);

           }



           publicvoid testToLogin() {

           //和UI相关的操作要放在UI线程中执行。

           /*

            * instrumentation.runOnMainSync();等同于一下两个函数

            * mActivity.runOnUiThread();//主线程中运行UI操作

            * mInstrument.waitForIdleSync();//等待UI同步

            */

           // mMainActivity.runOnUiThread(new Runnable() {

           // public void run() {

           // mUsernameView.requestFocus();

           // }

           // });

           // // 因为测试用例运行在单独的线程上，这里最好要

           // // 同步application，等待其执行完后再运行

           // mInstrumentation.waitForIdleSync();



           mInstrumentation.runOnMainSync(new Runnable() {



@Override

         publicvoid run() {

                 mUsernameView.requestFocus();//获得焦点

                 }

                 });

                 sendKeys(KeyEvent.KEYCODE_Y, KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_U);//输入字符



                 mInstrumentation.runOnMainSync(new Runnable() {

@Override

         publicvoid run() {

                 mToLoginView.requestFocus();

                 mToLoginView.performClick();//执行提交按钮的click事件

                 }

                 });

                 }



                 }
                 ————————————————
                 版权声明：本文为CSDN博主「技术至上」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
                 原文链接：https://blog.csdn.net/wenzongliang/article/details/84711950
