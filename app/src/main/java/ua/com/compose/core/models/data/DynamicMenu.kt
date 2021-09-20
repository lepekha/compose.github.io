package ua.com.compose.core.models.data

sealed class DynamicMenu {
    abstract val id: Int
    abstract val titleResId: Int
    abstract val isVisible: () -> Boolean

    class Text(
            override val id: Int,
            override val titleResId: Int,
            val onPress: () -> Unit,
            override val isVisible: () -> Boolean
    ) : DynamicMenu()

    class Icon(
            override val id: Int,
            override val titleResId: Int,
            val iconResId: Int,
            val onPress: () -> Unit,
            override val isVisible: () -> Boolean
    ) : DynamicMenu()

    class List(
            override val id: Int,
            override val titleResId: Int,
            val innerMenu: MutableList<DynamicMenu>,
            override val isVisible: () -> Boolean
    ) : DynamicMenu()

    class Grid(
            override val id: Int,
            override val titleResId: Int,
            override val isVisible: () -> Boolean,
            val innerMenu: MutableList<DynamicMenu>
    ) : DynamicMenu()

    class Short(
            override val id: Int,
            override val titleResId: Int,
            val onPress: () -> Unit,
            override val isVisible: () -> Boolean,
            val backgroundImageId: Int
    ) : DynamicMenu()

    class Medium(override val id: Int,
                 override val titleResId: Int,
                 val onPress: () -> Unit,
                 override val isVisible: () -> Boolean,
                 val backgroundImageId: Int
    ): DynamicMenu()

    class Long(
            override val id: Int,
            override val titleResId: Int,
            val onPress: () -> Unit,
            override val isVisible: () -> Boolean,
            val backgroundImageId: Int
    ): DynamicMenu()
}