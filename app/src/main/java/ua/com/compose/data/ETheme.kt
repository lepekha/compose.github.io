package ua.com.compose.data

import android.os.Build
import androidx.annotation.StringRes
import ua.com.compose.R

enum class ETheme(val key: Int, @StringRes val strRes: Int) {
    DAY(key = 0, strRes = R.string.color_pick_day),
    NIGHT(key = 1, strRes = R.string.color_pick_night),
    SYSTEM(key = 2, strRes = R.string.color_pick_system),
    DYNAMIC(key = 3, strRes = R.string.color_pick_dynamic);

    companion object {

        fun visibleValues() = mutableListOf<ETheme>().apply {
            this.add(DAY)
            this.add(NIGHT)
            this.add(SYSTEM)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.add(DYNAMIC)
            }
        }
        fun getByKey(key: Int) = values().firstOrNull { it.key == key } ?: SYSTEM
    }
}