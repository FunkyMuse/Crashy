package com.crazylegend.crashyreporter.utils

import android.content.Context
import com.crazylegend.crashyreporter.CrashyReporter
import com.crazylegend.crashyreporter.extensions.*
import java.util.*


/**
 * Created by crazy on 7/19/20 to long live and prosper !
 */

object ApplicationUtils {

    internal fun appendApplicationInfo(context: Context): String {
        return "*********** Application info ***********\n" +
                "\n" +
                "App name: ${context.appName.notAvailableIfNull()}\n" +
                "Version code: ${context.getVersionCodeCompat()}\n" +
                "Version name: ${context.getVersionName().notAvailableIfNull()}\n" +
                "Package name: ${context.applicationInfo.packageName.notAvailableIfNull()}\n" +
                "Short package name: ${context.shortAppName.notAvailableIfNull()}\n" +
                "Flavor: ${context.flavor.notAvailableIfNull()}\n" +
                "Signatures: ${context.apkSignatures.mapWithoutNewLine().notAvailableIfNull()}\n" +
                "Is debuggable: ${context.isDebuggable().asYesOrNo()}\n" +
                "First installed: ${CrashyReporter.dateFormat.format(Date(context.getFirstInstallTime))}\n" +
                "Default prefs: ${SharedPreferencesUtil.collect(context).notAvailableIfNull()}\n" +
                "\n" +
                "*********** END of Application info ***********" +
                "\n" +
                "\n"
    }
}