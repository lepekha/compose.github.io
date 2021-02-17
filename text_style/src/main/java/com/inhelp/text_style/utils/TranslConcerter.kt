package com.inhelp.text_style.utils

object TranslConverter {

    private val translMap = mutableMapOf(
            Pair("а", "a"), Pair("б", "b"), Pair("в", "v"), Pair("г", "g"),
            Pair("д", "d"), Pair("е", "e"), Pair("ё", "yo"), Pair("ж", "zh"),
            Pair("з", "z"), Pair("и", "i"), Pair("й", "j"), Pair("к", "k"),
            Pair("л", "l"), Pair("м", "m"), Pair("н", "n"), Pair("о", "o"),
            Pair("п", "p"), Pair("р", "r"), Pair("с", "s"), Pair("т", "t"),
            Pair("у", "u"), Pair("ф", "f"), Pair("х", "kh"), Pair("ц", "ts"),
            Pair("ч", "ch"), Pair("ш", "sh"), Pair("щ", "shch"), Pair("ы", "y"),
            Pair("ь", ""), Pair("ъ", ""), Pair("э", "e"), Pair("ю", "yu"),
            Pair("я", "ya")
    )

    fun getCorrectString(oldString: String): String {
        val stringBuffer = StringBuffer()
        oldString.forEach { oldChar ->
            val oldStr = oldChar.toString()
            val oldStrLoverCase = oldStr.toLowerCase()
            if(translMap.containsKey(oldStrLoverCase)){
                if(oldChar.isUpperCase()){
                    stringBuffer.append(translMap[oldStrLoverCase]?.toUpperCase() ?: "")
                }else{
                    stringBuffer.append(translMap[oldStrLoverCase])
                }
            }else{
                stringBuffer.append(oldChar)
            }
        }
        return stringBuffer.toString()
    }
}