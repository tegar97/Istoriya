package com.tegar.istoriya.ui.main


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.tegar.istoriya.R
import com.tegar.istoriya.data.api.retrofit.ApiConfig
import com.tegar.istoriya.ui.auth.LoginActivity
import com.tegar.istoriya.ui.home.HomeActivity
import com.tegar.istoriya.ui.setting.SettingActivity
import com.tegar.istoriya.utilities.EspressoIdlingResource
import com.tegar.istoriya.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class MainActivityTest {
    private val mockWebServer = MockWebServer()


    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

    }
    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginAndLogout(){

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)



        Intents.init()
        onView(withId(R.id.btn_login)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))

        onView(withId(R.id.edt_email)).perform(typeText("tegarakmal3401@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.edt_password)).perform(typeText("tegar123"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())


        intended(hasComponent(HomeActivity::class.java.name))
        onView(withId(R.id.rvStory))
            .check(matches(isDisplayed()))


        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>())
        onView(withText("Setting")).perform(click())

        intended(hasComponent(SettingActivity::class.java.name))
        onView(withId(R.id.btn_logout)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_logout)).perform(click())


        intended(hasComponent(MainActivity::class.java.name))


        Intents.release()


    }











}