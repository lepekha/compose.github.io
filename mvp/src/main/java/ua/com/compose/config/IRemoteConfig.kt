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
}