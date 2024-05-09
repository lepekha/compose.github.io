package ua.com.compose.api.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson


open class RemoteConfigSetup {

    private val gson = Gson()

    private val config = FirebaseRemoteConfig.getInstance().apply {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .build()
        setConfigSettingsAsync(configSettings)
        fetchAndActivate()
    }

    inline fun <reified T> read(param: ConfigParam): T? = read(param, T::class.java)

    fun <T> read(param: ConfigParam, returnType: Class<T>): T? {
        val value: Any? = when (returnType) {
            String::class.java -> config.getString(param.key)
            Boolean::class.java -> config.getBoolean(param.key)
            Long::class.java -> config.getLong(param.key)
            Int::class.java -> config.getLong(param.key).toInt()
            Double::class.java -> config.getDouble(param.key)
            Float::class.java -> config.getDouble(param.key).toFloat()
            else -> {
                val json = config.getString(param.key)
                json.takeIf { it.isNotBlank() }?.let { gson.jsonToObjectOrNull(json, returnType) }
            }
        }
        @Suppress("UNCHECKED_CAST")
        return (value as? T)
    }
}

fun <T> Gson.jsonToObjectOrNull(json: String?, clazz: Class<T>): T? =
        try {
            fromJson(json, clazz)
        } catch (ignored: Exception) {
            null
        }