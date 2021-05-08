package ua.com.compose.core.models.data

sealed class DynamicMenu {
    abstract val id: Int
    abstract val titleResId: Int

    class Text(
            override val id: Int,
            override val titleResId: Int,
            val onPress: () -> Unit,
    ) : DynamicMenu()

    class Icon(
            override val id: Int,
            override val titleResId: Int,
            val iconResId: Int,
            val onPress: () -> Unit,
    ) : DynamicMenu()

    class List(
            override val id: Int,
            override val titleResId: Int,
            val innerMenu: MutableList<DynamicMenu>
    ) : DynamicMenu()

    class Grid(
            override val id: Int,
            override val titleResId: Int,
            val innerMenu: MutableList<DynamicMenu>
    ) : DynamicMenu()

    class Short(
            override val id: Int,
            override val titleResId: Int,
            val onPress: () -> Unit,
            val backgroundImageId: Int
    ) : DynamicMenu()

    class Medium(override val id: Int,
                 override val titleResId: Int,
                 val onPress: () -> Unit,
                 val backgroundImageId: Int
    ): DynamicMenu()

    class Long(
            override val id: Int,
            override val titleResId: Int,
            val onPress: () -> Unit,
            val backgroundImageId: Int
    ): DynamicMenu()
}