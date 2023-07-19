package ua.com.compose.api.config

enum class ConfigParam(val key: String) {
    COLOR_TYPE_HEX("color_type_hex"),
    COLOR_TYPE_RGBDecimal("color_type_rgb_decimal"),
    COLOR_TYPE_RGBPercent("color_type_rgb_percent"),
    COLOR_TYPE_BINARY("color_type_binary"),
    COLOR_TYPE_HSV("color_type_hsv"),
    COLOR_TYPE_HSL("color_type_hsl"),
    COLOR_TYPE_CMYK("color_type_cmyk"),
    COLOR_TYPE_CIELAB("color_type_cie_lab"),
    COLOR_TYPE_XYZ("color_type_xyz"),

    MENU_PHOTO("menu_photo"),
    MENU_IMAGE("menu_image"),
    MENU_PALETTE("menu_palette");
}