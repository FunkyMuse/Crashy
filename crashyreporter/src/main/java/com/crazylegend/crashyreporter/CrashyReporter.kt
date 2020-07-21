package com.crazylegend.crashyreporter

import android.content.Context
import com.crazylegend.crashyreporter.handlers.CrashyExceptionHandler
import com.crazylegend.crashyreporter.handlers.CrashyNotInitializedException
import com.crazylegend.crashyreporter.utils.DeviceUtils
import com.crazylegend.crashyreporter.utils.ThreadUtils
import com.crazylegend.crashyreporter.utils.ThreadUtils.getThreadInfo
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
        setupExceptionHandler()
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
    inline fun getLogsAsStringsAndPurge(purgeResult: (Boolean) -> Unit = {}) = dumpFolder.listFiles()?.map { it.readText() }.also { purgeResult(purgeLogs()) }

    /**
     * Get all dumps as [List] of [File]
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    inline fun getLogFilesAndPurge(purgeResult: (Boolean) -> Unit = {}) = dumpFolder.listFiles()?.toList().also { purgeResult(purgeLogs()) }


    /**
     * Get all dumps as [List] of [String]
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    inline fun getLogsAsStringsActionBeforePurge(purgeResult: (Boolean) -> Unit = {}, onStringsAction: (List<String>?) -> Unit) =
            dumpFolder.listFiles()?.map { it.readText() }.also {
                onStringsAction(it)
                purgeResult(purgeLogs())
            }

    /**
     * Get all dumps as [List] of [File]
     * @throws CrashyNotInitializedException see [NOT_REGISTERED_MESSAGE]
     */
    @Throws(CrashyNotInitializedException::class)
    inline fun getLogFilesActionBeforePurge(purgeResult: (Boolean) -> Unit = {}, onFilesAction: (List<File>?) -> Unit) = dumpFolder.listFiles()?.toList().also {
        onFilesAction(it)
        purgeResult(purgeLogs())
    }

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
        pathToWriteTo.writeText(ThreadUtils.buildStackTraceString(stackTrace) + "\n" +
                threadName + "\n" + DeviceUtils.getDeviceDetails(applicationContext) + "\n" +
                DeviceUtils.getRunningProcesses(applicationContext))
    }
    //endregion
}