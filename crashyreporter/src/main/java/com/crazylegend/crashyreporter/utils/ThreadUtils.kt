package com.crazylegend.crashyreporter.utils

import android.os.SystemClock
import com.crazylegend.crashyreporter.extensions.NEW_ROW
import com.crazylegend.crashyreporter.extensions.formatMillisToHoursMinutesSeconds


/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
internal object ThreadUtils {

    fun getThreadInfo(thread: Thread) =
            "`` Thread info ``$NEW_ROW" +
                    NEW_ROW +
                    "Name: ${thread.name}$NEW_ROW" +
                    "ID: ${thread.id}$NEW_ROW" +
                    "State: ${thread.state.name}$NEW_ROW" +
                    "Priority: ${thread.priority}$NEW_ROW" +
                    "Thread group name: ${thread.threadGroup?.name}$NEW_ROW" +
                    "Thread group parent: ${thread.threadGroup?.parent?.name}$NEW_ROW" +
                    "Thread group active count: ${thread.threadGroup?.activeCount()}$NEW_ROW" +
                    "Thread time: ${formatMillisToHoursMinutesSeconds(SystemClock.currentThreadTimeMillis())}$NEW_ROW" +
                    NEW_ROW +
                    "`` END of thread info ``$NEW_ROW"


    fun buildStackTraceString(stackTrace: String) =
            "`` Stacktrace ``$NEW_ROW" +
                    NEW_ROW +
                    "$stackTrace$NEW_ROW" +
                    NEW_ROW +
                    "`` END of stacktrace ``$NEW_ROW"
}