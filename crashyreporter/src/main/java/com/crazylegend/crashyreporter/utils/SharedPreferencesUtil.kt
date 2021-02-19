package com.crazylegend.crashyreporter.utils

import android.content.Context
import android.preference.PreferenceManager


/**
 * Created by crazy on 7/20/20 to long live and prosper !
 */
internal object SharedPreferencesUtil {

    fun collect(context: Context) =
            PreferenceManager.getDefaultSharedPreferences(context).all.iterator().asSequence().map {
                val key = it.key
                val value = it.value
                "$key = $value"
            }.toList().joinToString()
}