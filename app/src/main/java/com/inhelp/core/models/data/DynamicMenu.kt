package com.inhelp.core.models.data

import android.graphics.Bitmap


sealed class DynamicMenu {
    abstract val id: Int
    abstract val titleResId: Int
    abstract val backgroundColorId: Int
    abstract val onPress: () -> Unit

    class Text(
            override val id: Int,
            override val titleResId: Int,
            override val backgroundColorId: Int,
            override val onPress: () -> Unit,
    ) : DynamicMenu()

    class Short(
            override val id: Int,
            override val titleResId: Int,
            override val backgroundColorId: Int,
            override val onPress: () -> Unit,
            val backgroundImageId: Int
    ) : DynamicMenu()

    class Medium(override val id: Int,
                 override val titleResId: Int,
                 override val backgroundColorId: Int,
                 override val onPress: () -> Unit,
                 val backgroundImageId: Int
    ): DynamicMenu()

    class Long(
            override val id: Int,
            override val titleResId: Int,
            override val backgroundColorId: Int,
            override val onPress: () -> Unit,
            val backgroundImageId: Int
    ): DynamicMenu()
}