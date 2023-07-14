package com.zattech.zatgpt

import android.content.Intent
import android.os.Build.VERSION_CODES.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zattech.zatgpt.R
import android.os.Handler


class SplashActivity : AppCompatActivity()

{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Apply the splash theme
//        setTheme(R.style.Theme_ZatGPT_Splash)
        setContentView(com.zattech.zatgpt.R.layout.activity_splash)


        Handler(mainLooper).postDelayed({
                                        startActivity(Intent(this,MainActivity::class.java))
            finish()
        },1500)

    }
}