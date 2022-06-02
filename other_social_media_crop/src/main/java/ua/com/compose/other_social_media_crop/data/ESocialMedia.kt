package ua.com.compose.other_social_media_crop.data

import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.other_social_media_crop.R

data class Size(val titleResId: Int, val ratio: Ratio)

enum class ESocialMedia(val eName: String, val titleResId: Int, val iconResId: Int, val sizes: List<Size>) {
    FACEBOOK(
        eName = "FACEBOOK",
        titleResId= R.string.module_other_social_media_facebook,
        iconResId = R.drawable.module_other_social_media_crop_ic_facebook,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_facebook_profile_picture_size, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_facebook_cover_photo_size, ratio = Ratio.AspectRatio(205f, 78f)),
            Size(titleResId = R.string.module_other_social_media_facebook_link_image_size, ratio = Ratio.AspectRatio(40f, 21f)),
            Size(titleResId = R.string.module_other_social_media_facebook_image_post_size, ratio = Ratio.AspectRatio(40f, 21f)),
            Size(titleResId = R.string.module_other_social_media_facebook_event_image_size, ratio = Ratio.AspectRatio(128f, 67f)),
            Size(titleResId = R.string.module_other_social_media_facebook_group_cover_image_size, ratio = Ratio.AspectRatio(205f, 107f)),
            Size(titleResId = R.string.module_other_social_media_facebook_image_ad_size, ratio = Ratio.AspectRatio(300f, 157f)),
            Size(titleResId = R.string.module_other_social_media_facebook_story_ad_size, ratio = Ratio.AspectRatio(9f, 16f)),
            Size(titleResId = R.string.module_other_social_media_facebook_messenger_image_ad_size, ratio = Ratio.AspectRatio(300f, 157f))
        )),

    INSTAGRAM (
        eName = "INSTAGRAM",
        titleResId= R.string.module_other_social_media_instagram,
        iconResId = R.drawable.module_other_social_media_crop_ic_instagram,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_instagram_profile_picture, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_square_photo, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_landscape_photo, ratio = Ratio.AspectRatio(1.91f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_portrait_photo, ratio = Ratio.AspectRatio(0.8f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_stories, ratio = Ratio.AspectRatio(0.56f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_square_ads, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_landscape_ads, ratio = Ratio.AspectRatio(1.91f, 1f))
        )),

    YOUTUBE (
        eName = "YouTube",
        titleResId= R.string.module_other_social_media_youtube,
        iconResId = R.drawable.module_other_social_media_crop_ic_youtube,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_youtube_profile_photo, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_youtube_desktop_channel_picture, ratio = Ratio.AspectRatio(1.78f, 1f)),
            Size(titleResId = R.string.module_other_social_media_youtube_smartphones_channel_picture, ratio = Ratio.AspectRatio(3.65f, 1f)),
            Size(titleResId = R.string.module_other_social_media_youtube_thumbnail, ratio = Ratio.AspectRatio(1.78f, 1f))
        )),

    PINTEREST (
        eName = "Pinterest",
        titleResId= R.string.module_other_social_media_pinterest,
        iconResId = R.drawable.module_other_social_media_crop_ic_pinterest,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_pinterest_profile_image, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_pinterest_profile_cover, ratio = Ratio.AspectRatio(1.78f, 1f)),
            Size(titleResId = R.string.module_other_social_media_pinterest_pin_image, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_pinterest_pin_image_recommended, ratio = Ratio.AspectRatio(2f, 3f)),
            Size(titleResId = R.string.module_other_social_media_pinterest_story_pin, ratio = Ratio.AspectRatio(0.56f, 1f)),
        )),

    LINKEDIN (
        eName = "LINKEDIN",
        titleResId= R.string.module_other_social_media_linkedin,
        iconResId = R.drawable.module_other_social_media_crop_ic_linkedin,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_linkedin_company_logo_size, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_company_cover_photo_size, ratio = Ratio.AspectRatio(5.91f, 1f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_company_sponsored_content_image_size, ratio = Ratio.AspectRatio(400f, 209f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_company_blog_post_link_images, ratio = Ratio.AspectRatio(400f, 209f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_company_business_banner, ratio = Ratio.AspectRatio(323f, 110f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_personal_profile_picture, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_personal_background_photo, ratio = Ratio.AspectRatio(4f, 1f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_personal_post_image_square, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_personal_post_image_portrait, ratio = Ratio.AspectRatio(0.8f, 1f)),
            Size(titleResId = R.string.module_other_social_media_linkedin_personal_link_post, ratio = Ratio.AspectRatio(1.91f, 1f))
        )),

    TWITTER (
        eName = "TWITTER",
        titleResId= R.string.module_other_social_media_twitter,
        iconResId = R.drawable.module_other_social_media_crop_ic_twitter,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_twitter_profile_picture, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_twitter_header, ratio = Ratio.AspectRatio(3f, 1f)),
            Size(titleResId = R.string.module_other_social_media_twitter_post_image, ratio = Ratio.AspectRatio(1.78f, 1f)),
            Size(titleResId = R.string.module_other_social_media_twitter_card_image, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_twitter_ads_website_card, ratio = Ratio.AspectRatio(1.91f, 1f)),
            Size(titleResId = R.string.module_other_social_media_twitter_ads_app_card, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_twitter_ads_carousels, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_twitter_ads_direct_message_card, ratio = Ratio.AspectRatio(1.91f, 1f)),
            Size(titleResId = R.string.module_other_social_media_twitter_ads_conversation_card, ratio = Ratio.AspectRatio(1.91f, 1f))
        )),

    TIKTOK (
        eName = "TikTok",
        titleResId= R.string.module_other_social_media_tiktok,
        iconResId = R.drawable.module_other_social_media_crop_ic_tiktok,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_tiktok_profile_photo, ratio = Ratio.AspectRatio(1f, 1f))
        )),

    SNAPCHAT (
        eName = "Snapchat",
        titleResId= R.string.module_other_social_media_snapchat,
        iconResId = R.drawable.module_other_social_media_crop_ic_snapchat,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_snapchat_ads_image, ratio = Ratio.AspectRatio(0.56f, 1f)),
            Size(titleResId = R.string.module_other_social_media_snapchat_geofilter_image, ratio = Ratio.AspectRatio(0.56f, 1f)),
        ));
}