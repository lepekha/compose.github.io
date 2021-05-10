package ua.com.compose.image_maker.enums

enum class EResolution(val param: Pair<Int, Int>?) {
    ORIGINAL(param = null),
    ONE_TO_ONE(param = Pair(1,1)),
    TWO_TO_ONE(param = Pair(2,1)),
    THREE_TO_TWO(param = Pair(3,2)),
    THREE_TO_FOUR(param = Pair(3,4)),
    FOUR_TO_THREE(param = Pair(4,3)),
    SIXTEEN_TO_NINE(param = Pair(16,9));
}