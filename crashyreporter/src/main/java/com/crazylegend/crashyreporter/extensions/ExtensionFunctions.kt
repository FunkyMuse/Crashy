package com.crazylegend.crashyreporter.extensions

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.*
import android.app.ApplicationExitInfo.*
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.*
import com.crazylegend.crashyreporter.CrashyReporter
import java.util.*


/**
 * Created by crazy on 6/21/20 to long live and prosper !
 */

internal data class AppDeathInfo(
        val description: String?,
        val importance: String,
        val reason: String,
        val timestamp: String
)

private inline val Context.powerManager
    get() = getSystemService(Context.POWER_SERVICE) as PowerManager?

private inline val Context.activityManager: ActivityManager
    get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

internal val Context.isSustainedPerformanceModeSupported
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        powerManager?.isSustainedPerformanceModeSupported.booleanAsYesOrNo()
    } else {
        notAvailableString
    }

internal val Context.isInPowerSaveMode
    get() = powerManager?.isPowerSaveMode.booleanAsYesOrNo()

internal val Context.isInInteractiveState
    get() = powerManager?.isInteractive.booleanAsYesOrNo()

internal val Context.isIgnoringBatteryOptimization
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager?.isIgnoringBatteryOptimizations(packageName).booleanAsYesOrNo()
    } else {
        notAvailableString
    }

private fun Boolean?.booleanAsYesOrNo() =
        when (this) {
            true -> "Yes"
            false -> "No"
            null -> notAvailableString
        }


/**
 *  THERMAL_STATUS_NONE if device in not under thermal throttling. Value is
 *  THERMAL_STATUS_NONE, THERMAL_STATUS_LIGHT,
 *  THERMAL_STATUS_MODERATE, THERMAL_STATUS_SEVERE, THERMAL_STATUS_CRITICAL, THERMAL_STATUS_EMERGENCY, or THERMAL_STATUS_SHUTDOWN
 */
internal val Context.getThermalStatus: String
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (powerManager?.currentThermalStatus) {
                THERMAL_STATUS_NONE -> "STATUS_NONE"
                THERMAL_STATUS_LIGHT -> "STATUS_LIGHT"
                THERMAL_STATUS_MODERATE -> "STATUS_MODERATE"
                THERMAL_STATUS_SEVERE -> "STATUS_SEVERE"
                THERMAL_STATUS_CRITICAL -> "STATUS_CRITICAL"
                THERMAL_STATUS_EMERGENCY -> "STATUS_EMERGENCY"
                THERMAL_STATUS_SHUTDOWN -> "STATUS_SHUTDOWN"

                else -> notAvailableString
            }
        } else {
            notAvailableString
        }
    }

/**
 * 	Value is LOCATION_MODE_NO_CHANGE, LOCATION_MODE_GPS_DISABLED_WHEN_SCREEN_OFF,
 * 	LOCATION_MODE_ALL_DISABLED_WHEN_SCREEN_OFF,
 * 	LOCATION_MODE_FOREGROUND_ONLY, or LOCATION_MODE_THROTTLE_REQUESTS_WHEN_SCREEN_OFF
 */
internal val Context.locationPowerSaveMode: String
    get() {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            when (powerManager?.locationPowerSaveMode) {
                LOCATION_MODE_NO_CHANGE -> "MODE_NO_CHANGE"
                LOCATION_MODE_GPS_DISABLED_WHEN_SCREEN_OFF -> "MODE_GPS_DISABLED_WHEN_SCREEN_OFF"
                LOCATION_MODE_ALL_DISABLED_WHEN_SCREEN_OFF -> "MODE_ALL_DISABLED_WHEN_SCREEN_OFF"
                LOCATION_MODE_FOREGROUND_ONLY -> "MODE_FOREGROUND_ONLY"
                LOCATION_MODE_THROTTLE_REQUESTS_WHEN_SCREEN_OFF -> "MODE_THROTTLE_REQUESTS_WHEN_SCREEN_OFF"
                else -> notAvailableString
            }
        } else {
            notAvailableString
        }
    }

internal val Context.isDeviceIdle
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager?.isDeviceIdleMode.booleanAsYesOrNo()
    } else {
        notAvailableString
    }


private fun buildExitReason(reason: Int) = when (reason) {
    REASON_ANR -> "ANR"
    REASON_CRASH -> "CRASH"
    REASON_CRASH_NATIVE -> "CRASH_NATIVE"
    REASON_DEPENDENCY_DIED -> "DEPENDENCY_DIED"
    REASON_EXCESSIVE_RESOURCE_USAGE -> "EXCESSIVE_RESOURCE_USAGE"
    REASON_EXIT_SELF -> "EXIT_SELF"
    REASON_INITIALIZATION_FAILURE -> "INITIALIZATION_FAILURE"
    REASON_LOW_MEMORY -> "LOW_MEMORY"
    REASON_OTHER -> "OTHER"
    REASON_PERMISSION_CHANGE -> "PERMISSION_CHANGE"
    REASON_SIGNALED -> "SIGNALED"
    REASON_USER_REQUESTED -> "USER_REQUESTED"
    REASON_USER_STOPPED -> "USER_STOPPED"
    android.app.ApplicationExitInfo.REASON_UNKNOWN -> "UNKNOWN"
    else -> notAvailableString
}

internal fun Context.getExitReasons(pid: Int = 0, maxRes: Int = 1) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activityManager.getHistoricalProcessExitReasons(packageName, pid, maxRes).mapIndexed { index, it ->
                "~~~~~~~~~~~ Exit reason #${index+1} ~~~~~~~~~~~\n" +
                        "\n" +
                        "Description: ${it.description}\n" +
                        "Importance: ${buildImportance(it.importance)}\n" +
                        "Reason: ${buildExitReason(it.reason)}\n" +
                        "Timestamp: ${CrashyReporter.dateFormat.format(Date(it.timestamp))}\n" +
                        "\n" +
                        "~~~~~~~~~~~ END of exit reason #${index+1} ~~~~~~~~~~~" +
                        "\n" +
                        "\n"
            }
        } else {
            emptyList()
        }

fun buildImportance(importance: Int): String {
    return when(importance){
        IMPORTANCE_FOREGROUND-> "FOREGROUND"
        IMPORTANCE_FOREGROUND_SERVICE-> "FOREGROUND_SERVICE"
        IMPORTANCE_TOP_SLEEPING-> "TOP_SLEEPING"
        IMPORTANCE_VISIBLE-> "VISIBLE"
        IMPORTANCE_PERCEPTIBLE-> "PERCEPTIBLE"
        IMPORTANCE_CANT_SAVE_STATE-> "CANT_SAVE_STATE"
        IMPORTANCE_SERVICE-> "SERVICE"
        IMPORTANCE_CACHED-> "CACHED"
        IMPORTANCE_GONE -> "GONE"
        else-> notAvailableString
    }
}

internal const val notAvailableString = "N/A"

internal fun <T> Collection<T>?.notAvailableIfNullNewLine(): String = if (this.isNullOrEmpty()) "N/A" else "\n${this}"
internal fun <T> Collection<T>?.notAvailableIfNull(): String = if (this.isNullOrEmpty()) "N/A" else "$this"
