package ua.com.compose

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ua.com.compose.data.DataStoreKey
import ua.com.compose.data.enums.EColorType
import ua.com.compose.data.enums.ECreateColorType
import ua.com.compose.data.enums.ETheme
import ua.com.compose.data.SharedPreferencesKey
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.data.enums.EPanel
import ua.com.compose.data.enums.ESortDirection
import ua.com.compose.data.enums.ESortType
import ua.com.compose.extension.dataStore
import ua.com.compose.extension.has
import ua.com.compose.colors.colorINTOf

object Settings {

    var lastUri: Uri? = null

    fun updateVersion() = runBlocking {
        dataStore.data.map { preferences ->
            preferences[DataStoreKey.KEY_UPDATE_VERSION] ?: prefs.get(key = SharedPreferencesKey.KEY_UPDATE_VERSION, defaultValue = "")
        }.first()
    }

    fun updateVersion(value: String) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_UPDATE_VERSION] = value
        }
    }

    fun openAppCount() = runBlocking {
        dataStore.data.map { preferences ->
            preferences[DataStoreKey.KEY_OPEN_APP_COUNT] ?: prefs.get(key = SharedPreferencesKey.KEY_OPEN_APP_COUNT, defaultValue = 1)
        }.first()
    }

    fun updateOpenAppCount(value: Int) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_OPEN_APP_COUNT] = value
        }
    }

    fun vibrationFlow(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DataStoreKey.KEY_VIBRATION] ?: prefs.get(key = SharedPreferencesKey.KEY_VIBRATION, defaultValue = true)
    }

    fun vibrationValue() = runBlocking {
        vibrationFlow().first()
    }

    fun updateVibration(value: Boolean) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_VIBRATION] = value
        }
    }

    fun colorType(): Flow<EColorType> = dataStore.data.map { preferences ->
        EColorType.getByKey(preferences[DataStoreKey.KEY_COLOR_TYPE] ?: prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key))
    }

    fun colorTypeValue() = runBlocking {
        colorType().first()
    }

    fun updateColorType(value: EColorType) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_COLOR_TYPE] = value.key
        }
    }

    fun sortType(): Flow<ESortType> = dataStore.data.map { preferences ->
        ESortType.valueByKey(preferences[DataStoreKey.KEY_SORT_TYPE] ?: prefs.get(key = SharedPreferencesKey.KEY_SORT_TYPE, defaultValue = ESortType.ORDER.key))
    }

    fun updateSortType(value: ESortType) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_SORT_TYPE] = value.key
        }
    }

    fun sortDirection(): Flow<ESortDirection> = dataStore.data.map { preferences ->
        ESortDirection.valueByKey(preferences[DataStoreKey.KEY_SORT_DIRECTION] ?: prefs.get(key = SharedPreferencesKey.KEY_SORT_DIRECTION, defaultValue = ESortDirection.DESC.key))
    }

    fun updateSortDirection(value: ESortDirection) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_SORT_DIRECTION] = value.key
        }
    }

    fun dialogColorPickTypeFlow(): Flow<ECreateColorType> = dataStore.data.map { preferences ->
        ECreateColorType.getByKey(preferences[DataStoreKey.KEY_CREATE_COLOR_TYPE] ?: prefs.get(key = SharedPreferencesKey.KEY_CREATE_COLOR_TYPE, defaultValue = ECreateColorType.BOX.key))
    }

    fun dialogColorPickTypeValue() = runBlocking {
        dialogColorPickTypeFlow().first()
    }

    fun updateDialogColorPickType(value: ECreateColorType) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_CREATE_COLOR_TYPE] = value.key
        }
    }

    fun theme(): Flow<ETheme> = dataStore.data.map { preferences ->
        ETheme.getByKey(preferences[DataStoreKey.KEY_THEME] ?: prefs.get(key = SharedPreferencesKey.KEY_THEME, defaultValue = ETheme.SYSTEM.key))
    }

    fun updateTheme(value: ETheme) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_THEME] = value.key
        }
    }

    fun firstOpen() = !prefs.has(SharedPreferencesKey.KEY_START_SCREEN)

    fun startScreen(): Flow<EPanel> = dataStore.data.map { preferences ->
        EPanel.valueOfKey(preferences[DataStoreKey.KEY_START_SCREEN] ?: prefs.get(key = SharedPreferencesKey.KEY_START_SCREEN, defaultValue = EPanel.PALLETS.id))
    }

    fun updateStartScreen(value: EPanel) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_START_SCREEN] = value.id
        }
    }

    fun colorInputTypeFlow(): Flow<EColorType> = dataStore.data.map { preferences ->
        EColorType.getByKey(preferences[DataStoreKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE] ?: prefs.get(key = SharedPreferencesKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE, defaultValue = EColorType.HEX.key))
    }

    fun colorInputTypeValue() = runBlocking {
        colorInputTypeFlow().first()
    }

    fun updateColorInputType(value: EColorType) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE] = value.key
        }
    }

    fun defaultPaletteName(context: Context, withIncrement: Boolean = true): String = runBlocking {
        val number = dataStore.data.map { preferences ->
            preferences[DataStoreKey.KEY_PALLET_NUMBER] ?: prefs.get(key = SharedPreferencesKey.KEY_PALLET_NUMBER, defaultValue = 1)
        }.first()

        if(withIncrement) {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_PALLET_NUMBER] = number + 1
            }
        }
        context.getString(R.string.color_pick_pallet) + "_" + number
    }

    fun lastColor(): Flow<ua.com.compose.colors.data.Color> = dataStore.data.map { preferences ->
        colorINTOf(preferences[DataStoreKey.KEY_LAST_COLOR] ?: prefs.get(key = SharedPreferencesKey.KEY_LAST_COLOR, defaultValue = Color.parseColor("#2EAAB4")))
    }

    fun updateLastColor(value: ua.com.compose.colors.data.Color) = runBlocking {
        dataStore.edit { settings ->
            settings[DataStoreKey.KEY_LAST_COLOR] = value.intColor
        }
    }
}