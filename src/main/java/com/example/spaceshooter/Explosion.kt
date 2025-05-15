package com.example.spaceshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Explosion(val eX: Int, val eY: Int) {

    companion object {
        private val explosionFrames: Array<Bitmap?> = arrayOfNulls(9)

        fun preload(context: Context) {
            if (explosionFrames[0] == null) {
                for (i in 0..8) {
                    val resId = context.resources.getIdentifier(
                        "explosion$i", "drawable", context.packageName
                    )
                    explosionFrames[i] = BitmapFactory.decodeResource(context.resources, resId)
                }
            }
        }

        fun getFrame(index: Int): Bitmap? {
            return if (index in 0..8) explosionFrames[index] else null
        }

        val maxFrameIndex: Int
            get() = explosionFrames.size - 1
    }

    var explosionFrame = 0
}
