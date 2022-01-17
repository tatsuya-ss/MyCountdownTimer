package com.example.mycountdowntimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.mycountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // innerをつけることで、外部クラスのbindingを使えるようになる
    inner class MyCountDownTimer(millisInFuture: Long,
                                 countDownInterval: Long): CountDownTimer(millisInFuture, countDownInterval) {
        var isRunning = false

        override fun onTick(p0: Long) {
            val minute = p0 / 1000L / 60L
            val second = p0 / 1000L % 60L
            binding.timerText.text = "%1d:%2$02d".format(minute, second)
        }

        override fun onFinish() {
            binding.timerText.text = "0:00"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupText()
        setupTimer()
    }

    private fun setupBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupText() {
        binding.timerText.text = "3:00"
    }

    private fun setupTimer() {
        var timer = MyCountDownTimer(3 * 60 * 1000, 100)
        binding.playStop.setOnClickListener {
            timer.isRunning = when(timer.isRunning) {
                true -> {
                    timer.cancel()
                    binding.playStop.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    false
                }
                false -> {
                    timer.start()
                    binding.playStop.setImageResource(R.drawable.ic_baseline_stop_24)
                    true
                }
            }
        }
    }

}