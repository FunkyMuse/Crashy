package com.crazylegend.crashyreporter.utils

import java.io.IOException


/**
 * Created by crazy on 7/19/20 to long live and prosper !
 */
object RuntimeInfo {


    fun getNumberOfCores() = Runtime.getRuntime().availableProcessors()

    fun getCPUMaxFrequency() = getProcessText("cpumaxfreq")?.trim()

    fun getSerialNumber() = getProcessText("serial_num")?.trim()

    /**
     * the uptime of the system (seconds)
     */
    fun getUpTime() = formatTime(getProcessText("uptime")?.split(" ")?.firstOrNull()?.toDoubleOrNull()?.toLong() ?: 0L)

    private fun formatTime(longVal: Long): String {
        val hours = longVal.toInt() / 3600
        var remainder = longVal.toInt() - hours * 3600
        val mins = remainder / 60
        remainder -= mins * 60
        val secs = remainder
        return "${hours}h ${mins}m ${secs}s"
    }

    fun getLinuxKernelVersion() = getProcessText("version")?.trim()

    fun getHardwareInfo() = getProcessText("hwinfo")?.trim()

    fun getCPUModel() = getProcessText("cpuinfo")?.substringAfter("Hardware\t: ")?.trim()

    fun getMemoryInfo() = getProcessText("meminfo")

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

    private fun runProcess(command: String): String? {
        val process = Runtime.getRuntime().exec(command)
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