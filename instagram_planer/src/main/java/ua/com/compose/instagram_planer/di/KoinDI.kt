package ua.com.compose.instagram_planer.di

import ua.com.compose.instagram_planer.view.image.InstagramPlanerImagePresenter
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
        scoped { InstagramPlanerImagePresenter(context = androidContext()) }
        scoped { CreateUserUseCase(database = get()) }
        scoped { GetAllUsersUseCase(database = get()) }
        scoped { GetAllUserImagesUseCase(database = get()) }
        scoped { RemoveUserUseCase(database = get()) }
        scoped { UpdateImageUseCase(database = get()) }
        scoped { RemoveAllImagesFromUserUseCase(database = get()) }
        scoped { ChangeImagePositionsUseCase(database = get()) }
        scoped { AddImagesForUserUseCase(context = androidContext(), database = get()) }

        viewModel {
            InstagramPlanerViewModel(
                createUserUseCase = get(),
                getAllUsersUseCase = get(),
                getAllUserImagesUseCase = get(),
                removeUserUseCase = get(),
                updateImageUseCase = get(),
                addImagesForUserUseCase = get(),
                removeAllImagesFromUserUseCase = get(),
                changeImagePositionsUseCase = get()
            )
        }
    }
}