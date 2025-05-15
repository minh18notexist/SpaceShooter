package com.example.spaceshooter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.MediaPlayer
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import java.util.*

class SpaceShooter(context: Context) : View(context) {

    private val gameContext = context

    // Bitmaps
    private val background: Bitmap = BitmapFactory.decodeResource(gameContext.resources, R.drawable.background)
    private val lifeImage: Bitmap = BitmapFactory.decodeResource(gameContext.resources, R.drawable.life)
    private val pauseIcon: Bitmap = BitmapFactory.decodeResource(gameContext.resources, R.drawable.pause_icon)
    private val resumeIcon: Bitmap = BitmapFactory.decodeResource(gameContext.resources, R.drawable.resume_icon)

    // Game objects
    private val ourSpaceShip: OurSpaceShip
    private val enemySpaceShip: EnemySpaceShip

    // Game state
    private var life = 3
    private var point = 0

    // Pause state
    private var paused = false
    private var showingPauseMenu = false

    // Paints
    private val TEXT_SIZE = 80
    private val scorePaint: Paint = Paint().apply {
        color = Color.RED
        textSize = TEXT_SIZE.toFloat()
        textAlign = Paint.Align.LEFT
    }
    private val pauseMenuPaint: Paint = Paint().apply {
        color = Color.argb(200, 0, 0, 0)
    }
    private val pauseTextPaint: Paint = Paint().apply {
        color = Color.WHITE
        textSize = 100f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    private val buttonTextPaint: Paint = Paint().apply {
        color = Color.LTGRAY
        textSize = 60f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // Handler and game loop
    private val UPDATE_MILLIS = 30L
    private val handler = Handler()
    private val runnable = Runnable { invalidate() }

    companion object {
        var screenWidth: Int = 0
        var screenHeight: Int = 0
    }

    // Logic data
    private val random = Random()
    private val enemyShots = ArrayList<Shot>()
    private val ourShots = ArrayList<Shot>()
    private val explosions = ArrayList<Explosion>()
    private var enemyShotAction = false

    // Sounds
    private val shootSound = MediaPlayer.create(context, R.raw.laser_shoot)
    private val explosionSound = MediaPlayer.create(context, R.raw.explosion_sound)
    private val pauseSound = MediaPlayer.create(context, R.raw.pause_sound)

    // Pause menu button bounds
    private val pauseButtonRect = Rect()
    private val resumeButtonRect = Rect()
    private val exitButtonRect = Rect()

    init {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y

        OurSpaceShip.preload(context)
        EnemySpaceShip.preload(context)
        Explosion.preload(context)
        Shot.preload(context)

        ourSpaceShip = OurSpaceShip()
        enemySpaceShip = EnemySpaceShip(context)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(background, 0f, 0f, null)
        canvas.drawText("Pt: $point", 0f, TEXT_SIZE.toFloat(), scorePaint)
        drawLives(canvas)
        drawPauseButton(canvas)

        if (paused && showingPauseMenu) {
            drawPauseMenu(canvas)
            return
        }

        updateEnemy(canvas)
        updatePlayer(canvas)
        updateEnemyShots(canvas)
        updatePlayerShots(canvas)
        updateExplosions(canvas)

        handler.postDelayed(runnable, UPDATE_MILLIS)
    }

    private fun drawPauseButton(canvas: Canvas) {
        val size = pauseIcon.width
        val padding = 30
        val left = screenWidth - size - padding
        val top = padding
        pauseButtonRect.set(left, top, left + size, top + size)

        if (paused) {
            canvas.drawBitmap(resumeIcon, left.toFloat(), top.toFloat(), null)
        } else {
            canvas.drawBitmap(pauseIcon, left.toFloat(), top.toFloat(), null)
        }
    }

    private fun drawPauseMenu(canvas: Canvas) {
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), pauseMenuPaint)

        canvas.drawText("Paused", (screenWidth / 2).toFloat(), (screenHeight / 3).toFloat(), pauseTextPaint)

        val btnWidth = screenWidth / 3
        val btnHeight = 150
        val centerX = screenWidth / 2

        val resumeTop = screenHeight / 2
        resumeButtonRect.set(centerX - btnWidth / 2, resumeTop, centerX + btnWidth / 2, resumeTop + btnHeight)
        canvas.drawRect(resumeButtonRect, Paint().apply { color = Color.DKGRAY })
        canvas.drawText("Resume", resumeButtonRect.centerX().toFloat(), resumeButtonRect.centerY() + 20f, buttonTextPaint)

        val exitTop = resumeTop + btnHeight + 40
        exitButtonRect.set(centerX - btnWidth / 2, exitTop, centerX + btnWidth / 2, exitTop + btnHeight)
        canvas.drawRect(exitButtonRect, Paint().apply { color = Color.DKGRAY })
        canvas.drawText("Exit", exitButtonRect.centerX().toFloat(), exitButtonRect.centerY() + 20f, buttonTextPaint)
    }
    // Vẽ số mạng hiển thị trên màn hình
    private fun drawLives(canvas: Canvas) {
        for (i in 1..life) {
            canvas.drawBitmap(
                lifeImage,
                (screenWidth - lifeImage.width * i).toFloat(),
                0f,
                null
            )
        }
    }

    // Xử lý khi nhấn nút pause: chuyển trạng thái tạm dừng
    private fun togglePause() {
        paused = !paused
        showingPauseMenu = paused
        if (paused) {
            pauseSound.start()
            if (shootSound.isPlaying) shootSound.pause()
            if (explosionSound.isPlaying) explosionSound.pause()
            handler.removeCallbacks(runnable)
        } else {
            pauseSound.start()
            if (!shootSound.isPlaying) shootSound.start()
            if (!explosionSound.isPlaying) explosionSound.start()
            handler.postDelayed(runnable, UPDATE_MILLIS)
        }
        invalidate()
    }

