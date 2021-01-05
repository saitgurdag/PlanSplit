package com.example.plansplit.Controllers;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import com.example.plansplit.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Works with this scenario:  with one group, without any expense beforehand
 * Tested on PIXEL 4 XL API 30
 */
@LargeTest
public class AddGroupExpenseTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addGroupExpenseTest() {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_groups),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerGroups),
                        childAtPosition(
                                withId(R.id.recyclerRelative),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.add_expense),
                        childAtPosition(
                                allOf(withId(R.id.recycler_viewRL2),
                                        childAtPosition(
                                                withId(R.id.mygroup_bottomRL),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextExpenseName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("su"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editTextExpenseAmounth),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("20"), closeSoftKeyboard());

        closeSoftKeyboard();

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.saveExpenseButton),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                10),
                        isDisplayed()));
        appCompatButton.perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.groups_events_object_name), withText("su"),
                        withParent(allOf(withId(R.id.lin1),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class)))),
                        isDisplayed()));
        textView.check(matches(withText("su")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.group_events_object_coast), withText("20"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("20")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
