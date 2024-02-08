package ua.com.compose.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.BottomSheet
import ua.com.compose.data.ELanguage
import ua.com.compose.data.ETheme
import ua.com.compose.dialogs.ChipItem
import ua.com.compose.dialogs.DialogChoise
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(theme: ETheme, viewModel: SettingsViewModel, onDismissRequest: () -> Unit) {
    val colorTypes by viewModel.colorTypes
    val colorType by viewModel.colorType

    val view = LocalView.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val localConfiguration = LocalConfiguration.current

    var appLocale: ELanguage? by remember {
        val currentLocale = localConfiguration.locales.get(0).language
        mutableStateOf(ELanguage.values().firstOrNull { it.value == currentLocale })
    }

    LaunchedEffect(key1 = Unit) {
        analytics.send(SimpleEvent(key = Analytics.Event.OPEN_SETTINGS))
    }

    var stateLanguage: Boolean by remember { mutableStateOf(false) }
    if(stateLanguage) {
        DialogChoise(
            items = ELanguage.values().sortedBy { it.title }.map { ChipItem(title = it.title, obj = it, icon = painterResource(id = it.flagRes), isSelect = it == appLocale) },
            onDone = {
                appLocale = it
                val locale: LocaleListCompat = LocaleListCompat.forLanguageTags(it.value)
                AppCompatDelegate.setApplicationLocales(locale)
            },
            onDismissRequest = { stateLanguage = false }
        )
    }

    var stateTheme: Boolean by remember { mutableStateOf(false) }
    if(stateTheme) {
        DialogChoise(
            items = ETheme.visibleValues().map { ChipItem(title = stringResource(id = it.strRes), obj = it, isSelect = Settings.theme == it) },
            onDone = {
                viewModel.changeTheme(it)
            },
            onDismissRequest = { stateTheme = false }
        )
    }
    val containerBackground = MaterialTheme.colorScheme.surfaceContainerLow

    BottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {
            Column(modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)) {

                Column(modifier = Modifier
                    .background(
                        color = containerBackground,
                        shape = MaterialTheme.shapes.medium
                    )
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    Text(text = stringResource(id = R.string.color_pick_setting_color_type),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight(500))

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FlowRow(verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.Top)) {
                        colorTypes.forEach {
                            FilterChip(
                                selected = it == colorType,
                                border = null,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                onClick = {
                                    view.vibrate(EVibrate.BUTTON)
                                    viewModel.changeColorType(it)
                                },
                                label = {
                                    Text(text = it.title(), fontSize = 16.sp)
                                }
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalIconButton(
                    onClick = {
                        stateTheme = true
                        view.vibrate(EVibrate.BUTTON) },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerBackground),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)) {
                        Text(text = stringResource(id = R.string.color_pick_theme),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight(500))

                        Text(text = stringResource(id = theme.strRes),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight(700),
                            modifier = Modifier
                                .weight(1f))
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalIconButton(
                    onClick = {
                        stateLanguage = true
                        view.vibrate(EVibrate.BUTTON) },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerBackground),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)) {
                        val currentLocale = LocalConfiguration.current.locales.get(0).language
                        Text(text = stringResource(id = R.string.color_pick_setting_language),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight(500))

                        Text(text = appLocale?.title ?: currentLocale,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight(700),
                            modifier = Modifier
                                .weight(1f))
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .background(
                        color = containerBackground,
                        shape = MaterialTheme.shapes.medium
                    )
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 16.dp, end = 16.dp)) {
                    Text(text = stringResource(id = R.string.color_pick_setting_vibration),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight(500),
                        modifier = Modifier.weight(1f))

                    var state by remember { mutableStateOf(Settings.vibration) }
                    Switch(checked = state,
                        onCheckedChange = {
                            view.vibrate(EVibrate.BUTTON)
                            state = it
                            viewModel.changeVibration(it)
                        },
                        colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        uncheckedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    ))
                }
            }
        }
}