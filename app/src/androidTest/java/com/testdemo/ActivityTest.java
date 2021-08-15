package com.testdemo;

import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.testdemo.testFlipView.TestFlipperActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Create by Greyson on 2021/08/15
 * 测试Activity和View。区别于{@link ApplicationTest}类，后者是测试业务逻辑的
 */
@RunWith(AndroidJUnit4.class)
public class ActivityTest {

    /*@Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void buttonTest() throws Exception {
        onView(withId(R.id.previous))
                .check(matches(withText("previous")))
                .perform(click());
        onView(withId(R.id.next))
                .check(matches(withText("next")));
    }*/

    //TODO greyson_2021/8/15 现在还跑不起来，提示无法通过Intent启动某个Activity...
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //appContext.startActivity(intent);

        ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);
        activityTestRule.launchActivity(intent);
    }
}
