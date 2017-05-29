package com.rmhub.bakingapp;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.rmhub.bakingapp.ui.Home;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by MOROLANI on 5/28/2017
 * <p>
 * owm
 * .
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeTest {
    @Rule
    public ActivityTestRule<Home> mActivityRule = new ActivityTestRule<>(Home.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withText("Hello world!")).check(matches(isDisplayed()));
    }
}
