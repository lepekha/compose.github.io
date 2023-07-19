package ua.com.compose.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ua.com.compose.data.ImageInfoViewModule
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
import ua.com.compose.fragments.ColorPickViewModule
import ua.com.compose.fragments.camera.CameraViewModule
import ua.com.compose.fragments.image.ImageUri
import ua.com.compose.fragments.image.ImageViewModule
import ua.com.compose.fragments.info.ColorInfoViewModel
import ua.com.compose.fragments.palette.PaletteViewModule

val appModule = module {
    single { ColorDatabase(context = androidContext()) }
    single { GetAllColorsUseCase(database = get()) }
    single { AddColorUseCase(addPalletUseCase = get(), getPalletUseCase = get(), database = get()) }
    single { UpdateColorUseCase(database = get()) }
    single { RemoveColorUseCase(database = get()) }
    single { RemoveAllColorsUseCase(database = get()) }
    single { ImageUri() }
    single { AddPalletUseCase(database = get(), context = androidContext()) }
    single { RemovePalletUseCase(database = get()) }
    single { GetPalletUseCase(database = get()) }
    single { GetAllPalletUseCase(database = get()) }
    single { ChangeColorPalletUseCase(database = get()) }
    viewModel { ColorInfoViewModel(context = androidContext(), addColorUseCase = get()) }
    viewModel { ColorPickViewModule() }
    viewModel { ImageInfoViewModule() }
    viewModel { PaletteViewModule(context = androidContext(), changeColorPalletUseCase = get(), getPalletUseCase = get(), removePalletUseCase = get(), getAllPalletUseCase = get(), addPalletUseCase = get(), getAllColorsUseCase = get(), removeColorUseCase = get(), updateColorUseCase = get(), addColorUseCase = get(), removeAllColorsUseCase = get()) }
    viewModel { CameraViewModule(addColorUseCase = get()) }
    viewModel { ImageViewModule(imageUri = get(), addColorUseCase = get()) }
}