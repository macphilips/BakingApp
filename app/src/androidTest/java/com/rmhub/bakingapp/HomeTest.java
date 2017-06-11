package com.rmhub.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.rmhub.bakingapp.ui.Home;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by MOROLANI on 5/28/2017
 * <p>
 * owm
 * .
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeTest {
    private static final String RECIPE_NAME = "Brownies";
    @Rule
    public ActivityTestRule<Home> mActivityRule = new ActivityTestRule<>(Home.class);


    @Test
    public void clickRecyclerViewItem_HomeActivity() {

        boolean b = mActivityRule.getActivity().hasRecipe();
        if (b) {
            onView(withId(R.id.empty_view)).check(matches(not(isDisplayed())));
            onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
            onView(withId(R.id.detail_layout_recipe_name)).check(matches(withText(RECIPE_NAME)));
        } else {
            onView(withId(R.id.empty_view)).check(matches(isDisplayed()));
        }
    }
}
