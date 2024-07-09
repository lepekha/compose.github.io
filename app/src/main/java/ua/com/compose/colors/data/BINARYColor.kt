package ua.com.compose.colors.data


import ua.com.compose.colors.BYTEToINT
import ua.com.compose.colors.asRGBdecimal


data class BINARYColor(val bytes: ByteArray): Color() {

    override val intColor: Int by lazy {
        BYTEToINT(bytes)
    }

    override fun name() = "BINARY"

    override fun toString(): String {
        val rgb = this.asRGBdecimal()
        return "${rgb.red.toString(2)} ${rgb.green.toString(2)} ${rgb.blue.toString(2)}"
    }
}
