package ua.com.compose.colors.data

sealed class Color {
    abstract fun name(): String
    abstract val intColor: Int
}

