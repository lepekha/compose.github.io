package ua.com.compose.config

class RemoteConfigImpl: RemoteConfigSetup(), IRemoteConfig {
    override val isMenuImage: Boolean
        get() = read<Boolean>(ConfigParam.MENU_PHOTO) ?: true
    override val isMenuPhoto: Boolean
        get() = read<Boolean>(ConfigParam.MENU_IMAGE) ?: true
    override val isMenuPalette: Boolean
        get() = read<Boolean>(ConfigParam.MENU_PALETTE) ?: true
}