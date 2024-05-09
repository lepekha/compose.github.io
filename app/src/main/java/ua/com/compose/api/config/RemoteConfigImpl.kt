package ua.com.compose.api.config

class RemoteConfigImpl: RemoteConfigSetup(), IRemoteConfig {
    override val showUpdateMessage: Boolean
        get() = read<Boolean>(ConfigParam.COLOR_PICK_SHOW_UPDATE_MESSAGE) ?: true
}