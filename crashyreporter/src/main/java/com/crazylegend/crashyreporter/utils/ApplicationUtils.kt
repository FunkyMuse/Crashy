package com.crazylegend.crashyreporter.utils

import android.content.Context
import com.crazylegend.crashyreporter.CrashyReporter
import com.crazylegend.crashyreporter.extensions.*
import java.util.*


/**
 * Created by crazy on 7/19/20 to long live and prosper !
 */

internal object ApplicationUtils {

    internal fun appendApplicationInfo(context: Context): String {
        return "`` Application info ``$NEW_ROW" +
                NEW_ROW +
                "App name: ${context.appName.notAvailableIfNull()}$NEW_ROW" +
                "Version code: ${context.getVersionCodeCompat()}$NEW_ROW" +
                "Version name: ${context.getVersionName().notAvailableIfNull()}$NEW_ROW" +
                "Package name: ${context.applicationInfo.packageName.notAvailableIfNull()}$NEW_ROW" +
                "Short package name: ${context.shortAppName.notAvailableIfNull()}$NEW_ROW" +
                "Flavor: ${context.flavor.notAvailableIfNull()}$NEW_ROW" +
                "Signatures: ${context.apkSignatures.joinToString { it }.notAvailableIfNull()}$NEW_ROW" +
                "Is debuggable: ${context.isDebuggable().asYesOrNo()}$NEW_ROW" +
                "First installed: ${CrashyReporter.dateFormat.format(Date(context.getFirstInstallTime))}$NEW_ROW" +
                "Last updated: ${CrashyReporter.dateFormat.format(Date(context.lastUpdateTime))}$NEW_ROW" +
                "Requested permissions: ${context.requestedPermissions?.joinToString { it.toString() }.notAvailableIfNull()}$NEW_ROW" +
                "Default prefs: ${SharedPreferencesUtil.collect(context).notAvailableIfNull()}$NEW_ROW" +
                "Default prefs: ${SharedPreferencesUtil.collect(context).notAvailableIfNull()}$NEW_ROW" +
                NEW_ROW +
                "`` END of Application info ``" +
                NEW_ROW + NEW_ROW
    }
}