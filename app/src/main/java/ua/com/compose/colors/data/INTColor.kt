package ua.com.compose.colors.data

data class INTColor internal constructor(val int: Int): IColor {

    override fun name() = "INTEGER"

    override fun toString(): String {
        return int.toString()
    }

    override val intColor: Int by lazy {
        int
    }
}
