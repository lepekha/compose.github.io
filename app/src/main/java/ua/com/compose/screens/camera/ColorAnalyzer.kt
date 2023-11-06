package ua.com.compose.screens.camera

import android.graphics.Color
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

class ColorAnalyzer(val listener: (value: Int) -> Unit) : ImageAnalysis.Analyzer {
        private var lastTotalRed = 0
        private var lastTotalGreen = 0
        private var lastTotalBlue = 0

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }

        override fun analyze(image: ImageProxy) {
            val imageWidth = image.width
            val imageHeight = image.height

            // Задати розмір області центру
            val centerSize = 2

            // Знайти початкові координати області центру
            val centerX = imageWidth / 2 - centerSize / 2
            val centerY = imageHeight / 2 - centerSize / 2

            // Отримати пікселі зображення з ImageProxy
            val buffer = image.planes[0].buffer
            val pixels = buffer.toByteArray()

            // Зберігати суми каналів кольорів для всіх пікселів у центральній області
            var totalRed = 0
            var totalGreen = 0
            var totalBlue = 0

            // Перебрати всі пікселі у центральній області і додати значення каналів кольору до сум
            for (y in centerY until centerY + centerSize) {
                for (x in centerX until centerX + centerSize) {
                    val pixelOffset = (y * imageWidth + x) * 4
                    val red = pixels[pixelOffset].toInt() and 0xFF
                    val green = pixels[pixelOffset + 1].toInt() and 0xFF
                    val blue = pixels[pixelOffset + 2].toInt() and 0xFF

                    totalRed += red
                    totalGreen += green
                    totalBlue += blue
                }
            }

            // Обчислити середнє значення кольору для червоного, зеленого та синього каналів
            val averageRed = totalRed / (centerSize * centerSize)
            val averageGreen = totalGreen / (centerSize * centerSize)
            val averageBlue = totalBlue / (centerSize * centerSize)

            if(Math.abs(averageRed - lastTotalRed) > 3 || Math.abs(averageBlue - lastTotalBlue) > 3 || Math.abs(averageGreen - lastTotalGreen) > 3) {
                lastTotalRed = averageRed
                lastTotalGreen = averageGreen
                lastTotalBlue = averageBlue
            }
            // Створити колір із середніми значеннями каналів RGB
            val averageColor = Color.rgb(lastTotalRed, lastTotalGreen, lastTotalBlue)
            listener(averageColor)
            image.close()
        }
    }