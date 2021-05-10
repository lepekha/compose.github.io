package ua.com.compose.other_text_style.utils

interface Converter {
    fun convertSymbol(oldSymbol: Char, pack: Int = 0): String
    fun convertString(string: String, pack: Int = 0): String
    fun size(): Int
}

class EnglishConverter: Converter {
    private val originStr = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private val symbolMap = mutableMapOf<Char, Int>().apply {
        originStr.forEachIndexed { index, char ->
            this[char] = index
        }
    }

    fun addTranslMap(){
        symbolMap['А'] = 36
    }

    private val packAlternative = arrayOf(
            arrayOf("❶","❷","❸","❹","❺","❻","❼","❽","❾","0",
                    "🅐","🅑","🅒","🅓","🅔","🅕","🅖","🅗","🅘",
                    "🅙","🅚","🅛","🅜","🅝","🅞","🅟","🅠","🅡",
                    "🅢","🅣","🅤","🅥","🅦","🅧","🅨","🅩"),

            arrayOf("1","2","3","4","5","6","7","8","9","0",
                    "🅰","🅱","🅲","🅳","🅴","🅵","🅶","🅷","🅸",
                    "🅹","🅺","🅻","🅼","🅽","🅾","🅿","🆀","🆁",
                    "🆂","🆃","🆄","🆅","🆆","🆇","🆈","🆉")
    )

    override fun size(): Int {
        return packAlternative.size
    }

    override fun convertSymbol(oldSymbol: Char, pack: Int): String {
        val oldChar = oldSymbol.toUpperCase()
        if(pack > size()){
            return oldChar.toString()
        }
        val oldSymbolIndex = symbolMap[oldChar] ?: return oldChar.toString()
        return packAlternative[pack][oldSymbolIndex]
    }

    override fun convertString(string: String, pack: Int): String {
        val oldString = string.toUpperCase()
        if(pack > size()){
            return oldString
        }
        val stringBuffer = StringBuffer()
        oldString.forEach { oldSymbol ->
            val oldSymbolIndex = symbolMap[oldSymbol]
            if(oldSymbolIndex != null){
                stringBuffer.append(packAlternative[pack][oldSymbolIndex])
            }else{
                stringBuffer.append(oldSymbol)
            }
        }
        return stringBuffer.toString()
    }
}

class TextStyleConverter {
    companion object{
        fun createConverter() = EnglishConverter()
    }
}