package ua.com.compose.other_text_style.di

import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import ua.com.compose.other_text_style.domain.GetStringsUseCase
import ua.com.compose.other_text_style.main.TextStyleViewModel
import ua.com.compose.other_text_style.utils.EnglishConverter

val textStyleModule = module {
    single { EnglishConverter() }
    single { GetStringsUseCase(converter = get<EnglishConverter>()) }

    viewModel { TextStyleViewModel(getStringsUseCase = get()) }
}