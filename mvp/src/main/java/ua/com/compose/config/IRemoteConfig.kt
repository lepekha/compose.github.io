package ua.com.compose.config

lateinit var remoteConfig: IRemoteConfig

interface IRemoteConfig {
    val isMenuTextStyle: Boolean
    val isMenuImageFilter: Boolean
    val isMenuImageCrop: Boolean
    val isMenuImageRotate: Boolean
    val isMenuImageCompress: Boolean
    val isMenuInstagramPlaner: Boolean
    val isMenuInstagramNoCrop: Boolean
    val isMenuInstagramGrid: Boolean
    val isMenuInstagramPanorama: Boolean

    val isFilterContrast: Boolean
    val isFilterBrightness: Boolean
    val isFilterSaturation: Boolean
    val isFilterVibrance: Boolean
    val isFilterExposure: Boolean
    val isFilterTemperature: Boolean
    val isFilterGamma: Boolean
    val isFilterRGB: Boolean
    val isFilterSharpen: Boolean
    val isFilterVignette: Boolean
    val isFilterBlackAndWhite: Boolean
    val isFilterShadow: Boolean
    val isFilterUV: Boolean
    val isFilterSepia: Boolean
}