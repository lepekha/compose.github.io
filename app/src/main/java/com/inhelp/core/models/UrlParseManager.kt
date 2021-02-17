package com.inhelp.core.models

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.inhelp.utils.extension.clipboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URL
import java.util.regex.Pattern

data class InstagramSaveData(val image: String, val arrImages: ArrayList<String>)

class InstagramUrlData(private val context: Context) {

    val isValidUrl = context.clipboard.contains("https://www.instagram.com")

    fun getPhotos() = GlobalScope.async {
        if(isValidUrl.not()) return@async arrayListOf<String>()
        val first = "<script type=\"text/javascript\">window._sharedData = "
        val last = ";</script>"
        val p = Pattern.compile("$first.*?$last")
        val url = context.clipboard
        val m = p.matcher(Jsoup.connect(url).get().html())
        m.find()
        val text = m.group(0).replace(first, "").replace(last, "")

        val shortcode_media = JSONObject(text)
                .getJSONObject("entry_data")
                .getJSONArray("PostPage")
                .getJSONObject(0)
                .getJSONObject("graphql")
                .getJSONObject("shortcode_media")

        val urlArray = arrayListOf<String>()

        try {
            val edges = shortcode_media
                    .getJSONObject("edge_sidecar_to_children")
                    .getJSONArray("edges")

            for (i in 0..(edges.length() - 1)) {
                urlArray.add(edges.getJSONObject(i).getJSONObject("node").getString("display_url"))
            }

        }catch (e: Exception){
            urlArray.add(shortcode_media.getString("display_url"))
        }
        urlArray
    }
}