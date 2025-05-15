package com.example.spaceshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

// Định nghĩa enum ngoài class để dễ sử dụng
enum class ShipLevel { NORMAL, UPGRADED, MAX }

class OurSpaceShip {

    companion object {
        private val shipBitmaps = mutableMapOf<ShipLevel, Bitmap>()
        var currentLevel: ShipLevel = ShipLevel.NORMAL

        fun preload(context: Context) {
            shipBitmaps[ShipLevel.NORMAL] = BitmapFactory.decodeResource(context.resources, R.drawable.rocket1)
            shipBitmaps[ShipLevel.UPGRADED] = BitmapFactory.decodeResource(context.resources, R.drawable.rocket3)
            shipBitmaps[ShipLevel.MAX] = BitmapFactory.decodeResource(context.resources, R.drawable.rocket4)
        }

        val bitmap: Bitmap
            get() = shipBitmaps[currentLevel]
                ?: throw IllegalStateException("OurSpaceShip bitmap not loaded")

        val width: Int
            get() = bitmap.width

        val height: Int
            get() = bitmap.height

        fun upgradeLevel(points: Int) {
            currentLevel = when {
                points >= 100 -> ShipLevel.MAX
                points >= 50 -> ShipLevel.UPGRADED
                else -> ShipLevel.NORMAL
            }
        }
    }

    var ox: Int = 0
    var oy: Int = 0
    var isAlive: Boolean = true
    var ourVelocity: Int = 0

    private val random = Random()

    init {
        reset()
    }

    fun reset() {
        ox = random.nextInt(SpaceShooter.screenWidth - width)
        oy = SpaceShooter.screenHeight - height
        ourVelocity = 10 + random.nextInt(6)
        isAlive = true
    }

    fun getTripleShotPositions(): List<Pair<Int, Int>> {
        return listOf(
            Pair(ox + width / 2, oy),
            Pair(ox + width / 2 - 30, oy + 10),
            Pair(ox + width / 2 + 30, oy + 10)
        )
    }
}
