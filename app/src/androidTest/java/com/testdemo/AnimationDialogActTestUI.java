package com.testdemo;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withAlpha;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Instrumentation;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Create by Greyson on 2021/08/15
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AnimationDialogActTestUI {

    @Before
    public void launchActivity() {
        ActivityScenario.launch(TestAnimationDialogAct.class);
    }

    @Test
    public void testBGAlpha() { // 2022/5/27 很奇怪，能跑得通，但就是会报红。找不到Activity和R类
        onView(withId(R.id.anim_btn)).perform(click());
        onView(withId(R.id.blackBgIV)).check(matches(withAlpha(0.9f)));

    }


    /*@Test
    public void testFromNet() { // 了解一下getResult()和moveToState()
        ActivityScenario<TestAnimationDialogAct> scenario = ActivityScenario.launch(TestAnimationDialogAct.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
        // onActivityResult never be called after %d milliseconds [45000] 45s
        Instrumentation.ActivityResult result = scenario.getResult();
        System.out.println("test delete image: "+result.getResultCode()+"");
    }*/
}
