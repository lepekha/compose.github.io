package ua.com.compose.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
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
import ua.com.compose.data.enums.ELanguage
import ua.com.compose.data.enums.ESortDirection
import ua.com.compose.data.enums.ESortType
import ua.com.compose.data.enums.ETheme
import ua.com.compose.dialogs.ChipItem
import ua.com.compose.dialogs.DialogBilling
import ua.com.compose.dialogs.DialogChoise
import ua.com.compose.dialogs.DialogSort
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onDismissRequest: () -> Unit) {
    val colorTypes by viewModel.colorTypes
    val colorType by Settings.colorType.flow.collectAsState(initial = Settings.colorType.value)
    val theme by Settings.theme.flow.collectAsState(initial = Settings.theme.value)
    val isPremium by viewModel.isPremium.observeAsState(false)

    val view = LocalView.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val localConfiguration = LocalConfiguration.current
    val settingsRightTextSize = 18.sp

    var appLocale: ELanguage? by remember {
        val currentLocale = localConfiguration.locales.get(0).language
        mutableStateOf(ELanguage.entries.firstOrNull { it.value == currentLocale })
    }

    var stateShowBilling by remember { mutableStateOf(false) }
    if (stateShowBilling) {
        analytics.send(SimpleEvent(key = Analytics.Event.CLICK_SETTING_PREMIUM))
        DialogBilling(text = stringResource(id = R.string.color_pick_half_access)) {
            stateShowBilling = false
        }
    }

    LaunchedEffect(key1 = Unit) {
        analytics.send(SimpleEvent(key = Analytics.Event.OPEN_SETTINGS))
    }

    val sortDirection by Settings.sortDirection.flow.collectAsState(initial = Settings.sortDirection.value)
    val sortType by Settings.sortType.flow.collectAsState(initial = Settings.sortType.value)
    var stateSortDialog: Boolean by remember { mutableStateOf(false) }
    if (stateSortDialog) {
        DialogSort(
            type = sortType,
            direction = sortDirection,
            onDone = { type, direction ->
                Settings.sortType.update(type ?: ESortType.ORDER)
                Settings.sortDirection.update(direction ?: ESortDirection.DESC)
            },
            onDismissRequest = { stateSortDialog = false }
        )
    }

    var stateLanguage: Boolean by remember { mutableStateOf(false) }
    if (stateLanguage) {
        DialogChoise(
            items = ELanguage.entries.sortedBy { it.title }.map {
                ChipItem(
                    title = it.title,
                    obj = it,
                    icon = painterResource(id = it.flagRes),
                    isSelect = it == appLocale
                )
            },
            onDone = {
                appLocale = it
                val locale: LocaleListCompat = LocaleListCompat.forLanguageTags(it.value)
                AppCompatDelegate.setApplicationLocales(locale)
            },
            onDismissRequest = { stateLanguage = false }
        )
    }

    var stateTheme: Boolean by remember { mutableStateOf(false) }
    if (stateTheme) {
        DialogChoise(
            items = ETheme.visibleValues().map {
                ChipItem(
                    title = stringResource(id = it.strRes),
                    obj = it,
                    isSelect = theme == it
                )
            },
            onDone = {
                Settings.theme.update(it)
            },
            onDismissRequest = { stateTheme = false }
        )
    }
    val containerBackground = MaterialTheme.colorScheme.surfaceContainerLow

    BottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {
        LazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {

            item {
                Column(
                    modifier = Modifier
                        .background(
                            color = containerBackground,
                            shape = MaterialTheme.shapes.medium
                        )
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                {

                    Text(
                        text = stringResource(id = R.string.color_pick_setting_color_type),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight(500)
                    )

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
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                FilledTonalIconButton(
                    onClick = {
                        stateSortDialog = true
                        view.vibrate(EVibrate.BUTTON)
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerBackground),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {

                        Text(
                            text = stringResource(id = R.string.color_pick_sort_type),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight(500)
                        )

                        Text(
                            text = stringResource(id = sortType.stringResId),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = settingsRightTextSize,
                            fontWeight = FontWeight(700)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                FilledTonalIconButton(
                    onClick = {
                        stateTheme = true
                        view.vibrate(EVibrate.BUTTON)
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerBackground),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.color_pick_theme),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight(500)
                        )

                        Text(
                            text = stringResource(id = theme.strRes),
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = settingsRightTextSize,
                            fontWeight = FontWeight(700),
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                FilledTonalIconButton(
                    onClick = {
                        stateLanguage = true
                        view.vibrate(EVibrate.BUTTON)
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerBackground),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        val currentLocale = LocalConfiguration.current.locales.get(0).language
                        Text(
                            text = stringResource(id = R.string.color_pick_setting_language),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight(500)
                        )

                        Text(
                            text = appLocale?.title ?: currentLocale,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = settingsRightTextSize,
                            fontWeight = FontWeight(700),
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .background(
                            color = containerBackground,
                            shape = MaterialTheme.shapes.medium
                        )
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.color_pick_setting_vibration),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight(500),
                        modifier = Modifier.weight(1f)
                    )

                    val vibration by Settings.vibration.flow.collectAsState(initial = Settings.vibration.value)
                    Switch(
                        checked = vibration,
                        onCheckedChange = {
                            view.vibrate(EVibrate.BUTTON)
                            Settings.vibration.update(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            uncheckedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                        )
                    )
                }
            }

            item {
                if (!isPremium) {
                    Spacer(modifier = Modifier.height(4.dp))

                    FilledTonalIconButton(
                        onClick = {
                            stateShowBilling = true
                            view.vibrate(EVibrate.BUTTON)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, end = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.color_pick_premium_version),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onTertiary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight(500),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}