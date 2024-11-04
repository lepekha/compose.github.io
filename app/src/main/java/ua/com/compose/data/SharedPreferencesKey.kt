package ua.com.compose.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object SharedPreferencesKey {
    const val KEY_PANEL_ID = "KEY_PANEL_ID"
    const val KEY_CREATE_COLOR_TYPE = "KEY_CREATE_COLOR_TYPE"
    const val KEY_COLOR_TYPE = "KEY_COLOR_TYPE"
    const val KEY_THEME = "KEY_THEME"
    const val KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE = "KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE"
    const val KEY_PALLET_ID = "KEY_PALLET_ID"
    const val KEY_PALLET_NUMBER = "KEY_PALLET_NUMBER"
    const val KEY_VIBRATION = "KEY_VIBRATION"
    const val KEY_LAST_COLOR = "KEY_LAST_COLOR"
    const val KEY_START_SCREEN = "KEY_START_SCREEN"
    const val KEY_SORT_TYPE = "KEY_SORT_TYPE"
    const val KEY_SORT_DIRECTION = "KEY_SORT_DIRECTION"
    const val KEY_OPEN_APP_COUNT = "KEY_OPEN_APP_COUNT"
    const val KEY_UPDATE_VERSION = "KEY_UPDATE_VERSION"
}

object DataStoreKey {
    val KEY_VIBRATION = booleanPreferencesKey("KEY_VIBRATION")
    val KEY_PREMIUM = booleanPreferencesKey("KEY_PREMIUM")
    val KEY_START_SCREEN = intPreferencesKey("KEY_START_SCREEN")
    val KEY_THEME = intPreferencesKey("KEY_THEME")
    val KEY_CREATE_COLOR_TYPE = intPreferencesKey("KEY_CREATE_COLOR_TYPE")
    val KEY_COLOR_TYPE = intPreferencesKey("KEY_COLOR_TYPE")
    val KEY_COLOR_NAME = intPreferencesKey("KEY_COLOR_NAME")
    val KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE = intPreferencesKey("KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE")
    val KEY_SORT_TYPE = intPreferencesKey("KEY_SORT_TYPE")
    val KEY_COLOR_WHEEL_SCHEME = intPreferencesKey("KEY_COLOR_WHEEL_SCHEME")
    val KEY_SORT_DIRECTION = intPreferencesKey("KEY_SORT_DIRECTION")
    val KEY_OPEN_APP_COUNT = intPreferencesKey("KEY_OPEN_APP_COUNT")
    val KEY_UPDATE_VERSION = stringPreferencesKey("KEY_UPDATE_VERSION")
    val KEY_LAST_COLOR = intPreferencesKey("KEY_LAST_COLOR")
    val KEY_PALLET_NUMBER = intPreferencesKey("KEY_PALLET_NUMBER")
}