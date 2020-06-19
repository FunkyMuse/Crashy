package com.crazylegend.crashyreporter.utils


/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
internal object ThreadUtil {

    fun getThreadInfo(thread: Thread) =
            "----------- Thread info -----------\n" +
                    "\n" +
                    "Name: ${thread.name}\n" +
                    "ID: ${thread.id}\n" +
                    "State: ${thread.state.name}\n" +
                    "Priority: ${thread.priority}\n" +
                    "Thread group name: ${thread.threadGroup?.name}\n" +
                    "Thread group parent: ${thread.threadGroup?.parent?.name}\n" +
                    "Thread group active count: ${thread.threadGroup?.activeCount()}\n" +
                    "\n" +
                    "----------- END of thread info -----------\n"


    fun buildStackTraceString(stackTrace: String) =
            "----------- Stacktrace -----------\n" +
                    "\n" +
                    "$stackTrace\n" +
                    "\n" +
                    "----------- END of stacktrace -----------\n"
}