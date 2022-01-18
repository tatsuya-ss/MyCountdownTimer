package com.example.mycountdowntimer

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.example.mycountdowntimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var soundPool: SoundPool
    private var soundResId = 0
    var timer = MyCountDownTimer(1 * 5 * 1000, 100)

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
        setupSpinner()
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

    private fun setPlayArrowButton() {
        binding.playStop.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    private fun setupTimer() {
        binding.playStop.setOnClickListener {
            timer.isRunning = when(timer.isRunning) {
                true -> {
                    timer.cancel()
                    setPlayArrowButton()
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

    private fun setupSpinner() {
        // オブジェクト式
        // object: 継承するクラス名、または実装するインターフェイス名 { クラスの定義 }
        // https://dogwood008.github.io/kotlin-web-site-ja/docs/reference/object-declarations.html
        binding.spinner.onItemSelectedListener =
            object: AdapterView.OnItemSelectedListener {

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    timer.cancel()
                    setPlayArrowButton()
                    val item = binding.spinner.selectedItem as? String
                    item?.let {
                        if (it.isNotEmpty()) binding.timerText.text = it
                        val times = it.split(":")
                        val min = times[0].toLong()
                        val sec = times[1].toLong()
                        val mil = (min * 60 + sec) * 1000
                        timer = MyCountDownTimer(mil, 100)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) { }

            }
    }

}