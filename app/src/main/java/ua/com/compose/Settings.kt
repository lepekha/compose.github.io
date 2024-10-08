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
import ua.com.compose.data.enums.EColorSchemeType

object Settings {

    var lastUri: Uri? = null

    object updateAppVersion {
        val value: String
            get() = runBlocking {
                dataStore.data.map { preferences ->
                    preferences[DataStoreKey.KEY_UPDATE_VERSION] ?: prefs.get(key = SharedPreferencesKey.KEY_UPDATE_VERSION, defaultValue = "")
                }.first()
            }

        fun update(value: String) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_UPDATE_VERSION] = value
            }
        }
    }

    object openAppCount {
        val value: Int
            get() = runBlocking {
                dataStore.data.map { preferences ->
                    preferences[DataStoreKey.KEY_OPEN_APP_COUNT] ?: prefs.get(key = SharedPreferencesKey.KEY_OPEN_APP_COUNT, defaultValue = 1)
                }.first()
            }

        fun update(value: Int) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_OPEN_APP_COUNT] = value
            }
        }
    }

    object vibration {
        val flow: Flow<Boolean>
            get() = dataStore.data.map { preferences ->
                preferences[DataStoreKey.KEY_VIBRATION] ?: prefs.get(key = SharedPreferencesKey.KEY_VIBRATION, defaultValue = true)
            }

        val value: Boolean
            get() = runBlocking {
                flow.first()
            }

        fun update(value: Boolean) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_VIBRATION] = value
            }
        }
    }

    object colorType {
        val flow: Flow<EColorType>
            get() = dataStore.data.map { preferences ->
                EColorType.getByKey(preferences[DataStoreKey.KEY_COLOR_TYPE] ?: prefs.get(key = SharedPreferencesKey.KEY_COLOR_TYPE, defaultValue = EColorType.HEX.key))
            }


        val value: EColorType
            get() = runBlocking {
                flow.first()
            }

        fun update(value: EColorType) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_COLOR_TYPE] = value.key
            }
        }
    }

    object colorWheelScheme {
        val flow: Flow<EColorSchemeType>
            get() = dataStore.data.map { preferences ->
                EColorSchemeType.valueByKey(preferences[DataStoreKey.KEY_COLOR_WHEEL_SCHEME] ?: EColorSchemeType.SCHEME_0.key)
            }

        val value: EColorSchemeType
            get() = runBlocking {
                flow.first()
            }

        fun update(value: EColorSchemeType) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_COLOR_WHEEL_SCHEME] = value.key
            }
        }
    }

    object sortType {
        val flow: Flow<ESortType>
            get() = dataStore.data.map { preferences ->
                ESortType.valueByKey(preferences[DataStoreKey.KEY_SORT_TYPE] ?: prefs.get(key = SharedPreferencesKey.KEY_SORT_TYPE, defaultValue = ESortType.ORDER.key))
            }

        val value: ESortType
            get() = runBlocking {
                flow.first()
            }

        fun update(value: ESortType) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_SORT_TYPE] = value.key
            }
        }
    }

    object sortDirection {
        val flow: Flow<ESortDirection>
            get() = dataStore.data.map { preferences ->
                ESortDirection.valueByKey(preferences[DataStoreKey.KEY_SORT_DIRECTION] ?: prefs.get(key = SharedPreferencesKey.KEY_SORT_DIRECTION, defaultValue = ESortDirection.DESC.key))
            }

        val value: ESortDirection
            get() = runBlocking {
                flow.first()
            }

        fun update(value: ESortDirection) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_SORT_DIRECTION] = value.key
            }
        }
    }

    object dialogColorPick {
        object createType {
            val flow: Flow<ECreateColorType>
                get() = dataStore.data.map { preferences ->
                    ECreateColorType.getByKey(preferences[DataStoreKey.KEY_CREATE_COLOR_TYPE] ?: prefs.get(key = SharedPreferencesKey.KEY_CREATE_COLOR_TYPE, defaultValue = ECreateColorType.HSV.key))
                }

            val value: ECreateColorType
                get() = runBlocking {
                    flow.first()
                }

            fun update(value: ECreateColorType) = runBlocking {
                dataStore.edit { settings ->
                    settings[DataStoreKey.KEY_CREATE_COLOR_TYPE] = value.key
                }
            }
        }

        object inputColorType {
            val flow: Flow<EColorType>
                get() = dataStore.data.map { preferences ->
                    EColorType.getByKey(preferences[DataStoreKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE] ?: prefs.get(key = SharedPreferencesKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE, defaultValue = EColorType.HEX.key))
                }

            val value: EColorType
                get() = runBlocking {
                    flow.first()
                }

            fun update(value: EColorType) = runBlocking {
                dataStore.edit { settings ->
                    settings[DataStoreKey.KEY_DIALOG_COLOR_PICK_INPUT_COLOR_TYPE] = value.key
                }
            }
        }
    }

    object theme {
        val flow: Flow<ETheme>
            get() = dataStore.data.map { preferences ->
                ETheme.getByKey(preferences[DataStoreKey.KEY_THEME] ?: prefs.get(key = SharedPreferencesKey.KEY_THEME, defaultValue = ETheme.SYSTEM.key))
            }

        val value: ETheme
            get() = runBlocking {
                flow.first()
            }

        fun update(value: ETheme) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_THEME] = value.key
            }
        }
    }

    object startScreen {
        val flow: Flow<EPanel>
            get() = dataStore.data.map { preferences ->
                EPanel.valueOfKey(preferences[DataStoreKey.KEY_START_SCREEN] ?: prefs.get(key = SharedPreferencesKey.KEY_START_SCREEN, defaultValue = EPanel.PALLETS.id))
            }

        val value: EPanel
            get() = runBlocking {
                flow.first()
            }

        fun update(value: EPanel) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_START_SCREEN] = value.id
            }
        }

        fun isKeyStored(): Boolean = runBlocking {
            dataStore.data.map { preferences ->
                preferences.contains(DataStoreKey.KEY_START_SCREEN)
            }.first()
        }
    }

    fun firstOpen() = startScreen.isKeyStored()

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

    object lastColor {
        val flow: Flow<ua.com.compose.colors.data.IColor>
            get() = dataStore.data.map { preferences ->
                colorINTOf(preferences[DataStoreKey.KEY_LAST_COLOR] ?: prefs.get(key = SharedPreferencesKey.KEY_LAST_COLOR, defaultValue = Color.parseColor("#2EAAB4")))
            }

        val value: ua.com.compose.colors.data.IColor
            get() = runBlocking {
                flow.first()
            }

        fun update(value: ua.com.compose.colors.data.IColor) = runBlocking {
            dataStore.edit { settings ->
                settings[DataStoreKey.KEY_LAST_COLOR] = value.intColor
            }
        }
    }
}