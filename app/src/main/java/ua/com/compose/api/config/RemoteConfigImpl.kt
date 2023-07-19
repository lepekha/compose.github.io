package ua.com.compose.api.config

class RemoteConfigImpl: RemoteConfigSetup(), IRemoteConfig {
    override val showPanelImage: Boolean
        get() = read<Boolean>(ConfigParam.MENU_PHOTO) ?: true
    override val showPanelCamera: Boolean
        get() = read<Boolean>(ConfigParam.MENU_IMAGE) ?: true
    override val showPanelPalette: Boolean
        get() = read<Boolean>(ConfigParam.MENU_PALETTE) ?: true

    override val showColorTypeHEX: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_HEX) ?: true
    override val showColorTypeRGBDecimal: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_RGBDecimal) ?: true
    override val showColorTypeRGBPercent: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_RGBPercent) ?: true
    override val showColorTypeBINARY: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_BINARY) ?: true
    override val showColorTypeHSV: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_HSV) ?: true
    override val showColorTypeHSL: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_HSL) ?: true
    override val showColorTypeCMYK: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_CMYK) ?: true
    override val showColorTypeCIELAB: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_CIELAB) ?: true
    override val showColorTypeXYZ: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_TYPE_XYZ) ?: true
}