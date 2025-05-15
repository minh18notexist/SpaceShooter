package com.example.spaceshooter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class EnemySpaceShip(context: Context) {

    companion object {
        enum class EnemyLevel { NORMAL, UPGRADED, BOSS }

        private val enemyBitmaps = mutableMapOf<EnemyLevel, Bitmap>()

        fun preload(context: Context) {
            enemyBitmaps[EnemyLevel.NORMAL] =
                BitmapFactory.decodeResource(context.resources, R.drawable.rocket2)
            enemyBitmaps[EnemyLevel.UPGRADED] =
                BitmapFactory.decodeResource(context.resources, R.drawable.rocket2_alt)
            enemyBitmaps[EnemyLevel.BOSS] =
                BitmapFactory.decodeResource(context.resources, R.drawable.rocket2_boss)
        }

        var currentLevel: EnemyLevel = EnemyLevel.NORMAL

        val bitmap: Bitmap
            get() = enemyBitmaps[currentLevel]
                ?: throw IllegalStateException("Enemy bitmap not loaded")

        fun upgradeLevel(points: Int) {
            currentLevel = when {
                points >= 110 -> EnemyLevel.BOSS
                points >= 55 -> EnemyLevel.UPGRADED
                else -> EnemyLevel.NORMAL
            }
        }
    }

    var ex: Int = 0
    var ey: Int = 0
    var enemyVelocity: Int = 0

    private val random = Random()

    val enemySpaceShipWidth: Int
        get() = bitmap.width

    val enemySpaceShipHeight: Int
        get() = bitmap.height

    init {
        resetEnemySpaceShip()
    }

    fun resetEnemySpaceShip() {
        ex = 200 + random.nextInt(400)
        ey = 0
        enemyVelocity = 14 + random.nextInt(10)
    }
}
