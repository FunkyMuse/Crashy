package com.crazylegend.crashy

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.crazylegend.crashyreporter.CrashyReporter

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        CrashyReporter.getLogsAsStrings()?.asSequence()?.forEach {
            Log.d("CRASHY", "WITH CRASH REASON: \n")
            findViewById<TextView>(R.id.test).apply {
                text = it
                movementMethod = ScrollingMovementMethod()
            }
            println(it)
        }

        findViewById<AppCompatButton>(R.id.crash).apply {
            setOnClickListener {
                CrashyReporter.purgeLogs()

                val array = arrayOf(1, 2)
                array[120]
            }
        }
    }


}