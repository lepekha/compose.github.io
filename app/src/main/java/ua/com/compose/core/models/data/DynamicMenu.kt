package ua.com.compose.core.models.data

sealed class DynamicMenu {
    abstract val id: Int
    abstract val titleResId: Int
    abstract val isVisible: () -> Boolean

    class Icon(
            override val id: Int,
            override val titleResId: Int,
            val iconResId: Int,
            val onPress: () -> Unit,
            override val isVisible: () -> Boolean
    ) : DynamicMenu()

    class Grid(
            override val id: Int,
            override val titleResId: Int,
            override val isVisible: () -> Boolean,
            val innerMenu: MutableList<DynamicMenu>,
            val spanCount: Int
    ) : DynamicMenu()

    class Image(override val id: Int,
                override val titleResId: Int,
                val onPress: () -> Unit,
                override val isVisible: () -> Boolean,
                val backgroundImageId: Int
    ): DynamicMenu()

}