package ua.com.compose.image_filter.data

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import ua.com.compose.extension.get
import ua.com.compose.extension.prefs
import ua.com.compose.extension.put

class Style {
    @SerializedName("name") var name: String = ""
    @SerializedName("appBuildVersion") var appBuildVersion: String = ""
    @SerializedName("filters") private var filters: String = ""

    fun setFilters(filters: List<ImageFilter>) {
        this.filters = filters.joinToString(";") { it.export() }
    }

    fun getFilters() = this.filters.split(";").map { EImageFilter.create(it) }

    companion object {
        private const val defStyles = """[{"appBuildVersion":"1.1.0","filters":"1|1.108;2|-0.027999999;4|0.8;3|1.136;4|0.368;7|1.06;10|-1.2;5|0.147","name":"Kyiv"},{"appBuildVersion":"1.1.0","filters":"11|0.096;9|0.7;5|0.25199997;1|1.3","name":"Munich"},{"appBuildVersion":"1.1.0","filters":"6|4328.0;6|4300.0;6|4300.0","name":"Rome"},{"appBuildVersion":"1.1.0","filters":"8|1.5:1.0:1.0","name":"Istanbul"},{"appBuildVersion":"1.1.0","filters":"6|10000.0","name":"Tokio"},{"appBuildVersion":"1.1.0","filters":"8|1.0:1.495:1.0","name":"Melbourne"},{"appBuildVersion":"1.1.0","filters":"6|4328.0;6|4300.0;6|4300.0","name":"Lisbon"},{"appBuildVersion":"1.1.0","filters":"11|0.096;9|0.7;5|0.25199997;1|1.3","name":"Cape Town"}]"""

        private val gson = GsonBuilder().create()

        fun loadStyles() = gson.fromJson<List<Style>>(prefs.get("image_styles", defStyles), object : TypeToken<List<Style>>(){}.type)?.toMutableList() ?: mutableListOf()
        fun saveStyles(styles: List<Style>) {
            prefs.put("image_styles", gson.toJson(styles))
        }
    }
}