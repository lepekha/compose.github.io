package ua.com.compose.data

import ua.com.compose.R

enum class ELanguage(val title: String, val value: String, val flagRes: Int) {
    English(title = "English", value = "en", flagRes = R.drawable.flag_gb),
    Spanish(title = "Español", value = "es", flagRes = R.drawable.flag_es),
    French(title = "Français", value = "fr", flagRes = R.drawable.flag_fr),
    German(title = "Deutsch", value = "de", flagRes = R.drawable.flag_de),
    Italian(title = "Italiano", value = "it", flagRes = R.drawable.flag_it),
    Hindi(title = "हिन्दी", value = "hi", flagRes = R.drawable.flag_in),
    Indonesian(title = "Bahasa Indonesia", value = "id", flagRes = R.drawable.flag_id),
    Japanese(title = "日本語", value = "ja", flagRes = R.drawable.flag_ja),
    Korean(title = "한국어", value = "ko", flagRes = R.drawable.flag_ko),
    Dutch(title = "Nederlands", value = "nl", flagRes = R.drawable.flag_nl),
    Polish(title = "Polski", value = "pl", flagRes = R.drawable.flag_pl),
    Portugues(title = "Português", value = "pt", flagRes = R.drawable.flag_pt),
    Russian(title = "Русский", value = "ru", flagRes = R.drawable.flag_ru),
    Turkish(title = "Türkçe", value = "tr", flagRes = R.drawable.flag_tr),
    Ukrainian(title = "Українська", value = "uk", flagRes = R.drawable.flag_uk),
    Chinese(title = "中文 (简体)", value = "zh", flagRes = R.drawable.flag_zh),
}