    // Xử lý resume game từ menu pause
    private fun resumeGame() {
        paused = false
        showingPauseMenu = false
        pauseSound.start()
        handler.postDelayed(runnable, UPDATE_MILLIS)
        invalidate()
    }

    // Xử lý thoát game từ menu pause về StartUp activity
    private fun exitGame() {
        val intent = Intent(gameContext, StartUp::class.java)
        gameContext.startActivity(intent)
        (gameContext as Activity).finish()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (pauseButtonRect.contains(touchX, touchY)) {
                    togglePause()
                    return true
                }

                if (paused && showingPauseMenu) {
                    if (resumeButtonRect.contains(touchX, touchY)) {
                        resumeGame()
                        return true
                    }
                    if (exitButtonRect.contains(touchX, touchY)) {
                        exitGame()
                        return true
                    }
                    return true
                }

                ourSpaceShip.ox = touchX
            }
            MotionEvent.ACTION_MOVE -> {
                if (!paused) {
                    ourSpaceShip.ox = touchX
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!paused) {
                    if (ourShots.size < 3) {
                        if (OurSpaceShip.currentLevel == ShipLevel.NORMAL) {
                            val shot = Shot(
                                ourSpaceShip.ox + OurSpaceShip.width / 2,
                                ourSpaceShip.oy
                            )
                            ourShots.add(shot)
                        } else {
                            val positions = ourSpaceShip.getTripleShotPositions()
                            positions.forEach { (x, y) ->
                                ourShots.add(Shot(x, y))
                            }
                        }
                        shootSound.start()
                    }
                }
            }
        }
        return true
    }

    private fun updateEnemy(canvas: Canvas) {
        if (paused) return

        EnemySpaceShip.upgradeLevel(point)

        enemySpaceShip.ex += enemySpaceShip.enemyVelocity
        if (enemySpaceShip.ex <= 0 || enemySpaceShip.ex + EnemySpaceShip.bitmap.width >= screenWidth) {
            enemySpaceShip.enemyVelocity *= -1
        }

        if (!enemyShotAction && enemySpaceShip.ex >= 200 + random.nextInt(400)) {
            val shot = Shot(
                enemySpaceShip.ex + EnemySpaceShip.bitmap.width / 2,
                enemySpaceShip.ey
            )
            enemyShots.add(shot)
            enemyShotAction = true
        }

        canvas.drawBitmap(
            EnemySpaceShip.bitmap,
            enemySpaceShip.ex.toFloat(),
            enemySpaceShip.ey.toFloat(),
            null
        )
    }

    private fun updatePlayer(canvas: Canvas) {
        if (paused) return

        OurSpaceShip.upgradeLevel(point)

        val maxX = screenWidth - OurSpaceShip.width
        if (ourSpaceShip.ox < 0) ourSpaceShip.ox = 0
        if (ourSpaceShip.ox > maxX) ourSpaceShip.ox = maxX

        canvas.drawBitmap(
            OurSpaceShip.bitmap,
            ourSpaceShip.ox.toFloat(),
            ourSpaceShip.oy.toFloat(),
            null
        )
    }

    private fun updateEnemyShots(canvas: Canvas) {
        if (paused) return

        var i = 0
        while (i < enemyShots.size) {
            val shot = enemyShots[i]
            shot.shy += 15
            canvas.drawBitmap(Shot.bitmap, shot.shx.toFloat(), shot.shy.toFloat(), null)

            if (shot.shx in ourSpaceShip.ox..(ourSpaceShip.ox + OurSpaceShip.width) &&
                shot.shy >= ourSpaceShip.oy
            ) {
                life--
                enemyShots.removeAt(i)
                explosions.add(Explosion(ourSpaceShip.ox, ourSpaceShip.oy))
                explosionSound.start()
                if (life == 0) endGame()
                continue
            } else if (shot.shy >= screenHeight) {
                enemyShots.removeAt(i)
                continue
            }
            i++
        }

        if (enemyShots.isEmpty()) enemyShotAction = false
    }

    private fun updatePlayerShots(canvas: Canvas) {
        if (paused) return

        var i = 0
        while (i < ourShots.size) {
            val shot = ourShots[i]
            shot.shy -= 15
            canvas.drawBitmap(Shot.bitmap, shot.shx.toFloat(), shot.shy.toFloat(), null)

            if (shot.shx in enemySpaceShip.ex..(enemySpaceShip.ex + EnemySpaceShip.bitmap.width) &&
                shot.shy in enemySpaceShip.ey..enemySpaceShip.enemySpaceShipHeight
            ) {
                point++
                ourShots.removeAt(i)
                explosions.add(Explosion(enemySpaceShip.ex, enemySpaceShip.ey))
                explosionSound.start()
                continue
            } else if (shot.shy <= 0) {
                ourShots.removeAt(i)
                continue
            }
            i++
        }
    }

    private fun updateExplosions(canvas: Canvas) {
        var i = 0
        while (i < explosions.size) {
            val exp = explosions[i]
            Explosion.getFrame(exp.explosionFrame)?.let {
                canvas.drawBitmap(it, exp.eX.toFloat(), exp.eY.toFloat(), null)
            }
            exp.explosionFrame++
            if (exp.explosionFrame > Explosion.maxFrameIndex) {
                explosions.removeAt(i)
                continue
            }
            i++
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        shootSound.release()
        explosionSound.release()
        pauseSound.release()
    }

    private fun endGame() {
        paused = true
        handler.removeCallbacks(runnable)

        val intent = Intent(gameContext, GameOver::class.java)
        intent.putExtra("points", point)
        gameContext.startActivity(intent)
        (gameContext as Activity).finish()
    }
}
