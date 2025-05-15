package com.example.spaceshooter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StartUp : AppCompatActivity() {

    private lateinit var tvHighScore: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startup)

        // Lấy View
        tvHighScore = findViewById(R.id.tvHighScore)

        // Hiển thị điểm cao
        prefs = getSharedPreferences("game_data", Context.MODE_PRIVATE)
        val highScore = prefs.getInt("high_score", 0)
        tvHighScore.text = "Điểm cao: $highScore"

        // Hiệu ứng fade-in
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        findViewById<View>(R.id.btnStart).startAnimation(fadeIn)
        tvHighScore.startAnimation(fadeIn)

        // Âm thanh khi nhấn nút
        mediaPlayer = MediaPlayer.create(this, R.raw.button_click)
    }

    // Hàm xử lý khi nhấn nút "Chơi ngay" (ImageView hoặc TextView)
    fun startGame(view: View) {
        mediaPlayer.start()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
