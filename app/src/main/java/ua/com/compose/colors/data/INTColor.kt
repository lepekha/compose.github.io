package ua.com.compose.colors.data

data class INTColor(val int: Int): Color() {

    override fun name() = "INTEGER"

    override fun toString(): String {
        return int.toString()
    }

    override val intColor: Int by lazy {
        int
    }
}
