package ua.com.compose

import android.content.Context
import android.graphics.Color
import android.net.Uri
import ua.com.compose.data.ColorPallet
import ua.com.compose.data.EColorType
import ua.com.compose.data.ECreateColorType
import ua.com.compose.data.EExportType
import ua.com.compose.data.ETheme
import ua.com.compose.data.SharedPreferencesKey
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put
import ua.com.compose.data.EPanel
import ua.com.compose.data.ESortDirection
import ua.com.compose.data.ESortType
import ua.com.compose.extension.has

object Settings {

    var lastUri: Uri? = null

    var updateVersion: String
        get() {
            return prefs.get(key = SharedPreferencesKey.KEY_UPDATE_VERSION, defaultValue = "")
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_UPDATE_VERSION, value = value)
        }

    var openInfoCount: Int
        get() {
            return prefs.get(key = SharedPreferencesKey.KEY_OPEN_APP_COUNT, defaultValue = 1)
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_OPEN_APP_COUNT, value = value)
        }

    var vibration: Boolean
        get() {
            return prefs.get(key = SharedPreferencesKey.KEY_VIBRATION, defaultValue = true)
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_VIBRATION, value = value)
        }

    var colorType: EColorType
        get() {
            return EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key)) ?: EColorType.HEX
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_COLOR_TYPE, value = value.key)
        }

    var sortType: ESortType
        get() {
            return ESortType.valueByKey(prefs.get(key = SharedPreferencesKey.KEY_SORT_TYPE, defaultValue = ESortType.ORDER.key))
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_SORT_TYPE, value = value.key)
        }

    var sortDirection: ESortDirection
        get() {
            return ESortDirection.valueByKey(prefs.get(key = SharedPreferencesKey.KEY_SORT_DIRECTION, defaultValue = ESortDirection.DESC.key))
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_SORT_DIRECTION, value = value.key)
        }

    var createColorType: ECreateColorType
        get() {
            return ECreateColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_CREATE_COLOR_TYPE, defaultValue = ECreateColorType.BOX.key))
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_CREATE_COLOR_TYPE, value = value.key)
        }

    var theme: ETheme
        get() {
            return ETheme.getByKey(prefs.get(key = SharedPreferencesKey.KEY_THEME, defaultValue = ETheme.NIGHT.key))
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_THEME, value = value.key)
        }

    fun firstOpen() = !prefs.has(SharedPreferencesKey.KEY_START_SCREEN)

    var startScreen: EPanel
        get() {
            return EPanel.valueOfKey(prefs.get(key = SharedPreferencesKey.KEY_START_SCREEN, defaultValue = EPanel.PALLETS.id))
        }
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_START_SCREEN, value = value.id)
        }

    var dialogColorInputType: EColorType
        get() {
            return EColorType.getByKey(prefs.get(key = SharedPreferencesKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE, defaultValue = EColorType.HEX.key)) ?: EColorType.HEX
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
        return context.getString(R.string.color_pick_pallet) + "_" + number
    }

    var lastColor: Int = prefs.get(key = SharedPreferencesKey.KEY_LAST_COLOR, defaultValue = Color.parseColor("#2EAAB4"))
        set(value) {
            prefs.put(key = SharedPreferencesKey.KEY_LAST_COLOR, value = value)
            field = value
        }
}