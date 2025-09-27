package com.example.ginomatt

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Delay for splash screen (2 sec)
        Handler(Looper.getMainLooper()).postDelayed({

            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                // User already logged in → go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User not logged in → go to AuthActivity
                startActivity(Intent(this, AuthActivity::class.java))
            }

            finish() // close SplashActivity so user can’t go back to it
        }, 2000) // 2000 ms = 2 sec
    }
}
