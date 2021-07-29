package com.crazylegend.crashyreporter.utils

import android.Manifest
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.crazylegend.crashyreporter.CrashyReporter
import com.crazylegend.crashyreporter.extensions.*
import com.crazylegend.crashyreporter.utils.ApplicationUtils.appendApplicationInfo
import java.util.*


/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
internal object DeviceUtils {

    fun getDeviceDetails(context: Context): String {

        return "`` Device info ``$NEW_ROW" +
                NEW_ROW +
                "Report ID: ${UUID.randomUUID()}" +
                NEW_ROW +
                "Device ID: ${getDeviceID(context)}$NEW_ROW" +
                "Application version: ${getAppVersion(context)}$NEW_ROW" +
                "Default launcher: ${getLaunchedFromApp(context)}$NEW_ROW" +
                "Timezone name: ${TimeZone.getDefault().displayName}$NEW_ROW" +
                "Timezone ID: ${TimeZone.getDefault().id}$NEW_ROW" +
                "Version release: ${Build.VERSION.RELEASE}$NEW_ROW" +
                "Version incremental : ${Build.VERSION.INCREMENTAL}$NEW_ROW" +
                "Version SDK: ${Build.VERSION.SDK_INT}$NEW_ROW" +
                "Board: ${Build.BOARD}$NEW_ROW" +
                "Bootloader: ${Build.BOOTLOADER}$NEW_ROW" +
                "Brand: ${Build.BRAND}$NEW_ROW" +
                "CPU ABIS 32: ${Build.SUPPORTED_32_BIT_ABIS.joinToString { it }.notAvailableIfNull()}$NEW_ROW" +
                "CPU ABIS 64: ${Build.SUPPORTED_64_BIT_ABIS.joinToString { it }.notAvailableIfNull()}$NEW_ROW" +
                "Supported ABIS: ${Build.SUPPORTED_ABIS.joinToString { it }.notAvailableIfNull()}$NEW_ROW" +
                "Device: ${Build.DEVICE}$NEW_ROW" +
                "Display: ${Build.DISPLAY}$NEW_ROW" +
                "Fingerprint: ${Build.FINGERPRINT}$NEW_ROW" +
                "Hardware: ${Build.HARDWARE}$NEW_ROW" +
                "Host: ${Build.HOST}$NEW_ROW" +
                "ID: ${Build.ID}$NEW_ROW" +
                "Manufacturer: ${Build.MANUFACTURER}$NEW_ROW" +
                "Product: ${Build.PRODUCT}$NEW_ROW" +
                "Build time: ${Build.TIME}$NEW_ROW" +
                "Build time formatted: ${CrashyReporter.dateFormat.format(Date(Build.TIME))}$NEW_ROW" +
                "Type: ${Build.TYPE}$NEW_ROW" +
                "Radio: ${getRadioVersion()}$NEW_ROW" +
                "Tags: ${Build.TAGS}$NEW_ROW" +
                "User: ${Build.USER}$NEW_ROW" +
                "User IDs: ${getUserPlayIDs(context).notAvailableIfNull()}$NEW_ROW" +
                "Is sustained performance mode supported: ${context.isSustainedPerformanceModeSupported}$NEW_ROW" +
                "Is in power save mode: ${context.isInPowerSaveMode}$NEW_ROW" +
                "Is in interactive state: ${context.isInInteractiveState}$NEW_ROW" +
                "Is ignoring battery optimizations: ${context.isIgnoringBatteryOptimization}$NEW_ROW" +
                "Thermal status: ${context.getThermalStatus}$NEW_ROW" +
                "Location power save mode: ${context.locationPowerSaveMode}$NEW_ROW" +
                "Is device idle: ${context.isDeviceIdle}$NEW_ROW" +
                "Battery percentage: ${context.getBatteryPercentage}$NEW_ROW" +
                "Battery remaining time: ${getChargeRemainingTime(context)}$NEW_ROW" +
                "Is battery charging: ${context.isBatteryCharging.asYesOrNo()}$NEW_ROW" +
                "Is device rooted: ${RootUtils.isDeviceRooted.asYesOrNo()}$NEW_ROW" +
                "CPU Model: ${CPUInfo.getCPUModel().notAvailableIfNull()}$NEW_ROW" +
                "Number of CPU cores: ${CPUInfo.getNumberOfCores()}$NEW_ROW" +
                "Up time with sleep: ${upTimeWithSleep()}$NEW_ROW" +
                "Up time without sleep: ${upTimeWithoutSleep()}$NEW_ROW" +
                NEW_ROW +
                "`` END of Device info ``" +
                NEW_ROW + NEW_ROW +
                appendExitReasons(context) +
                NEW_ROW + NEW_ROW +
                appendApplicationInfo(context)
    }

    private fun getChargeRemainingTime(context: Context): String {
        val chargeRemainingTime = context.getChargeTimeRemaining
        return if (chargeRemainingTime != null) {
            if (chargeRemainingTime == -1L) {
                notAvailableString
            } else {
                CrashyReporter.dateFormat.format(Date(chargeRemainingTime)).notAvailableIfNull()
            }
        } else {
            notAvailableString
        }
    }


    private fun appendExitReasons(context: Context): String {
        return "`` Exit reasons ``$NEW_ROW" + NEW_ROW +
                "${context.getExitReasons(maxRes = 3).notAvailableIfNullNewLine().replace("[", "").replace("]", "").replace(",", NEW_ROW)}$NEW_ROW" +
                NEW_ROW +
                "`` END of exit reasons ``"
    }


    @SuppressLint("MissingPermission")
    private fun getUserPlayIDs(context: Context): List<String?> {
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            (context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager).accounts.map {
                if (it.type.equals("com.google", true)) {
                    it.name
                } else {
                    null
                }
            }
        } else {
            emptyList()
        }
    }

    private fun upTimeWithSleep() = formatMillisToHoursMinutesSeconds(SystemClock.elapsedRealtime())
    private fun upTimeWithoutSleep() = formatMillisToHoursMinutesSeconds(SystemClock.uptimeMillis())

    private fun getRadioVersion() = try {
        Build.getRadioVersion()
    } catch (e: java.lang.Exception) {
        null
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceID(context: Context): String? = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    private fun getAppVersion(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
    } else {
        context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong()
    }

    private fun getLaunchedFromApp(context: Context): String? {
        val packageName: String?
        val localPackageManager = context.packageManager
        val intent = with(Intent("android.intent.action.MAIN")) {
            addCategory("android.intent.category.HOME")
            this
        }
        packageName = try {
            localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName
        } catch (e: Exception) {
            null
        }
        return packageName
    }



    fun getRunningProcesses(context: Context) =
            "`` Currently running foreground/background processes ``$NEW_ROW" +
                    NEW_ROW +
                    "${context.getRunningProcesses()}$NEW_ROW" +
                    NEW_ROW +
                    "`` END of running foreground/background processes info ``"


}



