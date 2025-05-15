package com.example.spaceshooter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {

    private lateinit var tvPoints: TextView
    private lateinit var tvHighScore: TextView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        tvPoints = findViewById(R.id.tvPoints)
        tvHighScore = findViewById(R.id.tvHighScore)

        // Lấy điểm hiện tại
        val points = intent.getIntExtra("points", 0)
        tvPoints.text = "Điểm của bạn: $points"

        // SharedPreferences để lấy/lưu điểm cao
        prefs = getSharedPreferences("game_data", Context.MODE_PRIVATE)
        val highScore = prefs.getInt("high_score", 0)

        if (points > highScore) {
            // Cập nhật điểm cao
            prefs.edit().putInt("high_score", points).apply()
            tvHighScore.text = "🎉 Kỷ lục mới: $points!"
        } else {
            tvHighScore.text = "Điểm cao: $highScore"
        }

        // Âm thanh thất bại
        mediaPlayer = MediaPlayer.create(this, R.raw.game_over)
        mediaPlayer.start()

        // Animation cho các thành phần
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        tvPoints.startAnimation(fadeIn)
        tvHighScore.startAnimation(fadeIn)

        // Nếu bạn dùng android:onClick trong XML thì không cần gán setOnClickListener ở đây
        // Nhưng nếu bạn muốn dùng cả hai cách, bạn có thể bỏ phần android:onClick trong XML
    }

    fun restart(view: View) {
        val intent = Intent(this, StartUp::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(view: View) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
