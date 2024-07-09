package ua.com.compose.colors.data

import ua.com.compose.colors.HEXtoINT


data class HEXColor(val hex: String): Color() {

    init {
        HEXtoINT(hex)
    }

    override fun name() = "HEX"

    override fun toString(): String {
        return hex
    }

    override val intColor: Int by lazy {
        HEXtoINT(hex)
    }
}