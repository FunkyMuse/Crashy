package com.crazylegend.crashyreporter

import android.content.Context
import android.util.Log
import com.crazylegend.crashyreporter.exceptions.CrashyExceptionHandler
import com.crazylegend.crashyreporter.exceptions.CrashyNotInitializedException
import com.crazylegend.crashyreporter.utils.DeviceUtils
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
     * <meta-data  android:name="com.crazylegend.crashyreporter.initializer.CrashyInitializer"
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
                    "     <meta-data  android:name=\"com.crazylegend.crashyreporter.initializer.CrashyInitializer\"\n" +
                    "     android:value=\"androidx.startup\" />\n" +
                    "     </provider>"


    //region public
    /**
     * Initializes the Crashy report
     * @param context Context
     */
    fun initialize(context: Context) {
        applicationContext = context
    }


    /**
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     * @return Boolean whether delition was success
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
     * You can use this for manually dumping log
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     * @param exception Exception
     */
    @Throws(CrashyNotInitializedException::class)
    fun logException(exception: Exception) {
        setupHandlerAndDumpFolder()
        buildLog(Thread.currentThread(), exception)
    }

    /**
     * You can use this for manually dumping log
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    fun getLogsAsStrings() = dumpFolder.listFiles()?.map { it.readText() }

  /**
     * You can use this for manually dumping log
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    fun getLots() = dumpFolder.listFiles()?.toList()


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

    private fun buildLog(thread: Thread, throwable: Throwable) {
        val stackTrace = getStackTrace(throwable)
        val threadInfo = getThreadInfo(thread)
        saveLog(stackTrace, threadInfo)
    }

    private fun setupHandlerAndDumpFolder() {
        setupExceptionHandler()
        if (!dumpFolder.exists()) dumpFolder.mkdirs()
    }

    private fun getStackTrace(throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        printWriter.use {
            throwable.printStackTrace(it)
        }
        return stringWriter.toString()
    }


    private fun saveLog(stackTrace: String, threadName: String) {
        val pathToWriteTo = File("$pathToDump/$crashLogTime.txt")
        pathToWriteTo.writeText(buildStackTraceString(stackTrace) + "\n" + threadName + "\n" + DeviceUtils.getDeviceDetails(applicationContext))
        dumpFolder.listFiles()?.forEach {
            val text = it.readText()
            Log.d("FILE PATH", it.path)
            Log.d("DEBUGGER", text)
        }
    }

    private fun buildStackTraceString(stackTrace: String) =
            "----------- Stacktrace -----------\n" +
                    "\n" +
                    "$stackTrace\n" +
                    "\n" +
                    "----------- END of Stacktrace -----------\n"

    //endregion
}