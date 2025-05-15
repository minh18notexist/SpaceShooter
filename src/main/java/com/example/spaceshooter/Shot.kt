package com.example.spaceshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Shot(val shx: Int, var shy: Int) {

    companion object {
        lateinit var bitmap: Bitmap

        fun preload(context: Context) {
            bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.shot)
        }

        val width: Int
            get() = bitmap.width
    }
}
