package ua.com.compose.config

class RemoteConfigImpl: RemoteConfigSetup(), IRemoteConfig {
    override val isMenuTextStyle: Boolean
        get() = read<Boolean>(ConfigParam.MENU_TEXT_STYLE) ?: true
    override val isMenuImageFilter: Boolean
        get() = read<Boolean>(ConfigParam.MENU_IMAGE_FILTER) ?: true
    override val isMenuImageCrop: Boolean
        get() = read<Boolean>(ConfigParam.MENU_IMAGE_CROP) ?: true
    override val isMenuImageRotate: Boolean
        get() = read<Boolean>(ConfigParam.MENU_IMAGE_ROTATE) ?: true
    override val isMenuImageCompress: Boolean
        get() = read<Boolean>(ConfigParam.MENU_IMAGE_COMPRESS) ?: true
    override val isMenuInstagramPlaner: Boolean
        get() = read<Boolean>(ConfigParam.MENU_INSTAGRAM_PLANER) ?: true
    override val isMenuInstagramNoCrop: Boolean
        get() = read<Boolean>(ConfigParam.MENU_INSTAGRAM_NO_CROP) ?: true
    override val isMenuInstagramGrid: Boolean
        get() = read<Boolean>(ConfigParam.MENU_INSTAGRAM_GRID) ?: true
    override val isMenuInstagramPanorama: Boolean
        get() = read<Boolean>(ConfigParam.MENU_INSTAGRAM_PANORAMA) ?: true
}