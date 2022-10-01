package ua.com.compose.config

import com.google.gson.Gson


open class RemoteConfigSetup {



    inline fun <reified T> read(param: ConfigParam): T? = read(param, T::class.java)

    fun <T> read(param: ConfigParam, returnType: Class<T>): T? {
        val value: Any? = when (returnType) {
            String::class.java -> ""
            Boolean::class.java -> true
            Long::class.java -> 1L
            Int::class.java -> 1
            Double::class.java -> .0
            Float::class.java -> 1f
            else -> {
                ""
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