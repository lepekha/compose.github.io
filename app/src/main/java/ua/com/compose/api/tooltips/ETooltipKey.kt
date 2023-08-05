package ua.com.compose.api.tooltips

import ua.com.compose.extension.prefs
import ua.com.compose.extension.put

enum class ETooltipKey(private val key: String) {
    PALETTE_DRAG_AND_DROP(key = "PALETTE_DRAG_AND_DROP") {
        override fun isShow(): Boolean {
            return prefs.getBoolean(PALETTE_DRAG_AND_DROP.key, true)
        }
        override fun confirm() {
            prefs.put(PALETTE_DRAG_AND_DROP.key, false)
        }
    },
    COLOR_INFO_LONG_PRESS(key = "COLOR_INFO_LONG_PRESS") {
        override fun isShow(): Boolean {
            return prefs.getBoolean(COLOR_INFO_LONG_PRESS.key, true)
        }
        override fun confirm() {
            prefs.put(COLOR_INFO_LONG_PRESS.key, false)
        }
    };

    abstract fun isShow(): Boolean
    abstract fun confirm()
}