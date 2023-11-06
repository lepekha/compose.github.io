package ua.com.compose.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.composable.DialogAccentButton
import ua.com.compose.data.ELanguage
import ua.com.compose.dialogs.ChipItem
import ua.com.compose.dialogs.DialogChoise
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(onDismissRequest: () -> Unit) {
    val viewModule: SettingsViewModel = koinViewModel()
    val scope = rememberCoroutineScope()
    val colorTypes by viewModule.colorTypes
    val colorType by viewModule.colorType

    val view = LocalView.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val localConfiguration = LocalConfiguration.current

    var appLocale: ELanguage? by remember {
        val currentLocale = localConfiguration.locales.get(0).language
        mutableStateOf(ELanguage.values().firstOrNull { it.value == currentLocale })
    }

    var stateLanguage: Boolean by remember { mutableStateOf(false) }
    if(stateLanguage) {
        DialogChoise(
            items = ELanguage.values().map { ChipItem(title = it.title, obj = it, icon = painterResource(id = it.flagRes), isSelect = it == appLocale) },
            onDone = {
                appLocale = it
                val locale: LocaleListCompat = LocaleListCompat.forLanguageTags(it.value)
                AppCompatDelegate.setApplicationLocales(locale)
            },
            onDismissRequest = { stateLanguage = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {

        Box(modifier = Modifier
            .wrapContentHeight()
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.color_main_header),
                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)
            )) {
            Column(modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(16.dp)) {

                Column(modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.color_main_background),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    Text(text = stringResource(id = R.string.module_other_color_pick_setting_color_type),
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_night_5),
                        fontSize = 20.sp,
                        fontWeight = FontWeight(500))

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FlowRow {
                        colorTypes.forEach {
                            FilterChip(
                                selected = it == colorType,
                                border = null,
                                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = colorResource(id = R.color.color_night_5),
                                    selectedContainerColor = colorResource(id = R.color.color_night_6)
                                ),
                                onClick = {
                                    view.vibrate(EVibrate.BUTTON)
                                    viewModule.changeColorType(it)
                                },
                                label = {
                                    Text(text = it.title(), color = colorResource(id = R.color.color_night_2), fontSize = 16.sp)
                                }
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalIconButton(
                    onClick = {
                        stateLanguage = true
                        view.vibrate(EVibrate.BUTTON) },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = colorResource(id = R.color.color_main_background)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)) {
                        val currentLocale = LocalConfiguration.current.locales.get(0).language
                        Text(text = stringResource(id = R.string.module_other_color_pick_setting_language),
                            textAlign = TextAlign.Start,
                            color = colorResource(id = R.color.color_night_5),
                            fontSize = 20.sp,
                            fontWeight = FontWeight(500))

                        Text(text = appLocale?.title ?: currentLocale,
                            textAlign = TextAlign.End,
                            color = colorResource(id = R.color.color_night_6),
                            fontSize = 22.sp,
                            fontWeight = FontWeight(700),
                            modifier = Modifier
                                .weight(1f))
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.color_main_background),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 16.dp, end = 16.dp)) {
                    Text(text = stringResource(id = R.string.module_other_color_pick_setting_vibration),
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_night_5),
                        fontSize = 20.sp,
                        fontWeight = FontWeight(500),
                        modifier = Modifier.weight(1f))

                    var state by remember { mutableStateOf(Settings.vibration) }
                    Switch(checked = state,
                        onCheckedChange = {
                            view.vibrate(EVibrate.BUTTON)
                            state = it
                            viewModule.changeVibration(it)
                                          }, colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.color_night_5),
                        uncheckedThumbColor = colorResource(id = R.color.color_night_5),
                        checkedTrackColor = colorResource(id = R.color.color_night_6),
                        uncheckedTrackColor = colorResource(id = R.color.color_night_8),
                        uncheckedBorderColor = colorResource(id = R.color.color_night_8),
                    ))
                }

                Spacer(modifier = Modifier.height(30.dp))

                DialogAccentButton(painter = painterResource(id = R.drawable.ic_done), modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)) {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismissRequest.invoke()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}