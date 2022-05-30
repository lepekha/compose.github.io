package ua.com.compose.other_social_media_crop.data

import ua.com.compose.image_maker.data.Ratio
import ua.com.compose.other_social_media_crop.R

data class Size(val titleResId: Int, val ratio: Ratio)

enum class ESocialMedia(val eName: String, val titleResId: Int, val iconResId: Int, val sizes: List<Size>) {
    FACEBOOK(
        eName = "FACEBOOK",
        titleResId= R.string.module_other_social_media_facebook,
        iconResId = R.drawable.module_image_crop_ic_crop_original,
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

    LINKEDIN (
        eName = "LINKEDIN",
        titleResId= R.string.module_other_social_media_linkedin,
        iconResId = R.drawable.module_image_crop_ic_crop_original,
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

    INSTAGRAM (
        eName = "INSTAGRAM",
        titleResId= R.string.module_other_social_media_instagram,
        iconResId = R.drawable.module_image_crop_ic_crop_original,
        sizes = listOf(
            Size(titleResId = R.string.module_other_social_media_instagram_profile_picture, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_square_photo, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_landscape_photo, ratio = Ratio.AspectRatio(1.91f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_portrait_photo, ratio = Ratio.AspectRatio(0.8f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_stories, ratio = Ratio.AspectRatio(0.56f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_square_ads, ratio = Ratio.AspectRatio(1f, 1f)),
            Size(titleResId = R.string.module_other_social_media_instagram_landscape_ads, ratio = Ratio.AspectRatio(1.91f, 1f))
        )),
}