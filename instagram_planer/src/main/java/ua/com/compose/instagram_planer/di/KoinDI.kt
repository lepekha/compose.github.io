package ua.com.compose.instagram_planer.di

import ua.com.compose.instagram_planer.view.image.InstagramPlanerImageViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import ua.com.compose.instagram_planer.view.domain.*
import ua.com.compose.instagram_planer.view.main.InstagramPlanerViewModel

object Scope {
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
            "INSTAGRAM", named("INSTAGRAM")
        )
}

val instagramPlanerModule = module {

    scope(named("INSTAGRAM")) {
        scoped { InstagramPlanerDatabase(context = androidContext()) }
        scoped { CreateUserUseCase(database = get()) }
        scoped { GetAllUsersUseCase(database = get()) }
        scoped { GetAllUserImagesUseCase(database = get()) }
        scoped { RemoveUserUseCase(database = get()) }
        scoped { ImageRemoveUseCase(database = get()) }
        scoped { ImageUpdateUseCase(database = get()) }
        scoped { RemoveAllImagesFromUserUseCase(database = get()) }
        scoped { ImageChangePositionsUseCase(database = get()) }
        scoped { ImageChangeUriUseCase(context = androidContext(), database = get()) }
        scoped { AddImagesForUserUseCase(context = androidContext(), database = get()) }
        scoped { ImageDownloadUseCase(context = androidContext()) }
        scoped { ImageByIdUseCase(database = get()) }
        scoped { UserChangeUseCase(database = get()) }
        scoped { TextSymbolCountUseCase() }
        scoped { TextHashtagCountUseCase() }
        scoped { TextMailCountUseCase() }
        scoped { ImageUpdateTextUseCase(database = get()) }
        scoped { ImageShareInstagramUseCase(context = androidContext()) }
        scoped { ImageShareUseCase(context = androidContext()) }

        viewModel {
            InstagramPlanerViewModel(
                createUserUseCase = get(),
                getAllUsersUseCase = get(),
                getAllUserImagesUseCase = get(),
                removeUserUseCase = get(),
                imageUpdateUseCase = get(),
                addImagesForUserUseCase = get(),
                removeAllImagesFromUserUseCase = get(),
                imageChangePositionsUseCase = get(),
                imageRemoveUseCase = get(),
                imageChangeUseCase = get(),
                imageDownloadUseCase = get(),
                userChangeUseCase = get()
            )
        }

        viewModel {
            InstagramPlanerImageViewModel(
                imageByIdUseCase = get(),
                imageDownloadUseCase = get(),
                textSymbolCountUseCase = get(),
                textHashtagCountUseCase = get(),
                textMailCountUseCase = get(),
                imageUpdateTextUseCase = get(),
                imageShareUseCase = get(),
                imageShareInstagramUseCase = get()
            )
        }
    }
}