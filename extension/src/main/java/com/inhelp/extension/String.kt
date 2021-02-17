package com.inhelp.extension

fun String.count(subString: String): Int {
    var count = 0
    var idx = 0
    while(idx != -1) {
        idx = this.indexOf(string = subString, startIndex = idx)
        if(idx != -1){
            count++
            idx += subString.length
        }
    }
    return count
}