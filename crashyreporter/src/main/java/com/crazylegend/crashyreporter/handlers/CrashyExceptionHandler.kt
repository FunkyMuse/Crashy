package com.crazylegend.crashyreporter.handlers

import com.crazylegend.crashyreporter.CrashyReporter


/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
internal class CrashyExceptionHandler : Thread.UncaughtExceptionHandler {

    private val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        CrashyReporter.logException(thread, throwable)
        exceptionHandler?.uncaughtException(thread, throwable)
    }
}