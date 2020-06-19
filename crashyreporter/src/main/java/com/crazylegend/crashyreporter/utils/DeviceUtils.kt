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
import java.util.*


/**
 * Created by crazy on 6/18/20 to long live and prosper !
 */
internal object DeviceUtils {

    internal data class FingerprintPartition(val name: String, val fingerprint: String, val buildTimeMillis: Long)

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
                    "CPU_ABIS_32: ${Build.SUPPORTED_32_BIT_ABIS.map { it }}\n" +
                    "CPU_ABIS_64: ${Build.SUPPORTED_64_BIT_ABIS.map { it }}\n" +
                    "Supported ABIS: ${Build.SUPPORTED_ABIS.map { it }}\n" +
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
                    "User IDs: ${getUserPlayIDs(context)}\n" +
                    "Build partition name system: ${Build.Partition.PARTITION_NAME_SYSTEM}\n" +
                    "\n" +
                    "----------- END of Device info -----------" +
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
    private fun getAppVersion(context: Context) = context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
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

}