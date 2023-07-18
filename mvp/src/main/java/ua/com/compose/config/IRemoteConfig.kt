package ua.com.compose.config

lateinit var remoteConfig: IRemoteConfig

interface IRemoteConfig {
    val isMenuPhoto: Boolean
    val isMenuImage: Boolean
    val isMenuPalette: Boolean
}