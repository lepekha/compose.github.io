package com.inhelp.utils

import android.content.Context
import android.graphics.BitmapFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL
import android.content.Intent
import android.net.Uri


object Utils {
    fun loadImage(urlString: String) = GlobalScope.async {
        BitmapFactory.decodeStream(URL(urlString).openConnection().getInputStream())
    }
}