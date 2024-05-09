package ua.com.compose.api.config

lateinit var remoteConfig: IRemoteConfig

interface IRemoteConfig {
    val showUpdateMessage: Boolean
}