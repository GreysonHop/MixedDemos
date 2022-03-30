package com.testdemo;

import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.testdemo.architecture.TestDataBindingAct;
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
@LargeTest
public class ActivityTest {

    @Rule
    public ActivityTestRule<TestFlipperActivity> mActivityTestRule =
            new ActivityTestRule<>(TestFlipperActivity.class);

    @Test
    public void buttonTest() {
        onView(withId(R.id.previous))
                .check(matches(withText("previous")))
                .perform(click());
        onView(withId(R.id.next))
                .check(matches(withText("next")));
    }

    // greyson_2022/3/30 跑起来了
    @Test
    public void launchActivity() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(appContext, TestFlipperActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //appContext.startActivity(intent);

        ActivityTestRule<TestFlipperActivity> activityTestRule = new ActivityTestRule<>(TestFlipperActivity.class, false, false);
        activityTestRule.launchActivity(intent);
    }
}
