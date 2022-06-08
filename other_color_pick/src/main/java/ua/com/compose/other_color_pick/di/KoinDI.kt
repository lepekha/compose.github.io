package ua.com.compose.other_color_pick.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import ua.com.compose.other_color_pick.data.ColorDatabase
import ua.com.compose.other_color_pick.domain.AddColorUseCase
import ua.com.compose.other_color_pick.domain.GetAllColorsUseCase
import ua.com.compose.other_color_pick.domain.RemoveAllColorsUseCase
import ua.com.compose.other_color_pick.domain.RemoveColorUseCase
import ua.com.compose.other_color_pick.main.ImageInfoViewModule
import ua.com.compose.other_color_pick.main.camera.CameraViewModule
import ua.com.compose.other_color_pick.main.image.ImageUri
import ua.com.compose.other_color_pick.main.image.ImageViewModule
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
        scoped { AddColorUseCase(database = get()) }
        scoped { RemoveColorUseCase(database = get()) }
        scoped { RemoveAllColorsUseCase(database = get()) }
        scoped { ImageUri() }
        viewModel { ImageInfoViewModule() }
        viewModel { PaletteViewModule(getAllColorsUseCase = get(), removeColorUseCase = get(), removeAllColorsUseCase = get()) }
        viewModel { CameraViewModule(addColorUseCase = get()) }
        viewModel { ImageViewModule(imageUri = get(), addColorUseCase = get()) }
    }
}