package ua.com.compose.extension

import android.content.SharedPreferences

fun <T> SharedPreferences.get(key: String, defaultValue: T): T {
    when(defaultValue) {
        is Boolean -> return this.getBoolean(key, defaultValue as Boolean) as T
        is Float -> return this.getFloat(key, defaultValue as Float) as T
        is Double -> return java.lang.Double.longBitsToDouble(this.getLong(key, java.lang.Double.doubleToRawLongBits(defaultValue as Double))) as T
        is Int -> return this.getInt(key, defaultValue as Int) as T
        is Long -> return this.getLong(key, defaultValue as Long) as T
        is String -> return this.getString(key, defaultValue as String) as T
        is List<*> -> return this.getListOfString(key, defaultValue as List<String>) as T
        is Set<*> -> return this.getStringSet(key, defaultValue as Set<String>) as T
    }
    return defaultValue
}

fun <T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()

    when(value) {
        is Boolean -> editor.putBoolean(key, value as Boolean)
        is Float -> editor.putFloat(key, value as Float)
        is Double -> editor.putLong(key, java.lang.Double.doubleToRawLongBits(value as Double))
        is Int -> editor.putInt(key, value as Int)
        is Long -> editor.putLong(key, value as Long)
        is String -> editor.putString(key, value as String)
        is List<*> -> editor.putListOfString(key, value as List<String>)
        is Set<*> -> editor.putStringSet(key, value as Set<String>)
    }

    editor.apply()
}

fun SharedPreferences.has(key: String) = this.contains(key)

fun SharedPreferences.getListOfString(key: String, defaultValue: List<String>): List<String> {
    val s = this.getString(key, "")
    val favs = s?.takeIf { it.isNotEmpty() }?.split("\\|".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray() ?: arrayOf()
    return if (favs.isEmpty()) {
        defaultValue
    } else {
        listOf(*favs)
    }
}

fun SharedPreferences.Editor.putListOfString(key: String, value: List<String>) {
    val array = value.toTypedArray()
    for (i in array.indices)
        array[i] = array[i].replace('|', '/')

    val s = array.joinToString("|")
    this.putString(key, s)
}

fun SharedPreferences.remove(key: String) {
    val editor = this.edit()
    editor.remove(key)
    editor.apply()
}

fun SharedPreferences.copyTo(dest: SharedPreferences) = with(dest.edit()) {
    for (entry in all.entries) {
        val value = entry.value ?: continue
        val key = entry.key
        dest.put(key, value)
    }
    apply()
}

fun SharedPreferences.clearAll() {
    this.edit().clear().apply()
}