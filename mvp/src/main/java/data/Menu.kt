package data

import android.graphics.drawable.Drawable

interface Menu{
    var isEnabled: Boolean
}

class BottomMenu(var iconResId: Int, var id: Int = -1, val onPress: () -> Unit): Menu {
    override var isEnabled: Boolean = true
}