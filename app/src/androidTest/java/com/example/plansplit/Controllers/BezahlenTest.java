package com.example.plansplit.Controllers;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
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
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * Works with this scenario: Der Benutzer schuldet dem obersten Freund 10 TL
 * Tested on PIXEL XL API 25
 */

@LargeTest
public class BezahlenTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void bezahlenTest() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_friends),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction cardView = onView(
                allOf(withId(R.id.groupOperations_card),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recycler_friends),
                                        0),
                                0),
                        isDisplayed()));
        cardView.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.pay_IButton),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.payEdt),
                        childAtPosition(
                                allOf(withId(R.id.fragmentPayOperations),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.payEdt),
                        childAtPosition(
                                allOf(withId(R.id.fragmentPayOperations),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("10"), closeSoftKeyboard());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        closeSoftKeyboard();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.savePayButton),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       pressBack();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.groupOperations_amount),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView.check(matches(withText("0.0 TL")));
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
