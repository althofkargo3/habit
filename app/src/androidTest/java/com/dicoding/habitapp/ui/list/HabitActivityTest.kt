package com.dicoding.habitapp.ui.list

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Before
import org.junit.Test
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.add.AddHabitActivity

//TODO 16 : Write UI test to validate when user tap Add Habit (+), the AddHabitActivity displayed
class HabitActivityTest {
    @Before
    fun initTest() {
        Intents.init()
    }

    @Test
    fun checkRedirectToAddTask() {
        ActivityScenario.launch(HabitListActivity::class.java).use {

            Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())
            Intents.intended(hasComponent(AddHabitActivity::class.java.name))

        }
    }
}