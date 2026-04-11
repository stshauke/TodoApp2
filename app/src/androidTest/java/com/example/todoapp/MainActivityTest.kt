package com.example.todoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.todoapp.data.TaskDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        // Réinitialise le singleton avant chaque test
        // Évite les conflits avec TaskDaoTest
        TaskDatabase.resetInstance()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun fab_isDisplayed() {
        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))
    }

    @Test
    fun tabLayout_isDisplayed() {
        onView(withId(R.id.tabLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addTask_appearsInList() {
        val title = "Test ${System.currentTimeMillis()}"
        onView(withId(R.id.fab)).perform(click())
        onView(withHint("Ex: Acheter du pain"))
            .perform(typeText(title), closeSoftKeyboard())
        onView(withText("Ajouter")).perform(click())
        onView(withText(title)).check(matches(isDisplayed()))
    }
}