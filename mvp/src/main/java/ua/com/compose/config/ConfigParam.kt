package ua.com.compose.config

enum class ConfigParam(val key: String) {
    MENU_INSTAGRAM_PANORAMA("menu_instagram_panorama"),
    MENU_INSTAGRAM_GRID("menu_instagram_grid"),
    MENU_INSTAGRAM_NO_CROP("menu_instagram_no_crop"),
    MENU_INSTAGRAM_PLANER("menu_instagram_planer"),
    MENU_IMAGE_COMPRESS("menu_image_compress"),
    MENU_IMAGE_ROTATE("menu_image_rotate"),
    MENU_IMAGE_CROP("menu_image_crop"),
    MENU_IMAGE_FILTER("menu_image_filter"),
    MENU_TEXT_STYLE("menu_text_style"),

    FILTER_CONTRAST("filter_contrast"),
    FILTER_BRIGHTNESS("filter_brightness"),
    FILTER_SATURATION("filter_saturation"),
    FILTER_VIBRANCE("filter_vibrance"),
    FILTER_EXPOSURE("filter_exposure"),
    FILTER_TEMPERATURE("filter_temperature"),
    FILTER_GAMMA("filter_gamma"),
    FILTER_RGB("filter_rgb"),
    FILTER_SHARPEN("filter_sharpen"),
    FILTER_VIGNETTE("filter_vignette"),
    FILTER_BLACK_AND_WHITE("filter_black_and_white"),
    FILTER_SHADOW("filter_shadow"),
    FILTER_UV("filter_uv"),
    FILTER_SEPIA("filter_sepia")
}