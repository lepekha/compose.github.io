package ua.com.compose.config

class RemoteConfigImpl: RemoteConfigSetup(), IRemoteConfig {
    override val isMenuOtherColorPick: Boolean
        get() = read<Boolean>(ConfigParam.MENU_OTHER_COLOR_PICK) ?: true
    override val isMenuOtherSocialMediaCrop: Boolean
        get() = read<Boolean>(ConfigParam.MENU_OTHER_SOCIAL_MEDIA_CROP) ?: true
    override val isMenuOtherTextStyle: Boolean
        get() = read<Boolean>(ConfigParam.MENU_OTHER_TEXT_STYLE) ?: true
    override val isMenuImageFilter: Boolean
        get() = read<Boolean>(ConfigParam.MENU_IMAGE_FILTER) ?: true
    override val isMenuImageStyle: Boolean
        get() = read<Boolean>(ConfigParam.MENU_IMAGE_STYLE) ?: true
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

    override val isFilterContrast: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_CONTRAST) ?: true
    override val isFilterBrightness: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_BRIGHTNESS) ?: true
    override val isFilterSaturation: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_SATURATION) ?: true
    override val isFilterVibrance: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_VIBRANCE) ?: true
    override val isFilterExposure: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_EXPOSURE) ?: true
    override val isFilterTemperature: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_TEMPERATURE) ?: true
    override val isFilterGamma: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_GAMMA) ?: true
    override val isFilterRGB: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_RGB) ?: true
    override val isFilterSharpen: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_SHARPEN) ?: true
    override val isFilterVignette: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_VIGNETTE) ?: true
    override val isFilterBlackAndWhite: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_BLACK_AND_WHITE) ?: true
    override val isFilterShadow: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_SHADOW) ?: true
    override val isFilterUV: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_UV) ?: true
    override val isFilterSepia: Boolean
        get() = read<Boolean>(ConfigParam.FILTER_SEPIA) ?: false
}