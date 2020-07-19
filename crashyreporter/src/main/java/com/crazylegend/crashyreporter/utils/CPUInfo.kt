@file:Suppress("SameParameterValue")

package com.crazylegend.crashyreporter.utils

import java.io.IOException


/**
 * Created by crazy on 7/19/20 to long live and prosper !
 */
object CPUInfo {

    fun getNumberOfCores() = Runtime.getRuntime().availableProcessors()

    fun getCPUModel() = getProcessText("cpuinfo")?.substringAfter("Hardware\t: ")?.trim()

    private fun getProcessText(procFolder: String): String? {
        val process = Runtime.getRuntime().exec("cat /proc/$procFolder")
        return try {
            process.inputStream.use {
                it.reader().readText()
            }
        } catch (e: IOException) {
            null
        } finally {
            process.destroy()
        }
    }

}