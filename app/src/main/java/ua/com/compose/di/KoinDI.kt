package ua.com.compose.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ua.com.compose.data.ColorDatabase
import ua.com.compose.domain.dbColorItem.AddColorUseCase
import ua.com.compose.domain.dbColorItem.ChangeColorPalletUseCase
import ua.com.compose.domain.dbColorItem.GetAllColorsUseCase
import ua.com.compose.domain.dbColorItem.RemoveAllColorsUseCase
import ua.com.compose.domain.dbColorItem.RemoveColorUseCase
import ua.com.compose.domain.dbColorItem.UpdateColorUseCase
import ua.com.compose.domain.dbColorPallet.CreatePalletUseCase
import ua.com.compose.domain.dbColorPallet.GetCurrentPalletUseCase
import ua.com.compose.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.domain.dbColorPallet.RemovePalletUseCase
import ua.com.compose.domain.dbColorPallet.SelectPalletUseCase
import ua.com.compose.domain.dbColorPallet.UpdatePalletUseCase
import ua.com.compose.screens.camera.CameraViewModule
import ua.com.compose.screens.image.ImageViewModule
import ua.com.compose.screens.info.ColorInfoViewModel
import ua.com.compose.screens.palette.PaletteViewModule
import ua.com.compose.screens.palettes.PalettesViewModel
import ua.com.compose.screens.settings.SettingsViewModel
import ua.com.compose.screens.share.ShareViewModel

val appModule = module {
    single { ColorDatabase(context = androidContext()) }
    single { GetAllColorsUseCase(database = get()) }
    single { AddColorUseCase(createPalletUseCase = get(), database = get()) }
    single { UpdateColorUseCase(database = get()) }
    single { RemoveColorUseCase(database = get()) }
    single { RemoveAllColorsUseCase(database = get()) }
    single { CreatePalletUseCase(database = get(), context = androidContext()) }
    single { GetCurrentPalletUseCase(database = get()) }
    single { RemovePalletUseCase(database = get()) }
    single { GetPalletUseCase(database = get()) }
    single { UpdatePalletUseCase(database = get()) }
    single { SelectPalletUseCase(database = get()) }
    single { ChangeColorPalletUseCase(database = get()) }
    viewModel { SettingsViewModel() }
    viewModel { PalettesViewModel(getCurrentPalletUseCase = get(), addColorUseCase = get()) }
    viewModel { ColorInfoViewModel(addColorUseCase = get()) }
    viewModel { ShareViewModel(getAllColorsUseCase = get(), getPalletUseCase = get()) }
    viewModel {
        PaletteViewModule(
            database = get(),
            changeColorPalletUseCase = get(),
            selectPalletUseCase = get(),
            removePalletUseCase = get(),
            createPalletUseCase = get(),
            removeColorUseCase = get(),
            updateColorUseCase = get(),
            addColorUseCase = get()
        )
    }
    viewModel { CameraViewModule(addColorUseCase = get()) }
    viewModel { ImageViewModule(addColorUseCase = get()) }
}