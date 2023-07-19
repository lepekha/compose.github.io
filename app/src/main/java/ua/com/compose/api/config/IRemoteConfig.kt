package ua.com.compose.api.config

lateinit var remoteConfig: IRemoteConfig

interface IRemoteConfig {
    val showPanelCamera: Boolean
    val showPanelImage: Boolean
    val showPanelPalette: Boolean

    val showColorTypeHEX: Boolean
    val showColorTypeRGBDecimal: Boolean
    val showColorTypeRGBPercent: Boolean
    val showColorTypeBINARY: Boolean
    val showColorTypeHSV: Boolean
    val showColorTypeHSL: Boolean
    val showColorTypeCMYK: Boolean
    val showColorTypeCIELAB: Boolean
    val showColorTypeXYZ: Boolean
}