package com.example.mycountdowntimer

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.mycountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var soundPool: SoundPool
    private var soundResId = 0

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
            soundPool.play(soundResId, 1.0f, 100f, 0, 0, 1.0f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupText()
        setupTimer()
    }

    override fun onResume() {
        super.onResume()
        // SoundPoolのインスタンスを作成
        // runはletとwithの両方の特徴を持つ
        // 対象オブジェクトをthisで参照でき、戻り値はラムダ式の最後の値
        soundPool = SoundPool.Builder().run {

            val audioAttributes = AudioAttributes.Builder().run {
                // あえてthisを書いてみた
                this.setUsage(AudioAttributes.USAGE_ALARM)
                // 設定された属性を組み合わせた新しいAudioAttributesのインスタンスを生成する
                this.build()
            }
            setMaxStreams(1)
            setAudioAttributes(audioAttributes)
            build()
        }
        soundResId = soundPool.load(this, R.raw.bellsound, 1)
    }

    override fun onPause() {
        super.onPause()
        soundPool.release()
    }

    private fun setupBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupText() {
        binding.timerText.text = "0:05"
    }

    private fun setupTimer() {
        var timer = MyCountDownTimer(1 * 5 * 1000, 100)
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