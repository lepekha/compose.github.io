package ua.com.compose.data

import androidx.datastore.preferences.core.booleanPreferencesKey

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
    val KEY_PREMIUM = booleanPreferencesKey("KEY_PREMIUM")
}