package ua.com.compose

import android.content.Context
import ua.com.compose.data.ColorPallet
import ua.com.compose.data.EColorType
import ua.com.compose.data.SharedPreferencesKey
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put

object Settings {

    var colorType: EColorType
        get() {
            return EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key)).takeIf { it.isVisible() } ?: EColorType.HEX
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_COLOR_TYPE, value = value.key)
        }

    var dialogColorInputType: EColorType
        get() {
            return EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE, defaultValue = EColorType.HEX.key)).takeIf { it.isVisible() } ?: EColorType.HEX
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE, value = value.key)
        }

    var paletteID: Long
        get() {
            return prefs.get(key = SharedPreferencesKey.KEY_PALLET_ID, defaultValue = ColorPallet.DEFAULT_ID)
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_PALLET_ID, value = value)
        }

    fun defaultPaletteName(context: Context, withIncrement: Boolean = true): String {
        val number = prefs.get(key = SharedPreferencesKey.KEY_PALLET_NUMBER, defaultValue = 1)
        if(withIncrement) {
            prefs.put(key = SharedPreferencesKey.KEY_PALLET_NUMBER, value = (number + 1))
        }
        return context.getString(R.string.module_other_color_pick_pallet) + "_" + number
    }
}