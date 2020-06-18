package com.crazylegend.crashy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crazylegend.crashyreporter.CrashyReporter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CrashyReporter.purgeLogs()


        //Crashes and exceptions are also captured from other threads
        Thread(Runnable {
            try {
                val array = arrayOf(1,2)
                array[120]
            } catch (e: Exception) {
                //log caught Exception
               CrashyReporter.logException(e)
            }
        }).start()

    }
}