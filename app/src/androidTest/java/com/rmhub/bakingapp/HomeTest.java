package com.rmhub.bakingapp;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.rmhub.bakingapp.model.Recipe;
import com.rmhub.bakingapp.model.Step;
import com.rmhub.bakingapp.ui.Home;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
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

    @Rule
    public ActivityTestRule<Home> homeActivityTestRule = new ActivityTestRule<>(Home.class);

    @Test
    public void clickRecyclerViewItem_HomeActivity() {

        try {
            List<Recipe> recipes = homeActivityTestRule.getActivity().getRecipeList();
            boolean hasTwoPane = homeActivityTestRule.getActivity().getResources().getBoolean(R.bool.tablet);
            int position = 1;
            if (recipes != null && !recipes.isEmpty()) {

                onView(withId(R.id.empty_view)).check(matches(not(isDisplayed())));

                onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

                onView(withId(R.id.detail_layout_recipe_name)).check(matches(withText(recipes.get(position).getName())));

                Thread.sleep(1000);

                int i = 0;
                List<Step> steps = recipes.get(position).getSteps();
                if (!hasTwoPane) {
                    Step step1 = steps.get(0);

                    onView(withId(R.id.item_list)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));

                    onView(withId(R.id.button_prev)).check(matches(not(isDisplayed())));

                    if (TextUtils.isEmpty(step1.getVideoURL())) {
                        onView(allOf(withId(R.id.no_video_text), withText("No Video Available"), isDisplayed()));
                    }

                    if (!TextUtils.isEmpty(step1.getThumbnailURL())) {
                        onView(withId(R.id.recipe_step_thumbnail)).check(matches(isDisplayed()));
                    }

                    ViewInteraction nextButton = onView(withId(R.id.button_next)).check(matches(isDisplayed()));

                    nextButton.perform(click());

                    Step step2 = steps.get(1);
                    if (TextUtils.isEmpty(step2.getVideoURL())) {
                        onView(allOf(withId(R.id.no_video_text), withText("No Video Available"), isDisplayed()));
                    }

                    if (!TextUtils.isEmpty(step2.getThumbnailURL())) {
                        onView(withId(R.id.recipe_step_thumbnail)).check(matches(isDisplayed()));
                    }

                    Thread.sleep(1000);

                    ViewInteraction prevButton = onView(withId(R.id.button_prev)).check(matches(isDisplayed()));
                    prevButton.perform(click());

                    onView(allOf(withId(R.id.step_desc), withText(step1.getDescription()), isDisplayed()));

                    pressBack();

                    onView(withId(R.id.detail_layout_recipe_name)).check(matches(withText(recipes.get(position).getName())));
                    Thread.sleep(1000);

                    onView(withId(R.id.item_list)).perform(RecyclerViewActions.actionOnItemAtPosition(steps.size(), click()));

                    ViewInteraction doneButton = onView(withId(R.id.button_done)).check(matches(isDisplayed()));
                    doneButton.perform(click());

                    onView(withId(R.id.detail_layout_recipe_name)).check(matches(withText(recipes.get(position).getName())));

                } else {

                    onView(withId(R.id.item_list)).perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
                    onView(allOf(withId(R.id.step_desc), withText(steps.get(position).getDescription()), isDisplayed()));
                    Thread.sleep(500);
                    onView(withId(R.id.item_list)).perform(RecyclerViewActions.actionOnItemAtPosition(steps.size() / 2, click()));
                    onView(allOf(withId(R.id.step_desc), withText(steps.get(steps.size() / 2).getDescription()), isDisplayed()));
                    Thread.sleep(500);
                    onView(withId(R.id.item_list)).perform(RecyclerViewActions.actionOnItemAtPosition(steps.size(), click()));
                    onView(allOf(withId(R.id.step_desc), withText(steps.get(steps.size()).getDescription()), isDisplayed()));

                }

             /*   for (Step step : steps) {
                    onView(allOf(withId(R.id.step_desc), withText(step.getDescription()), isDisplayed()));
                    if (TextUtils.isEmpty(step.getVideoURL())) {
                        onView(allOf(withId(R.id.no_video_text), withText("No Video Available"), isDisplayed()));
                    }

                    if (!TextUtils.isEmpty(step.getThumbnailURL())) {
                        onView(withId(R.id.recipe_step_thumbnail)).check(matches(isDisplayed()));
                    }
                    if (i == 0) {
                        onView(withId(R.id.button_prev)).check(matches(not(isDisplayed())));

                        ViewInteraction nextButton = onView(withId(R.id.button_next)).check(matches(isDisplayed()));
                        nextButton.perform(click());

                        Thread.sleep(1000);

                        ViewInteraction prevButton = onView(withId(R.id.button_prev)).check(matches(isDisplayed()));
                        prevButton.perform(click());

                        onView(allOf(withId(R.id.step_desc), withText(step.getDescription()), isDisplayed()));

                    }

                    Thread.sleep(1000);

                    ++i;
                    if (hasTwoPane) {
                        onView(withId(R.id.item_list)).perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
                    } else {

                        if (i < steps.size() - 1) {
                            ViewInteraction nextButton = onView(withId(R.id.button_next)).check(matches(isDisplayed()));
                            nextButton.perform(click());
                        } else {
                            ViewInteraction doneButton = onView(withId(R.id.button_done)).check(matches(isDisplayed()));
                            doneButton.perform(click());
                        }
                    }
                    Thread.sleep(1000);
                }

  */
            } else {
                onView(withId(R.id.empty_view)).check(matches(isDisplayed()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
