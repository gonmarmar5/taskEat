package com.example.taskeat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_splash)
        startTimer()
    }

    fun startTimer(){
        object: CountDownTimer(3000,1000){
            override fun onTick(p0: Long) {
            }
            override fun onFinish() {
                val intent = Intent(applicationContext, MainActivity::class.java).apply {}
                startActivity(intent)
            }
        }.start()
    }
}