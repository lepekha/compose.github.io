package ua.com.compose.extension

import ua.com.compose.data.db.ColorItem
import ua.com.compose.data.enums.ColorNames
import ua.com.compose.colors.colorINTOf

fun ColorItem.color() = colorINTOf(this.intColor)
fun ColorItem.userColorName() = this.name ?: this.nearestColorName()
fun ColorItem.nearestColorName() = ColorNames.getColorName(this.color())