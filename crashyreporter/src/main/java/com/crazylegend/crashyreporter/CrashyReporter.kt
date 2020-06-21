package com.crazylegend.crashyreporter

import android.content.Context
import android.util.Log
import com.crazylegend.crashyreporter.extensions.*
import com.crazylegend.crashyreporter.extensions.getExitReasons
import com.crazylegend.crashyreporter.extensions.getThermalStatus
import com.crazylegend.crashyreporter.extensions.isInPowerSaveMode
import com.crazylegend.crashyreporter.extensions.isSustainedPerformanceModeSupported
import com.crazylegend.crashyreporter.handlers.CrashyExceptionHandler
import com.crazylegend.crashyreporter.handlers.CrashyNotInitializedException
import com.crazylegend.crashyreporter.utils.DeviceUtils
import com.crazylegend.crashyreporter.utils.ThreadUtil
import com.crazylegend.crashyreporter.utils.ThreadUtil.getThreadInfo
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
object CrashyReporter {

    private lateinit var applicationContext: Context

    //paths
    private val pathToDump get() = applicationContext.filesDir.path + "/crashy/logs"
    val dumpFolder get() = File(pathToDump)


    //time
    internal val dateFormat get() = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    private val crashLogTime get() = dateFormat.format(Date())

    /**
     * <provider
     * android:name="androidx.startup.InitializationProvider"
     * android:authorities="${applicationId}.androidx-startup"
     * android:exported="false"
     * tools:node="merge">
     * <meta-data
     * android:name="com.crazylegend.crashyreporter.initializer.CrashyInitializer"
     * android:value="androidx.startup" />
     *  </provider>
     */
    private const val NOT_REGISTERED_MESSAGE =
            "You must register the content provider in your AndroidManifest.xml" +
                    "<provider\n" +
                    "     android:name=\"androidx.startup.InitializationProvider\"\n" +
                    "     android:authorities=\"$ {applicationId}.androidx-startup\"\n" +
                    "     android:exported=\"false\"\n" +
                    "     tools:node=\"merge\">\n" +
                    "     <meta-data  " +
                    "     android:name=\"com.crazylegend.crashyreporter.initializer.CrashyInitializer\"\n" +
                    "     android:value=\"androidx.startup\" />\n" +
                    "     </provider>"


    //region public
    /**
     * Initializes the Crashy report
     * @param context Context
     */
    fun initialize(context: Context) {
        applicationContext = context

        Log.d("THERMAL", context.getThermalStatus.toString())
        context.getExitReasons(0, Int.MAX_VALUE).forEach {
            Log.d("EXIT REASON", it.toString())
        }
        Log.d("SUSTAINED PERFORMANCE", context.isSustainedPerformanceModeSupported.toString())
        Log.d("isInPowerSaveMode", context.isInPowerSaveMode.toString())
        Log.d("isInInteractiveState", context.isInInteractiveState.toString())
        Log.d("IgnoringBatteryOptimiz", context.isIgnoringBatteryOptimization.toString())
        Log.d("locationPowerSaveMode", context.locationPowerSaveMode.toString())
        Log.d("isDeviceIdle", context.isDeviceIdle.toString())
    }


    /**
     * Deletes all the logs inside the [dumpFolder]
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     * @return Boolean whether deletion was a success
     */
    @Throws(CrashyNotInitializedException::class)
    fun purgeLogs() = dumpFolder.deleteRecursively()

    /**
     * You can use this for manually dumping log
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     * @param thread Thread
     * @param throwable Throwable
     */
    @Throws(CrashyNotInitializedException::class)
    fun logException(thread: Thread, throwable: Throwable) {
        setupHandlerAndDumpFolder()
        buildLog(thread, throwable)
    }

    /**
     * You can use this for manually dumping log it takes an [Exception] and uses [Thread.currentThread] as the thread of error
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     * @param exception Exception
     */
    @Throws(CrashyNotInitializedException::class)
    fun logException(exception: Throwable) {
        setupHandlerAndDumpFolder()
        buildLog(Thread.currentThread(), exception)
    }

    /**
     * Get all dumps as [List] of [String]
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    fun getLogsAsStrings() = dumpFolder.listFiles()?.map { it.readText() }

    /**
     * Get all dumps as [List] of [File]
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    fun getLogFiles() = dumpFolder.listFiles()?.toList()


    /**
     * Get all dumps as [List] of [String]
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    fun getLogsAsStringsAndPurge() = dumpFolder.listFiles()?.map { it.readText() }.also { purgeLogs() }

    /**
     * Get all dumps as [List] of [File]
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    fun getLogFilesAndPurge() = dumpFolder.listFiles()?.toList().also { purgeLogs() }


    //endregion


    //region privates
    private fun setupExceptionHandler() {
        if (!::applicationContext.isInitialized) {
            throw CrashyNotInitializedException(NOT_REGISTERED_MESSAGE)
        }

        if (Thread.getDefaultUncaughtExceptionHandler() !is CrashyExceptionHandler) {
            Thread.setDefaultUncaughtExceptionHandler(CrashyExceptionHandler())
        }
    }

    private fun buildLog(thread: Thread, throwable: Throwable) = saveLog(getStackTrace(throwable), getThreadInfo(thread))

    private fun setupHandlerAndDumpFolder() {
        setupExceptionHandler()
        if (!dumpFolder.exists()) dumpFolder.mkdirs()
    }

    private fun getStackTrace(throwable: Throwable) =
            with(StringWriter()) {
                PrintWriter(this).also { printWriter -> printWriter.use { writer -> throwable.printStackTrace(writer) } }
                toString()
            }


    private fun saveLog(stackTrace: String, threadName: String) {
        val pathToWriteTo = File("$pathToDump/$crashLogTime.txt")
        pathToWriteTo.writeText(ThreadUtil.buildStackTraceString(stackTrace) + "\n" + threadName + "\n" + DeviceUtils.getDeviceDetails(applicationContext))
    }
    //endregion
}