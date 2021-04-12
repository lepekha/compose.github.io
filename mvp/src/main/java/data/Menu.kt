package data


interface Menu{
    var isEnabled: Boolean
    var iconTintRes: Int?
}

class BottomMenu(var iconResId: Int, var id: Int = -1, val onPress: () -> Unit): Menu {
    override var isEnabled: Boolean = true
    override var iconTintRes: Int? = null
}