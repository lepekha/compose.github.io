package ua.com.compose.core.models.data

sealed class DynamicMenu {
    abstract val id: Int
    abstract val name: String
    abstract val titleResId: Int

    class Icon(
            override val id: Int,
            override val name: String,
            override val titleResId: Int,
            val iconResId: Int,
            val iconColor: Int? = null,
            val onPress: () -> Unit
    ) : DynamicMenu() {
        var isVisible: () -> Boolean = { true }
    }

    class Grid(
            override val id: Int,
            override val name: String,
            override val titleResId: Int,
            val innerMenu: MutableList<DynamicMenu>,
            val spanCount: Int
    ) : DynamicMenu(){
        var isVisible: () -> Boolean = { true }
    }

    class Image(
        override val id: Int,
        override val name: String,
        override val titleResId: Int,
        val onPress: () -> Unit,
        val backgroundImageId: Int
    ): DynamicMenu(){
        var isVisible: () -> Boolean = { true }
    }

}