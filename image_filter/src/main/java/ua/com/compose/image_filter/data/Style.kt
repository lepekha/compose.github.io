package ua.com.compose.image_filter.data

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put

class Style {
    @SerializedName("name") var name: String = ""
    @SerializedName("appBuildVersion") var appBuildVersion: Long = 0
    @SerializedName("filters") private var filters: String = ""
    @SerializedName("app") var app: Boolean = false

    fun setFilters(filters: List<ImageFilter>) {
        this.filters = filters.joinToString(";") { it.export() }
    }

    fun getFilters() = this.filters.split(";").map { EImageFilter.create(it) }

    companion object {
        private const val defStyles = """[{"app":true,"appBuildVersion":"1","filters":"4|0.23200001;3|1.0879999;9|-0.07;4|0.152;10|-1.2","name":"Kyiv"},{"app":true,"appBuildVersion":"1","filters":"1|1.108;2|-0.027999999;4|0.8;3|1.136;4|0.368;7|1.06;10|-1.2;5|0.147","name":"Munich"},{"app":true,"appBuildVersion":"1","filters":"4|0.24800001;7|1.2;6|4300.0;9|-0.042;3|1.08;1|0.988","name":"Rome"},{"app":true,"appBuildVersion":"1","filters":"6|7200.0;4|0.24;3|1.056;10|-1.2;5|0.147","name":"Istanbul"},{"app":true,"appBuildVersion":"1","filters":"11|0.032;1|1.12;7|1.03;9|-0.049000002","name":"Tokio"},{"app":true,"appBuildVersion":"1","filters":"11|0.0;5|0.147;1|1.2939999;1|1.3;1|1.2099999;2|0.076;7|1.26","name":"Melbourne"}]"""

        private val gson = GsonBuilder().create()

        fun loadStyles(version: Long) = try {
            gson.fromJson<List<Style>>(prefs.get("image_styles", defStyles), object : TypeToken<List<Style>>(){}.type)?.filter { it.appBuildVersion <= version }?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }

        fun saveStyles(styles: List<Style>) {
            prefs.put("image_styles", gson.toJson(styles))
        }
    }
}