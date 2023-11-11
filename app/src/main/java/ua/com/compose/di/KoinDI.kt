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
import ua.com.compose.domain.dbColorPallet.AddPalletUseCase
import ua.com.compose.domain.dbColorPallet.GetAllPalletUseCase
import ua.com.compose.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.domain.dbColorPallet.RemovePalletUseCase
import ua.com.compose.screens.ColorPickViewModule
import ua.com.compose.screens.camera.CameraViewModule
import ua.com.compose.screens.image.ImageViewModule
import ua.com.compose.screens.info.ColorInfoViewModel
import ua.com.compose.screens.palette.PaletteViewModule
import ua.com.compose.screens.settings.SettingsViewModel

val appModule = module {
    single { ColorDatabase(context = androidContext()) }
    single { GetAllColorsUseCase(database = get()) }
    single { AddColorUseCase(addPalletUseCase = get(), getPalletUseCase = get(), database = get()) }
    single { UpdateColorUseCase(database = get()) }
    single { RemoveColorUseCase(database = get()) }
    single { RemoveAllColorsUseCase(database = get()) }
    single { AddPalletUseCase(database = get(), context = androidContext()) }
    single { RemovePalletUseCase(database = get()) }
    single { GetPalletUseCase(database = get()) }
    single { GetAllPalletUseCase(database = get()) }
    single { ChangeColorPalletUseCase(database = get()) }
    viewModel { SettingsViewModel() }
    viewModel { ColorInfoViewModel(context = androidContext(), addColorUseCase = get()) }
    viewModel { ColorPickViewModule() }
    viewModel {
        PaletteViewModule(
            context = androidContext(),
            database = get(),
            changeColorPalletUseCase = get(),
            getPalletUseCase = get(),
            removePalletUseCase = get(),
            getAllPalletUseCase = get(),
            addPalletUseCase = get(),
            getAllColorsUseCase = get(),
            removeColorUseCase = get(),
            updateColorUseCase = get(),
            addColorUseCase = get(),
            removeAllColorsUseCase = get()
        )
    }
    viewModel { CameraViewModule(addColorUseCase = get()) }
    viewModel { ImageViewModule(addColorUseCase = get()) }
}