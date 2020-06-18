package com.crazylegend.crashyreporter.initializer

import android.content.Context
import androidx.startup.Initializer
import com.crazylegend.crashyreporter.CrashyReporter


/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
internal class CrashyInitializer : Initializer<CrashyInitializer.CrashyToken> {

    object CrashyToken

    override fun create(context: Context) = with(CrashyReporter.initialize(context)){
        CrashyToken
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>>  = mutableListOf()
}