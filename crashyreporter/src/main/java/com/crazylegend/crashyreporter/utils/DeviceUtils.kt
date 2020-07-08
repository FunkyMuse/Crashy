package com.crazylegend.crashyreporter.utils

import android.Manifest
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.crazylegend.crashyreporter.CrashyReporter
import com.crazylegend.crashyreporter.extensions.*
import java.util.*


/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
internal object DeviceUtils {



    fun getDeviceDetails(context: Context) =
            "----------- Device info -----------\n" +
                    "\n" +
                    "Device ID: ${getDeviceID(context)}\n" +
                    "Application version: ${getAppVersion(context)}\n" +
                    "Default launcher: ${getLaunchedFromApp(context)}\n" +
                    "Timezone name: ${TimeZone.getDefault().displayName}\n" +
                    "Timezone ID: ${TimeZone.getDefault().id}\n" +
                    "Version release: ${Build.VERSION.RELEASE}\n" +
                    "Version incremental : ${Build.VERSION.INCREMENTAL}\n" +
                    "Version SDK: ${Build.VERSION.SDK_INT}\n" +
                    "Board: ${Build.BOARD}\n" +
                    "Bootloader: ${Build.BOOTLOADER}\n" +
                    "Brand: ${Build.BRAND}\n" +
                    "CPU_ABIS_32: ${Build.SUPPORTED_32_BIT_ABIS.map { it }.notAvailableIfNull()}\n" +
                    "CPU_ABIS_64: ${Build.SUPPORTED_64_BIT_ABIS.map { it }.notAvailableIfNull()}\n" +
                    "Supported ABIS: ${Build.SUPPORTED_ABIS.map { it }.notAvailableIfNull()}\n" +
                    "Device: ${Build.DEVICE}\n" +
                    "Display: ${Build.DISPLAY}\n" +
                    "Fingerprint: ${Build.FINGERPRINT}\n" +
                    "Hardware: ${Build.HARDWARE}\n" +
                    "Host: ${Build.HOST}\n" +
                    "ID: ${Build.ID}\n" +
                    "Manufacturer: ${Build.MANUFACTURER}\n" +
                    "Product: ${Build.PRODUCT}\n" +
                    "Build time: ${Build.TIME}\n" +
                    "Build time formatted: ${CrashyReporter.dateFormat.format(Date(Build.TIME))}\n" +
                    "Type: ${Build.TYPE}\n" +
                    "Radio: ${getRadioVersion()}\n" +
                    "Tags: ${Build.TAGS}\n" +
                    "User: ${Build.USER}\n" +
                    "User IDs: ${getUserPlayIDs(context).notAvailableIfNull()}\n" +
                    "Is sustained performance mode supported: ${context.isSustainedPerformanceModeSupported}\n" +
                    "Is in power save mode: ${context.isInPowerSaveMode}\n" +
                    "Is in interactive state: ${context.isInInteractiveState}\n" +
                    "Is ignoring battery optimizations: ${context.isIgnoringBatteryOptimization}\n" +
                    "Thermal status: ${context.getThermalStatus}\n" +
                    "Location power save mode: ${context.locationPowerSaveMode}\n" +
                    "Is device idle: ${context.isDeviceIdle}\n" +
                    "\n" +
                    "----------- END of Device info -----------" +
                    "\n" +
                    "\n" +
                    "*********** Exit reasons ***********\n" +
                    "${context.getExitReasons(maxRes = 3).notAvailableIfNullNewLine().replace("[", "").replace("]", "").replace(",", "\n")}\n" +
                    "*********** END of exit reasons ***********" +
                    "\n" +
                    "\n"




    @SuppressLint("MissingPermission")
    private fun getUserPlayIDs(context: Context): List<String?> {
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            (context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager).accounts.map {
                if (it.type.equals("com.google", true)){
                    it.name
                } else {
                    null
                }
            }
        } else {
            emptyList()
        }
    }


    private fun getRadioVersion() = try {
        Build.getRadioVersion()
    } catch (e:java.lang.Exception){
        null
    }

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
            "^^^^^^^^^^^ Currently running foreground/background processes ^^^^^^^^^^^\n" +
                    "\n" +
                    "${context.getRunningProcesses()}\n" +
                    "\n" +
                    "^^^^^^^^^^^ END of running foreground/background processes info ^^^^^^^^^^^"


}