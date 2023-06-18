package ua.com.compose.other_color_pick.main

import android.content.Context
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.data.SharedPreferencesKey

fun Context.defaultPaletteName(withIncrement: Boolean = true): String {
    var number = prefs.get(key = SharedPreferencesKey.KEY_PALLET_NUMBER, defaultValue = 0)
    if(withIncrement) {
        prefs.put(key = SharedPreferencesKey.KEY_PALLET_NUMBER, value = ++number)
    }
    return this.getString(R.string.module_other_color_pick_pallet) + " " + number
}