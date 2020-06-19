package com.crazylegend.crashyreporter

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CrashyReporterTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupReporter(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        CrashyReporter.initialize(context)
    }

    @Test
    fun forceCrash_and_check_if_inserted(){
        CrashyReporter.purgeLogs()
        CrashyReporter.logException(ConcurrentModificationException())
        val list = CrashyReporter.getLogsAsStrings()
        val condition = !list.isNullOrEmpty()
        assertThat(condition, `is`(true))
    }

    @Test
    fun purgeLogs(){
        forceCrash_and_check_if_inserted()
        val purgatory = CrashyReporter.purgeLogs()
        assertThat(purgatory, `is`(true))
    }

    @Test
    fun forceCrash_and_check_if_inserted_with_thread(){
        CrashyReporter.purgeLogs()
        CrashyReporter.logException(thread = Thread.currentThread(), throwable = IndexOutOfBoundsException())
        val list = CrashyReporter.getLogsAsStrings()
        val condition = !list.isNullOrEmpty()
        assertThat(condition, `is`(true))
    }

    @Test
    fun getContentTest(){
        val list = CrashyReporter.getLogsAsStrings()

        if (list.isNullOrEmpty()){
            val first = list?.firstOrNull()
            assertNull(first)
        } else {
            val first = list.first().isNotBlank()
            assert(first)
        }
    }

    @Test
    fun getContentAndPurge(){
        forceCrash_and_check_if_inserted()
        val list = CrashyReporter.getLogsAsStringsAndPurge()
        assertNotNull(list)
        val condition = !list.isNullOrEmpty()
        assert(condition)
    }



}