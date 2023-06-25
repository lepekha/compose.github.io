package ua.com.compose.other_color_pick.di

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.domain.dbColorItem.AddColorUseCase
import ua.com.compose.other_color_pick.domain.dbColorItem.GetAllColorsUseCase
import ua.com.compose.other_color_pick.domain.dbColorItem.RemoveAllColorsUseCase
import ua.com.compose.other_color_pick.domain.dbColorItem.RemoveColorUseCase
import ua.com.compose.other_color_pick.domain.dbColorItem.UpdateColorUseCase
import ua.com.compose.ImageInfoViewModule
import ua.com.compose.other_color_pick.domain.dbColorItem.ChangeColorPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.AddPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.GetAllPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.GetPalletUseCase
import ua.com.compose.other_color_pick.domain.dbColorPallet.RemovePalletUseCase
import ua.com.compose.other_color_pick.main.ColorPickViewModule
import ua.com.compose.other_color_pick.main.camera.CameraViewModule
import ua.com.compose.other_color_pick.main.image.ImageUri
import ua.com.compose.other_color_pick.main.image.ImageViewModule
import ua.com.compose.other_color_pick.main.info.ColorInfoViewModel
import ua.com.compose.other_color_pick.main.palette.PaletteViewModule


object Scope {
    val COLOR_PICK
        get() = KoinJavaComponent.getKoin().getOrCreateScope(
            "COLOR_PICK", named("COLOR_PICK")
        )
}

val otherColorPickModule = module {
    scope(named("COLOR_PICK")) {
        scoped { ColorDatabase(context = androidContext()) }
        scoped { GetAllColorsUseCase(database = get()) }
        scoped { AddColorUseCase(addPalletUseCase = get(), getPalletUseCase = get(), database = get()) }
        scoped { UpdateColorUseCase(database = get()) }
        scoped { RemoveColorUseCase(database = get()) }
        scoped { RemoveAllColorsUseCase(database = get()) }
        scoped { ImageUri() }
        scoped { AddPalletUseCase(database = get(), context = androidContext()) }
        scoped { RemovePalletUseCase(database = get()) }
        scoped { GetPalletUseCase(database = get()) }
        scoped { GetAllPalletUseCase(database = get()) }
        scoped { ChangeColorPalletUseCase(database = get()) }
        viewModel { ColorInfoViewModel(context = androidContext(), addColorUseCase = get()) }
        viewModel { ColorPickViewModule() }
        viewModel { ImageInfoViewModule() }
        viewModel { PaletteViewModule(changeColorPalletUseCase = get(), getPalletUseCase = get(), removePalletUseCase = get(), getAllPalletUseCase = get(), addPalletUseCase = get(), getAllColorsUseCase = get(), removeColorUseCase = get(), updateColorUseCase = get(), addColorUseCase = get(), removeAllColorsUseCase = get()) }
        viewModel { CameraViewModule(addColorUseCase = get()) }
        viewModel { ImageViewModule(imageUri = get(), addColorUseCase = get()) }
    }
}